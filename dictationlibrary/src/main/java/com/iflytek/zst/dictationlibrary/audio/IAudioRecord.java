package com.iflytek.zst.dictationlibrary.audio;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:录音工具接口
 */
public interface IAudioRecord {

    void startRecord(RecordCallback recordCallback);

    void stopRecord();

}
