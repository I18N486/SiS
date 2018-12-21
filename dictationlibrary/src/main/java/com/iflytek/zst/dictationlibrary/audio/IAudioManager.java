package com.iflytek.zst.dictationlibrary.audio;

import java.io.File;
import java.io.IOException;

/**
 * Created by jiwang on 2018/11/20.
 * 备注: 音频管理类接口
 */
public interface IAudioManager {
    /**
     * 是否需要创建文件,true 则通过{@link #setAudioResFile(File)} 生成文件流,写入到文件
     * @return
     */
    boolean createFile();
    /**
     * 设置音频保存目录
     *
     * @param audioResFile
     */
    void setAudioResFile(File audioResFile);

    /**
     * 从文件中读取音频到音频缓存池中,缓存池是一个阻塞队列,如果缓存满了将阻塞到哪里,等待队列take
     *
     * @param byteSize
     */
    void startAddQueue(final int byteSize);

    /**
     * 停止加入缓存队列
     */
    void stopAddQueue();

    /**
     * 先将音频放入缓存队列,当前用文件作为缓存队列
     *
     * @param bytes
     */
    void addToAudioQueue(byte[] bytes) throws InterruptedException, IOException;

    /**
     * 从音频缓存池中读取音频
     *
     * @return
     * @throws InterruptedException
     */
    byte[] getAudioBytes() throws InterruptedException;


    /**
     * 判断音频队列是否有音频
     *
     * @return
     */
    boolean hasAudio();
}
