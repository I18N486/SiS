package com.iflytek.zst.dictationlibrary.audio;

import android.content.Context;
import android.util.Log;

/**
 * Created by jiwang on 2018/12/4.
 * 备注:当使用第三方录音传入时,设置此录音工具
 */
class AudioRecordNoneRecord implements IAudioRecord {
    private static final String TAG = "AudioRecordNoneRecord";
    public AudioRecordNoneRecord(Context context) {
    }

    @Override
    public void startRecord(RecordCallback recordCallback) {
        Log.d(TAG, "startRecord() called 当前录音工具不会返回音频数据");
    }

    @Override
    public void stopRecord() {
        Log.d(TAG, "stopRecord() called 当前录音工具不会返回音频数据");
    }
}
