package com.iflytek.zst.dictationlibrary.online;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.zst.dictationlibrary.audio.AudioFactory;
import com.iflytek.zst.dictationlibrary.audio.AudioSimpleQueueManager;
import com.iflytek.zst.dictationlibrary.audio.AudioStreamImpl;
import com.iflytek.zst.dictationlibrary.audio.IAudioManager;
import com.iflytek.zst.dictationlibrary.audio.IAudioRecord;
import com.iflytek.zst.dictationlibrary.audio.RecordCallback;
import com.iflytek.zst.dictationlibrary.bean.FormatNormalBean;
import com.iflytek.zst.dictationlibrary.bean.FormatResultBean;
import com.iflytek.zst.dictationlibrary.bean.NormalResultBean;
import com.iflytek.zst.dictationlibrary.impl.DictationResultListener;
import com.iflytek.zst.dictationlibrary.online.transtask.TaskQueue;
import com.iflytek.zst.dictationlibrary.utils.DictationResultFormat;
import com.iflytek.zst.dictationlibrary.utils.MyLogUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by DELL-5490 on 2018/12/17.
 */

public class RecognizerEngine {
    private static final String TAG = "RecognizerEngine";
    private static String TRANSAPPID;

    private static SpeechRecognizer mAsr;

    private String sourceLanguage = "cn";
    private String targetLanguage = "en";
    private DictationResultListener resultListener = null;
    private boolean isRunning = false;
    private boolean isLongDictaion = false;
    private Thread writeAudioThread;
    private ExecutorService executorService;
    private TaskQueue<TransTask> mTransTaskTaskQueue;
    private IAudioRecord iAudioRecord;
    private IAudioManager audioManager;

    private RecognizerEngine(){
        executorService = Executors.newFixedThreadPool(1);
        mTransTaskTaskQueue = new TaskQueue<>(1);
    }
    private static class SingletonHolder{
        public static final RecognizerEngine instance = new RecognizerEngine();
    }

    public void create(Context context,boolean useDoNoise){
        create(AudioFactory.getAudioRecord(context,useDoNoise));
    }

    public void create(Context context,int audioRecordType){
        create(AudioFactory.getAudioRecord(context,audioRecordType));
    }

    public void create(IAudioRecord iAudioRecord){
        create(iAudioRecord,new AudioSimpleQueueManager(new AudioStreamImpl(new LinkedBlockingDeque<byte[]>())));
    }

    public void create(IAudioRecord iAudioRecord,IAudioManager audioManager){
        this.iAudioRecord = iAudioRecord;
        this.audioManager = audioManager;
    }

    /**
     * 获取单例
     * @return
     */
    public static RecognizerEngine getInstance(){
        return SingletonHolder.instance;
    }


