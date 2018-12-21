package com.iflytek.zst.dictationlibrary.audio;

import android.util.Log;

import com.iflytek.zst.dictationlibrary.utils.MyLogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:音频管理类者对象,所有音频都先保存到sdcard中
 */
public class AudioQueueManager implements IAudioManager{
    private static final String TAG = "AudioQueueManager";
    IAudioStream mIAudioStream;
    File mAudioResFile;
    private FileOutputStream mPcmFile;
    boolean isStartAdd;
    private Thread putAudioThread;
    public AudioQueueManager(IAudioStream IAudioStream) {
        mIAudioStream = IAudioStream;
    }

    @Override
    public boolean createFile() {
        return true;
    }

    /**
     * 设置音频保存目录
     *
     * @param audioResFile
     */
    public void setAudioResFile(File audioResFile){
        if (audioResFile == null) {
            throw new NullPointerException("音频文件不能为null");
        }
        this.mAudioResFile = audioResFile;
        try {
            this.mPcmFile = new FileOutputStream(mAudioResFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 从文件中读取音频到音频缓存池中,缓存池是一个阻塞队列,如果缓存满了将阻塞到哪里,等待队列take
     *
     * @param byteSize
     */
    public void startAddQueue(final int byteSize) {
        isStartAdd = true;
        putAudioThread = new Thread() {

            @Override
            public void run() {
                super.run();
                //TODO 调研最优方案
                MyLogUtils.d(TAG,"startAddQueue run() called byteSize = "+byteSize);
                RandomAccessFile randomAccessFile = null;
                FileChannel fileChannel = null;
                try {
                    randomAccessFile = new RandomAccessFile(mAudioResFile,"r");
                    fileChannel = randomAccessFile.getChannel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
                    int length = -1;
                    while (isStartAdd) {
                        if (randomAccessFile.length() - randomAccessFile.getFilePointer() >= byteSize) {
                            if ((length = fileChannel.read(byteBuffer)) != -1) {
                                byteBuffer.flip();
                                mIAudioStream.putAudioBytes(byteBuffer.array().clone());
                                byteBuffer.clear();
                            }
                        }else {
                            Thread.sleep(40);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fileChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "startAddQueue run finish " + mIAudioStream.size());
            }
        };
        putAudioThread.start();
    }

    /**
     * 停止加入缓存队列
     */
    public void stopAddQueue(){
        isStartAdd = false;
        if (putAudioThread != null && !putAudioThread.isInterrupted()) {
            putAudioThread.interrupt();
        }
        if (mIAudioStream != null && mIAudioStream.size() > 0) {
            mIAudioStream.clearAudioStream();
        }
        if (mPcmFile != null) {
            try {
                mPcmFile.close();
                mPcmFile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 先将音频放入缓存队列,当前用文件作为缓存队列
     * @param bytes
     */
    public void addToAudioQueue(byte[] bytes) throws IOException {
        if (mPcmFile != null) {
            mPcmFile.write(bytes);
        }
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

    public boolean hasAudio(){
        return mIAudioStream.size() > 0;
    }

}
