package com.iflytek.zst.dictationlibrary.audio;

/**
 * Created by jiwang on 2018/10/12.
 * 备注:音频流写入和读取的方法
 */
public interface IAudioStream {
    /**
     * 获取音频流的方法
     */
    byte[] getAudioBytes() throws InterruptedException;

    /**
     * 往队列里面放置音频队列的方法
     *
     * @param bytes
     */
    void putAudioBytes(byte[] bytes) throws InterruptedException;

    void clearAudioStream();

    int size();
}
