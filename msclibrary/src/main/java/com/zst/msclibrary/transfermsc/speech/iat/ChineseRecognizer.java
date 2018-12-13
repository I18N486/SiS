package com.zst.msclibrary.transfermsc.speech.iat;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.zst.msclibrary.Constants;
import com.zst.msclibrary.impl.TransferResultListener;
import com.zst.msclibrary.transfermsc.openapi.TransferCenter;
import com.zst.msclibrary.transfermsc.openapi.consts.TransferResultType;
import com.zst.msclibrary.transfermsc.openapi.entity.LatticeData;
import com.zst.msclibrary.transfermsc.openapi.entity.TransferResponse;
import com.zst.msclibrary.transfermsc.openapi.exception.TransferException;
import com.zst.msclibrary.transfermsc.openapi.handler.AbstractTransferHandler;
import com.zst.msclibrary.transfermsc.speech.msg.SentenceResultEvent;
import com.zst.msclibrary.transfermsc.speech.record.ByteRingBuffer;
import com.zst.msclibrary.transfermsc.transfer.MeetingTransfer;
import com.zst.msclibrary.transfermsc.transfer.TransferBean;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author changwu
 */
public class ChineseRecognizer extends BaseRecognizer {
    private static final String TAG = "RecognizerTranslate";
    private static DateFormat format5 = new SimpleDateFormat("yyyyMMddhhmmss");

    private String mFileDir;

    /**
     * 转写的Session ID
     */
    private String transferId = "";

    /**
     * 原文的结果
     */
    private String mOrgResult = "";

    /**
     * 翻译接口
     */
    private MeetingTransfer meetingTransfer;
    /**
     * 翻译结果
     */
    private String mTransResult = "";

    /**
     * 录音文件保存的名称
     */
    private String mFileName = "";

    private Handler mHandler;

    /**
     * 当前的讲话状态
     */
    public boolean isSpeaking = false;

    /**
     * 分段处理接口
     */
    private SegemProcess segemProcess;

    /**
     * 翻译的消息队列
     */
    private LinkedTransferQueue<TransEvent> transferQueue = new LinkedTransferQueue<>();


    private String srcLang;
    private String targetLang;
    private String appId;
    private boolean openRecord;
    private ByteRingBuffer mByteRingBuffer;
    private ByteBuffer mByteBuffer;

    private TransferResultListener transferResultListener;

    public void setTranslateParams(String srcLang, String targetLang, String appId, String fileDir) {
        this.srcLang = srcLang;
        this.targetLang = targetLang;
        this.appId = appId;
        mFileDir = fileDir;
    }

    public void setTranslateParams(String srcLang, String targetLang) {
        if (!TextUtils.isEmpty(srcLang)) {
            this.srcLang = srcLang;
        }
        if (!TextUtils.isEmpty(targetLang)) {
            this.targetLang = targetLang;
        }
    }

