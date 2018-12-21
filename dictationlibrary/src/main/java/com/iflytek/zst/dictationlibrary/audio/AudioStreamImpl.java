package com.iflytek.zst.dictationlibrary.audio;

import android.util.Log;

import java.util.concurrent.BlockingDeque;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:
 *  <p>使用阻塞缓存队列的方式往音频队列里面同时存和取操作.</p>
 *
 *  <p>音频  ==> 音频缓存(当前是文件) ==> 缓存队列  ==> 发送到语音云服务器</p>
 *
 *  <p>
 *
 *  如果缓存队列满了会阻塞在{@link AudioStreamImpl#putAudioBytes(byte[])} 方法
 *  如果缓存队列无音频会阻塞在{@link #getAudioBytes()}方法
 *  </p>
 *
 */
public class AudioStreamImpl implements IAudioStream {
    private static final String TAG = "AudioStreamImpl";
    /**
     * 缓存队列
     */
    BlockingDeque<byte[]> mBytes;

    public AudioStreamImpl(BlockingDeque<byte[]> bytes) {
        mBytes = bytes;
    }

    @Override
    public byte[] getAudioBytes() throws InterruptedException {
        return mBytes.take();
    }

    @Override
    public void putAudioBytes(byte[] bytes) throws InterruptedException {
        mBytes.put(bytes);
    }

    @Override
    public void clearAudioStream(){
        if (size() > 0) {
            mBytes.clear();
            Log.d(TAG, "clearAudioStream() called " +size());
        }
    }

    @Override
    public int size() {
        if (mBytes != null) {
            return mBytes.size();
        }
        return 0;
    }
}
