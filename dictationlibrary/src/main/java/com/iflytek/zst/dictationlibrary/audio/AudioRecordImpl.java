package com.iflytek.zst.dictationlibrary.audio;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.iflytek.zst.dictationlibrary.impl.AudioListener;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:录音工具实现类
 */
public class AudioRecordImpl implements IAudioRecord {
    AudioHelper mAudioHelper;
    private Handler mHandler;

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
                mAudioHelper.startRecord("", new AudioListener() {
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
        if (mAudioHelper != null) {
            mAudioHelper.stopRecord();
        }
    }
}
