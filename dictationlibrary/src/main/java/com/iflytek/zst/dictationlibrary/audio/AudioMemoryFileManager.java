package com.iflytek.zst.dictationlibrary.audio;

import com.iflytek.zst.dictationlibrary.utils.MyLogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:音频管理类者对象,所有音频都先保存到MemoryFile中,当快要沾满时保存到file中
 */
public class AudioMemoryFileManager implements IAudioManager{
    private static final String TAG = "AudioQueueManager";
    IAudioStream mIAudioStream;
    File mAudioResFile;
    // private OutputStream mPcmFile;
    private AudioMemoryFile mMemoryFile;
    boolean isStartAdd;
    private Thread putAudioThread;

    public AudioMemoryFileManager(IAudioStream IAudioStream) {
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
    @Override
    public void setAudioResFile(File audioResFile){
        if (audioResFile == null) {
            throw new NullPointerException("音频文件不能为null");
        }
        this.mAudioResFile = audioResFile;
        try {
            mMemoryFile=new AudioMemoryFile("pcmFile", 32000*500);
            mMemoryFile.allowPurging(false);
            mMemoryFile.setSaveFile(audioResFile,true );
            // this.mPcmFile = new FileOutputStream(mAudioResFile);
            // mInputStream = mMemoryFile.getInputStream();
            // mPcmFile = mMemoryFile.getOutputStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 从文件中读取音频到音频缓存池中,缓存池是一个阻塞队列,如果缓存满了将阻塞到哪里,等待队列take
     *
     * @param byteSize
     */
    @Override
    public void startAddQueue(final int byteSize) {
        isStartAdd = true;
        putAudioThread = new Thread() {

            @Override
            public void run() {
                super.run();
                //TODO 调研最优方案
                MyLogUtils.d(TAG,"startAddQueue run() called byteSize = "+byteSize);
                // RandomAccessFile randomAccessFile = null;
                // FileChannel fileChannel = null;
                try {
                    // randomAccessFile = new RandomAccessFile(mMemoryFile.getInputStream(),"r");
                    // fileChannel = mMemoryFile.getChannel();
                    // ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
                    byte[] bytes = new byte[byteSize];
                    int length = -1;
                    while (isStartAdd) {
                        // long time = SystemClock.elapsedRealtime();
                        // Log.d(TAG, "run() called " +mMemoryFile.getReadAbleSize());
                        if (mMemoryFile.getReadAbleSize() >= byteSize) {
                            if ((length = mMemoryFile.readBytes(bytes,0,byteSize)) != -1) {
                                mIAudioStream.putAudioBytes(bytes.clone());
                            }
                            // Log.d(TAG, "run() called length = " +length+ " cost time = " + (SystemClock.elapsedRealtime() - time));
                        }else {
                            Thread.sleep(40);
                            // Log.d(TAG, "run() called available = " + mMemoryFile.getReadAbleSize() +" sleep 40");
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (mMemoryFile != null) {
                        try {
                            mMemoryFile.save();
                            //关闭文件流
                            mMemoryFile.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                MyLogUtils.d(TAG, "startAddQueue run finish " + mIAudioStream.size());
            }
        };
        putAudioThread.start();
    }

    /**
     * 停止加入缓存队列
     */
    @Override
    public void stopAddQueue(){
        isStartAdd = false;
        if (putAudioThread != null && !putAudioThread.isInterrupted()) {
            putAudioThread.interrupt();
        }
        if (mIAudioStream != null && mIAudioStream.size() > 0) {
            mIAudioStream.clearAudioStream();
        }
    }

    /**
     * 先将音频放入缓存队列,当前用文件作为缓存队列
     * @param bytes
     */
    @Override
    public void addToAudioQueue(byte[] bytes) throws IOException {
        if (mMemoryFile != null) {
            mMemoryFile.writeBytes(bytes,0,bytes.length);
            if (mMemoryFile.getWritAbleSize() < bytes.length * 2) {
                mMemoryFile.save();
            }
        }
        MyLogUtils.d(TAG, "addToAudioQueue() called with: mMemoryFile.getRemaindSize() = "+mMemoryFile.getWritAbleSize());
    }

    /**
     * 从音频缓存池中读取音频
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public byte[] getAudioBytes() throws InterruptedException {
        return mIAudioStream.getAudioBytes();
    }

    public boolean hasAudio(){
        return mIAudioStream.size() > 0;
    }

}