    public ChineseRecognizer(Context context, boolean openRecord) {
        super(context);
        this.openRecord = openRecord;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            this.mFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            this.mFileDir = Environment.getDataDirectory().getAbsolutePath();
        }
        mHandler = new TransHandler(Looper.getMainLooper());
        segemProcess = new SegemProcess(this);
        //翻译
        meetingTransfer = new MeetingTransfer();
        mByteRingBuffer=new ByteRingBuffer();
        mByteBuffer = ByteBuffer.allocate(AUDIO_FRAME_DATA_LENGTH);
    }

    /**
     * 开始讲话
     */
    public void startSpeak(TransferResultListener transferResultListener) {
        if (isSpeaking) {
            return;
        }
        this.transferResultListener = transferResultListener;
        if (openRecord) {
            startRecord();
        }
        transferThread();

        //转写引擎的连接
        TransferResponse response = TransferCenter.start(TransferResultType.SENTENCE, transferHandler);
        if (!response.isSuccess()) {
            transferResultListener.onError(response.getCode(), response.getDesc());
            return;
        }
        transferId = response.getTransferId();
        isSpeaking = true;
        mHandler.sendEmptyMessage(START_SPEAK);
    }

    public void writeAudio(byte[] audioBuffer) {
        sendBytes(audioBuffer);
    }

    /**
     * 结束讲话
     */
    public void endSpeak() {
        TransferResponse response = TransferCenter.end(transferId);
        if (response.isSuccess()) {
            transferEnd();
        } else {
            transferError(response.getDesc());
        }
        if (openRecord) {
            stopRecord();
        }
        if (mByteRingBuffer != null) {
            mByteRingBuffer.clear();
        }
    }

    /**
     * 转写回调通知
     */
    public AbstractTransferHandler transferHandler = new AbstractTransferHandler() {
        @Override
        public void handleTransferResult(String transferId, LatticeData latticeData) {
            Message msg = Message.obtain();
            msg.what = SPEAK_RESULT;
            msg.obj = latticeData;
            mHandler.sendMessage(msg);
        }

        @Override
        public void handleTransferEnd(String transferId) {
            Log.v(TAG, "handleTransferEnd ");
            mHandler.sendEmptyMessage(TRANSFER_END);
        }

        @Override
        public void handleTransferError(String transferId, TransferException exception) {
            Log.v(TAG, "handleTransferError exception = " + exception.getMessage());
            Message msg = Message.obtain();
            msg.what = TRANSFER_ERROR;
            msg.obj = exception.getMessage();
            mHandler.sendMessage(msg);
        }
    };

    public static final int START_SPEAK = 0x01;     // 开始讲话
    public static final int SPEAK_RESULT = 0x02;        //识别结果
    public static final int END_SPEAK = 0x03;         //结束讲话

    public static final int SPEAK_TIMEOUT = 0x05;
    public static final int TRANSFER_END = 0x06;
    public static final int TRANSFER_ERROR = 0x07;
    public static final int SEGEM_RECORD = 0x08;
    private static final int TRANSER_RESULT = 0x09;

    public class TransHandler extends Handler {
        /**
         * Default constructor associates this handler with the {@link Looper} for the
         * current thread.
         * <p>
         * If this thread does not have a looper, this handler won't be able to receive messages
         * so an exception is thrown.
         */
        public TransHandler(Looper looper) {
            super(looper);
        }

        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_SPEAK:
                    segemProcess.initParam();
                    setIsSendData(true);
                    break;

                case END_SPEAK:
                    TransferResponse response = TransferCenter.end(transferId);
                    if (response.isSuccess()) {
                        transferEnd();
                    } else {
                        transferError(response.getDesc());
                    }
                    break;

                case SPEAK_RESULT:
                    segemProcess.processResult((LatticeData) msg.obj);
                    break;

                case SPEAK_TIMEOUT:
                    segemProcess.onTimeOut();
                    break;

                case SEGEM_RECORD:

                    break;

                case TRANSFER_END:
                    transferEnd();
                    break;

                case TRANSFER_ERROR:
                    String error = (String) msg.obj;
                    transferError(error);
                    break;
                case TRANSER_RESULT:
                    SentenceResultEvent sentenceResult = (SentenceResultEvent) msg.obj;
                    onMscResults(sentenceResult);
                    break;
                default:
            }
        }
    }

    private void onMscResults(SentenceResultEvent sentenceResult) {
        if (transferResultListener == null || sentenceResult == null) {
            return;
        }
//        transferResultListener.onMscResults(sentenceResult);
        if (sentenceResult.type.equals(Constants.RECOGNIZER_RESULT)) {
            transferResultListener.onSentenceEnd(sentenceResult.result, false);
        } else {
            transferResultListener.onSentenceUpdate(sentenceResult.result, 0, false);
        }
        transferResultListener.onTransSucess(sentenceResult.transResult, Integer.parseInt(sentenceResult.type));
    }

    @Override
    public void onSpeakTimeout() {
        mHandler.sendEmptyMessage(SPEAK_TIMEOUT);
    }

    @Override
    public void sendBytes(byte[] data) {
        mByteRingBuffer.put(data, 0, data.length);
        if (mByteRingBuffer.getBusySize() >= AUDIO_FRAME_DATA_LENGTH) {
            mByteRingBuffer.get(mByteBuffer, 0, AUDIO_FRAME_DATA_LENGTH);
            if (mFileDir != null) {
                super.sendBytes(mByteBuffer.array());
            }
            if (!TransferCenter.sendBytes(transferId, mByteBuffer.array())) {
                Log.v(TAG, "Send error");
            }
        }
    }

    @Override
    public void notifySpeakResult(Object object) {
    }

    /**
     * 识别结果通知
     *
     * @param object
     */
    @Override
    public void onSentenceReuslt(Object object) {
        transferQueue.add(new TransEvent(EVENT_SPEAK_TRANS, (SentenceResultEvent) object, null));
    }


    /**
     * 翻译的消息处理
     */
    private void transferThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransferBean testBean = new TransferBean();
                testBean.setAppid("5ae146f9");
                testBean.setSn(Build.SERIAL);
                testBean.setFrom(srcLang);
                testBean.setLatitude("123.23");
                testBean.setLongitude("132.519");
                String result = "";
                TransEvent event = null;
                SentenceResultEvent msg = null;
                while (true) {
                    testBean.setTo(targetLang);
                    try {
                        event = transferQueue.poll();
                        if (event == null) {
                            continue;
                        }
                        //翻译请求
                        if (event.eventId == EVENT_SPEAK_TRANS) {
                            msg = event.msg;
                            if (!TextUtils.isEmpty(msg.result)) {
                                testBean.setSrcStr(msg.result);
                                result = meetingTransfer.txtTrans(testBean, null);      //同步接口
                            } else {                    //文本为空，不处理
                                continue;
                            }
                            msg.transResult = result;
                            Message message = new Message();
                            message.what = TRANSER_RESULT;
                            message.obj = msg;
                            mHandler.sendMessage(message);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }


    /**
     * 语音分段处理
     */
    @Override
    public void segmenRecord() {
        transferQueue.add(new TransEvent(EVENT_UPLOAD, null, mFileName));   //上传录音文件和翻译结果
        mFileName = format5.format(new Date());
        createFile(mFileDir, mFileName);      //重新创建文件.,并进行录音
    }


    /**
     * 转写结束
     */
    public void transferEnd() {
        if (isSpeaking) {
            segemProcess.processEnd();
            isSpeaking = false;
            setIsSendData(false);
        }
    }

    /**
     * 转写出错
     *
     * @param desc
     */
    public void transferError(String desc) {
        if (isSpeaking) {
            segemProcess.processEnd();
            isSpeaking = false;
            stopRecord();
        }
    }


    public static final int EVENT_SPEAK_TRANS = 0x01;           //文本翻译
    public static final int EVENT_UPLOAD = 0x02;                //文本上传
    public static final int EVENT_SPEAK_TRANS_END = 0x03;       //文本结束

    /**
     * 翻译的消息定义
     */
    public class TransEvent {
        int eventId;
        SentenceResultEvent msg;
        String fileName;

        public TransEvent(int id, SentenceResultEvent msg, String filename) {
            this.eventId = id;
            this.msg = msg;
            this.fileName = filename;
        }
    }

}
