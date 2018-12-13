package com.zst.msclibrary.transfermsc.speech.iat;

import android.content.Context;
import android.util.Log;

import com.zst.msclibrary.transfermsc.speech.record.AudioRecordImpl;
import com.zst.msclibrary.transfermsc.speech.record.IAudioRecord;
import com.zst.msclibrary.transfermsc.speech.record.RecordCallback;

import java.io.File;
import java.io.FileOutputStream;

public class BaseRecognizer {
    public static final String TAG = "BaseRecognizer";
    // private AudioHelper helper;
    private IAudioRecord mIAudioRecord;
    /**
     * 打开VAD检测 会丢失掉静音音频
     * public static final String VAD_RES = "aue=raw,vad_res=meta_vad_16k.jet,res=0";
     * 不打开VAD检测
     */
    public static final String VAD_RES = "aue=raw";

    public static final int AUDIO_FRAME_DATA_LENGTH = 1280;

    private Context mContext;

    /**
     * 录音文件
     */
    private FileOutputStream fileOutputStream = null;
    Object lock = new Object();

    /**
     * 是否获取录音
     */
    private boolean isSendData;

    public BaseRecognizer(Context context) {
        this.mContext = context;
        mIAudioRecord=new AudioRecordImpl(context);
    }

    public void setIAudioRecord(IAudioRecord iAudioRecord) {
        this.mIAudioRecord = iAudioRecord;
    }


    /**
     * 录音处理
     */
    public void startRecord() {
        mIAudioRecord.startRecord(new RecordCallback() {
            @Override
            public void onRecord(byte[] audio) {
                sendBytes(audio);
            }

            @Override
            public void onError(int code) {
                Log.e("WebSocketManager", "record error");
            }
        });
    }

    /**
     * @param isSend
     */
    protected void setIsSendData(boolean isSend) {
        this.isSendData = isSend;
    }

    public void createFile(String fileDir, String fileName) {
        synchronized (lock) {
            if (null != fileName) {
                try {
                    File dir = new File(fileDir);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    //保存录音文件
                    File file = new File(fileDir, fileName + ".pcm");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    Log.v("BaseRecognizer", "createFile file path = " + file.getPath());
                    fileOutputStream = new FileOutputStream(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param data
     */
    public void sendBytes(byte[] data) {
        //录音数据定入文件
        synchronized (lock) {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.write(data, 0, data.length);
                }
            } catch (Exception e) {

            }
        }
    }

    public void closeFile() {
        synchronized (lock) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileOutputStream = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 停止录音
     */
    protected void stopRecord() {
        if (null != mIAudioRecord) {
            mIAudioRecord.stopRecord();
        }

    }

    public void onSpeakTimeout() {
//        mHandler.sendEmptyMessage(SPEAK_TIMEOUT);
    }

    public void segmenRecord() {
        //语音分段
    }

    public void notifySpeakResult(Object object) {
        //完整的句子
    }

    public void onSentenceReuslt(Object object) {
        //中间的句子和完整的句子
    }

}
