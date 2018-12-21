package com.iflytek.zst.dictationlibrary.audio;

import android.content.Context;

/**
 * Created by jiwang on 2018/11/12.
 * 备注:
 */
public class AudioFactory {
    public static final int AUDIORECORD_NONE = 0;
    public static final int AUDIORECORD_AI = 1;
    public static final int AUDIORECORD_DENOISE = 2;


    /**
     * 获取录音工具
     *
     * @param context
     * @param useDenoise true 使用降噪,false 不使用降噪
     * @return
     */
    public static IAudioRecord getAudioRecord(Context context, boolean useDenoise) {
        if (useDenoise) {
            //return new NoiseAudioRecord(context);
            return null;
        }else{
            return new AudioRecordImpl(context);
        }
    }
    /**
     * 获取录音工具
     *
     * @param context
     * @param audioRecordType 0 不使用录音工具 1 不使用降噪,2 使用降噪,
     * @return
     */
    public static IAudioRecord getAudioRecord(Context context, int audioRecordType) {
        IAudioRecord audioRecord;
        switch (audioRecordType) {
            case AUDIORECORD_NONE:
                audioRecord = new AudioRecordNoneRecord(context);
                break;
            case AUDIORECORD_AI:
                audioRecord = new AudioRecordImpl(context);
                break;
            case AUDIORECORD_DENOISE:
                //audioRecord = new NoiseAudioRecord(context);
                audioRecord = null;
                break;
            default:
                audioRecord = new AudioRecordImpl(context);
                break;
        }
        return audioRecord;
    }
}
