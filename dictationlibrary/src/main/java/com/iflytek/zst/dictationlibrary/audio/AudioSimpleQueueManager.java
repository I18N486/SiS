package com.iflytek.zst.dictationlibrary.audio;

import android.os.SystemClock;
import android.util.Log;

import com.iflytek.zst.dictationlibrary.utils.ByteRingBuffer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:音频管理类者对象,音频不保存,只使用缓存
 */
public class AudioSimpleQueueManager implements IAudioManager {
    private static final String TAG = "AudioQueueManager";
    private IAudioStream mIAudioStream;
    private ByteRingBuffer mByteRingBuffer;
    boolean isStartAdd;
    private ExecutorService mExecutorService;

    public AudioSimpleQueueManager(IAudioStream IAudioStream) {
        mIAudioStream = IAudioStream;
        mByteRingBuffer=new ByteRingBuffer();
        mExecutorService = Executors.newFixedThreadPool(1);
    }

    @Override
    public boolean createFile() {
        return false;
    }

    /**
     * 设置音频保存目录
     *
     * @param audioResFile
     */
    public void setAudioResFile(File audioResFile) {
        Log.d(TAG, "setAudioResFile() 不会保存到sd卡中");
    }

    /**
     * 从文件中读取音频到音频缓存池中,缓存池是一个阻塞队列,如果缓存满了将阻塞到哪里,等待队列take
     *
     * @param byteSize
     */
    public void startAddQueue(final int byteSize) {
        isStartAdd = true;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = new byte[byteSize];
                while (isStartAdd) {
                    // long time = SystemClock.elapsedRealtime();
                    if (mByteRingBuffer.getBusySize() >= byteSize) {
                        mByteRingBuffer.get(bytes, 0, byteSize);
                        try {
                            mIAudioStream.putAudioBytes(bytes.clone());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Log.d(TAG, "putAudioBytes() called cost time = " + (SystemClock.elapsedRealtime() - time));
                    }else{
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "startAddQueue() finished");
            }
        });
    }

    /**
     * 停止加入缓存队列
     */
    public void stopAddQueue() {
        isStartAdd = false;
        if (mIAudioStream != null && mIAudioStream.size() > 0) {
            mIAudioStream.clearAudioStream();
        }
        if (!mByteRingBuffer.isEmpty()) {
            mByteRingBuffer.clear();
        }
    }

    /**
     * 先将音频放入缓存队列,当前用文件作为缓存队列
     *
     * @param bytes
     */
    public void addToAudioQueue(byte[] bytes) throws InterruptedException {
        long time = SystemClock.elapsedRealtime();
        mByteRingBuffer.put(bytes, 0, bytes.length);
    }

    /**
     * 从音频缓存池中读取音频
     *
     * @return
     * @throws InterruptedException
     */
    public byte[] getAudioBytes() throws InterruptedException {
        return mIAudioStream.getAudioBytes();
    }

    public boolean hasAudio() {
        return mIAudioStream.size() > 0;
    }

}
