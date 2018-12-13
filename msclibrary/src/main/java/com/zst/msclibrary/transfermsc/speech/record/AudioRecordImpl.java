package com.zst.msclibrary.transfermsc.speech.record;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.iflytek.aipsdk.audio.AudioHelper;
import com.iflytek.aipsdk.audio.AudioListener;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:录音工具实现类
 */
public class AudioRecordImpl implements IAudioRecord {
    AudioHelper mAudioHelper;
    private Handler mHandler;
    /**
     * 打开VAD检测 会丢失掉静音音频
     * public static final String VAD_RES = "aue=raw,vad_res=meta_vad_16k.jet,res=0";
     * 不打开VAD检测
     */
    public static final String VAD_RES = "aue=raw";

    public AudioRecordImpl(final Context context) {
        HandlerThread audioHandlerTHread = new HandlerThread("audioHandlerThread");
        audioHandlerTHread.start();
        mHandler = new Handler(audioHandlerTHread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAudioHelper = new AudioHelper(context);
            }
        });

    }

    @Override
    public void startRecord(final RecordCallback recordCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAudioHelper.startRecord(VAD_RES, new AudioListener() {
                    @Override
                    public void onRecordBuffer(byte[] bytes, int i) {
                        if (recordCallback != null) {
                            recordCallback.onRecord(bytes);
                        }
                    }

                    @Override
                    public void onError(int i) {
                        if (recordCallback != null) {
                            recordCallback.onError(i);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void stopRecord() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAudioHelper != null) {
                    mAudioHelper.stopRecord();
                }
            }
        });
    }
}