    /**
     * 初始化监听器
     */
    private static InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            MyLogUtils.d(TAG,"oninit: "+i);
            if (i != ErrorCode.SUCCESS) {
                MyLogUtils.e(TAG,"初始化失败，错误码：" + i);
            }
        }
    };

    /**
     * 初始化
     * @param context
     * @param appId 识别appid
     * @param transAppID  翻译appid
     */
    public static void init(Context context,String appId,String transAppID){
        SpeechUtility.createUtility(context, "appid=" + appId);
        mAsr = SpeechRecognizer.createRecognizer(context,mInitListener);
        TRANSAPPID = transAppID;
    }


    /**
     * 设置源语种（识别），该方法只能在引擎开始识别之前设置生效
     * @param source
     */
    public void setSourceLanguage(String source){
        sourceLanguage = source;
    }


    /**
     * 设置目标语种（翻译）,使用webapi翻译时该方法即时生效
     * @param target
     */
    public void setTargetLanguage(String target){
        targetLanguage = target;
    }


    /**
     * 默认参数设置
     */
    private void setParam(String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"setParam error,the masr is null");
            return;
        }
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎类型（TYPE_CLOUD 在线引擎；TYPE_LOCAL 离线引擎）
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

        //设置音频源(不设置则默认是1，代表sdk使用mediarecorder进行录音识别；-1，使用音频源，配合写音频方式；
        // -2，读取音频文件方式识别，传入ASR_SOURCE_PATH路径；-3，使用 AIMIC 方式( -3 )下（仅唤醒下有效），SDK通过麦克风硬件录音)
        mAsr.setParameter(SpeechConstant.AUDIO_SOURCE,"1");

        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mAsr.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mAsr.setParameter(SpeechConstant.VAD_EOS, "10000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mAsr.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置语言（取值 “zh_cn”、“es_en”）,默认值zh_cn
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        if (filePath != null && !"".equals(filePath.trim())) {
            if (filePath.endsWith("wav")){
                mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            } else if (filePath.endsWith("pcm")){
                mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"pcm");
            } else {
                MyLogUtils.e(TAG,"the audio file path is error,Only PCM and wav formats are supported!");
            }
            mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, filePath);
        }
    }


    /**
     * 识别内容送去翻译
     * @param formatResultBean
     */
    private void addTextToTransTask(FormatResultBean formatResultBean){
        if (TextUtils.isEmpty(formatResultBean.content)){
            //空字串拦截
            return;
        }
        TransTask transTask = mTransTaskTaskQueue.obtainTask();
        if (transTask == null){
            transTask = new TransTask();
        }
        transTask.setData(TRANSAPPID,sourceLanguage,targetLanguage, formatResultBean,resultListener);
        mTransTaskTaskQueue.addTask(transTask);
    }

    /**
     * 通用识别，带pgs效果，使用sdk默认录音（当前引擎不支持长听写，最长1分钟后自动停止识别转写）
     * @param filePath 音频文件保存全路径（设置为null则不保存）
     * @param dictationResultListener 信息回调
     */
    public void startRecognWithPgs(DictationResultListener dictationResultListener,String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"startRecognWithPgs error,the masr is null");
            return;
        }
        //开始运行翻译线程
        if (mTransTaskTaskQueue != null) mTransTaskTaskQueue.start();

        resultListener = dictationResultListener;
        //开始识别之前先设置引擎参数
        setParam(filePath);
        //开启pgs功能，实时转写
        mAsr.setParameter(SpeechConstant.ASR_DWA, "wpgs");
        if (!mAsr.isListening()) {
            isRunning = true;
            int retCode = mAsr.startListening(new RecognizerListener(){

                @Override
                public void onVolumeChanged(int i, byte[] bytes) {
                    MyLogUtils.d(TAG,"onVolumeChanged "+ i);
                    if (resultListener != null){
                        resultListener.onAudioBytes(bytes);
                    }
                }

                @Override
                public void onBeginOfSpeech() {
                    MyLogUtils.d(TAG,"onBeginOfSpeech");
                    if (resultListener != null){
                        resultListener.onStartSpeech();
                    }
                }

                @Override
                public void onEndOfSpeech() {
                    MyLogUtils.d(TAG,"onEndOfSpeech");
                    if (resultListener != null){
                        resultListener.onEndSpeech();
                    }
                }

                @Override
                public void onResult(RecognizerResult recognizerResult, boolean b) {
                    MyLogUtils.d(TAG,"原始识别结果："+recognizerResult.getResultString());
                    FormatResultBean formatResultBean = DictationResultFormat.formatPgsIatResult(recognizerResult.getResultString());
                    MyLogUtils.d(TAG,"数据格式化后："+ formatResultBean.toString());
                    if (resultListener != null) {
                        resultListener.onSentenceResult(formatResultBean);
                        addTextToTransTask(formatResultBean);
                    }
                }

                @Override
                public void onError(SpeechError speechError) {
                    MyLogUtils.e(TAG,"recognize error,msg: "+speechError.toString());
                    if (resultListener != null) {
                        resultListener.onError(speechError.getErrorCode());
                    }
                }

                @Override
                public void onEvent(int i, int i1, int i2, Bundle bundle) {

                }
            });
            if (retCode != ErrorCode.SUCCESS) {
                isRunning = false;
                MyLogUtils.e(TAG, "听写识别失败，错误码：" + retCode);
            }
        } else {
            //引擎已经在识别状态
            MyLogUtils.d(TAG,"当前引擎已处于识别状态，无需重复开启");
        }
    }


    /**
     * 开启自定义录音长听写
     * @param resultListener
     * @param filePath
     */
    public void startRecognize(final DictationResultListener resultListener, final String filePath){
        isLongDictaion = true;
        if (iAudioRecord != null){
            iAudioRecord.startRecord(new RecordCallback() {
                @Override
                public void onRecord(byte[] audio) {
                    addToAudioQueue(audio);
                }

                @Override
                public void onError(int code) {

                }
            });
        }
        audioManager.startAddQueue(1280);
        writeAudioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startWriteAudio(resultListener,filePath);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        writeAudioThread.start();
    }

    /**
     * 向音频队列中添加音频数据
     *
     * @param bytes
     */
    public void addToAudioQueue(byte[] bytes) {
        if (audioManager != null) {
            try {
                audioManager.addToAudioQueue(bytes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始写入音频
     */
    public synchronized void startWriteAudio(DictationResultListener resultListener,String filePath) throws InterruptedException {
        startRecognWithPgsByCustomAudio(resultListener,filePath);
        while (isLongDictaion) {
            while (!isRunning() && isLongDictaion) {
                startRecognWithPgsByCustomAudio(resultListener,filePath);
                MyLogUtils.d(TAG, "startWriteAudio() called 开启识别");
                //session begin 等待时间
                Thread.sleep(40);
            }

            byte[] bytes = audioManager.getAudioBytes();
            writeAudio(bytes, 0, bytes.length);
            if (resultListener != null) {
                resultListener.onAudioBytes(bytes);
            }
        }
    }

    /**
     * 通用识别，使用自定义录音，通过队列和不停的重启组成长听写
     * @param dictationResultListener
     * @param filePath
     */
    public void startRecognWithPgsByCustomAudio(DictationResultListener dictationResultListener,String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"startRecognWithPgs error,the masr is null");
            return;
        }
        //开始运行翻译线程
        if (mTransTaskTaskQueue != null) mTransTaskTaskQueue.start();

        resultListener = dictationResultListener;
        //开始识别之前先设置引擎参数
        setParam(filePath);
        //设置音频源（writeaudio传入）
        mAsr.setParameter(SpeechConstant.AUDIO_SOURCE,"-1");
        //开启pgs功能，实时转写
        mAsr.setParameter(SpeechConstant.ASR_DWA, "wpgs");
        //重置前端点（10s）
        mAsr.setParameter(SpeechConstant.VAD_BOS, "10000");
        //重置后端点（1.2s）
        mAsr.setParameter(SpeechConstant.VAD_EOS, "1200");
        if (!mAsr.isListening()) {
            isRunning = true;
            int retCode = mAsr.startListening(new RecognizerListener(){

                @Override
                public void onVolumeChanged(int i, byte[] bytes) {
                    MyLogUtils.d(TAG,"onVolumeChanged "+ i);
                    if (resultListener != null){
                        resultListener.onAudioBytes(bytes);
                    }
                }

                @Override
                public void onBeginOfSpeech() {
                    MyLogUtils.d(TAG,"onBeginOfSpeech");
                    if (resultListener != null){
                        resultListener.onStartSpeech();
                    }
                }

                @Override
                public void onEndOfSpeech() {
                    MyLogUtils.d(TAG,"onEndOfSpeech");
                    if (resultListener != null){
                        resultListener.onEndSpeech();
                    }
                }

                @Override
                public void onResult(RecognizerResult recognizerResult, boolean b) {
                    MyLogUtils.d(TAG,"原始识别结果："+recognizerResult.getResultString());
                    FormatResultBean formatResultBean = DictationResultFormat.formatPgsIatResult(recognizerResult.getResultString());
                    MyLogUtils.d(TAG,"数据格式化后："+ formatResultBean.toString());
                    if (resultListener != null) {
                        resultListener.onSentenceResult(formatResultBean);
                        addTextToTransTask(formatResultBean);
                    }
                }

                @Override
                public void onError(SpeechError speechError) {
                    MyLogUtils.e(TAG,"recognize error,msg: "+speechError.toString());
                    if (resultListener != null) {
                        resultListener.onError(speechError.getErrorCode());
                    }
                }

                @Override
                public void onEvent(int i, int i1, int i2, Bundle bundle) {

                }
            });
            if (retCode != ErrorCode.SUCCESS) {
                isRunning = false;
                MyLogUtils.e(TAG, "听写识别失败，错误码：" + retCode);
            }
        } else {
            //引擎已经在识别状态
            MyLogUtils.d(TAG,"当前引擎已处于识别状态，无需重复开启");
        }
    }


    /**
     * 停止自定义录音长听写
     * @param handler
     * @param what
     */
    public void stopRecognizer(Handler handler,int what){
        this.isLongDictaion = false;
        if (iAudioRecord != null){
            iAudioRecord.stopRecord();
        }
        if (writeAudioThread != null && !writeAudioThread.isInterrupted()){
            writeAudioThread.interrupt();
        }
        audioManager.stopAddQueue();
        stopRecogn(handler,what);
    }


    /**
     * 向引擎写入音频数据（调用该方法之前必须先调用startlistening）
     * @param byteBuffer  要写入的录音数据缓存
     * @param offset  实际音频在传入缓存的起始点
     * @param length  音频数据长度
     */
    public void writeAudio(byte[] byteBuffer,int offset,int length){
        if (mAsr != null){
            mAsr.writeAudio(byteBuffer,offset,length);
        }
    }

    /**
     * 通用识别，不带pgs效果，使用sdk默认录音，不支持长听写
     * @param dictationResultListener 信息回调
     * @param filePath 音频文件保存全路径（设置为null则不保存）
     */
    public void startRecognNoPgs(DictationResultListener dictationResultListener,String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"startRecognNoPgs error,the masr is null");
            return;
        }
        resultListener = dictationResultListener;
        //开始识别之前先设置引擎参数
        setParam(filePath);
        //启用翻译(！！！注意启用同步翻译时不可开启pgs功能)
        mAsr.setParameter(SpeechConstant.ASR_SCH,"1");
        //翻译通道
        mAsr.setParameter(SpeechConstant.ADD_CAP,"translate");
        //设置原始语种
        mAsr.setParameter( SpeechConstant.ORI_LANG, sourceLanguage );
        //设置目标语种
        mAsr.setParameter( SpeechConstant.TRANS_LANG, targetLanguage );
        if (!mAsr.isListening()) {
            isRunning = true;
            int retCode = mAsr.startListening(new RecognizerListener() {
                @Override
                public void onVolumeChanged(int i, byte[] bytes) {
                    MyLogUtils.d(TAG,"onVolumeChanged "+ i);
                    if (resultListener != null){
                        resultListener.onAudioBytes(bytes);
                    }
                }

                @Override
                public void onBeginOfSpeech() {
                    MyLogUtils.d(TAG,"onBeginOfSpeech");
                    if (resultListener != null){
                        resultListener.onStartSpeech();
                    }
                }

                @Override
                public void onEndOfSpeech() {
                    MyLogUtils.d(TAG,"onEndOfSpeech");
                    if (resultListener != null){
                        resultListener.onEndSpeech();
                    }
                }

                @Override
                public void onResult(RecognizerResult recognizerResult, boolean b) {
                    MyLogUtils.d(TAG,"原始识别结果："+recognizerResult.getResultString());
                    FormatNormalBean formatNormalBean = DictationResultFormat
                            .formatNormalIatResult(recognizerResult.getResultString());
                    resultListener.onNoPgsResult(formatNormalBean);
                }

                @Override
                public void onError(SpeechError speechError) {
                    MyLogUtils.e(TAG,"recognize error,msg: "+speechError.toString());
                    if (resultListener != null) {
                        resultListener.onError(speechError.getErrorCode());
                    }
                }

                @Override
                public void onEvent(int i, int i1, int i2, Bundle bundle) {

                }
            });
            if (retCode != ErrorCode.SUCCESS) {
                isRunning = false;
                MyLogUtils.e(TAG, "听写识别失败，错误码：" + retCode);
            }
        } else {
            //引擎已经在识别状态
            MyLogUtils.d(TAG,"当前引擎已处于识别状态，无需重复开启");
        }
    }


    /**
     * 引擎是否在运行
     * @return
     */
    public boolean isRunning(){
        return isRunning && mAsr!= null && mAsr.isListening();
    }


    /**
     * 停止听写，该方法保证所有识别信息返回
     * @param handler 发送信息，告知主线程引擎已完成停止
     * @param what msg.what
     */
    public void stopRecogn(final Handler handler, final int what){
        if (mAsr == null){
            return;
        }
        //该方法调用之后需要过一段时间引擎才会完成结束，所以需要在子线程中轮询引擎状态，保证引擎的所有消息完全返回，状态一致
        mAsr.stopListening();
        if (executorService != null){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if (mAsr.isListening()){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isRunning = false;
                            handler.sendEmptyMessage(what);
                            break;
                        }
                    }
                }
            });
        }
    }


    /**
     * 停止听写，该方法会立即停止识别，后续消息立即停止返回
     */
    public void cancelRecogn(){
        if (mAsr == null){
            return;
        }
        mAsr.cancel();
    }

}
