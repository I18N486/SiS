package com.iflytek.zst.dictationlibrary.audio;

import android.os.MemoryFile;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jiwang on 2018/11/19.
 * 备注:
 */
public class AudioMemoryFile extends MemoryFile {
    private static final String TAG = "AudioMemoryFile";
    public static final int SAVE_BUFFER_LENGTH = 2560;

    /**
     * 音频读取的偏移量
     */
    private int mReadOffset;
    /**
     * 音频写入的偏移量
     */
    private int mWriteOffset;
    private File mSaveFile;
    private FileOutputStream mFileOutputStream;
    /**
     * Allocates a new ashmem region. The region is initially not purgable.
     *
     * @param name   optional name for the file (can be null).
     * @param length of the memory file in bytes, must be non-negative.
     * @throws IOException if the memory file could not be created.
     */
    public AudioMemoryFile(String name, int length) throws IOException {
        super(name, length);
    }

    public synchronized int readBytes(byte[] buffer, int srcOffset, int count) throws IOException {
        int length=super.readBytes(buffer, mReadOffset, 0, count);
        this.mReadOffset += count;
        return length;
    }

    public synchronized void writeBytes(byte[] buffer, int srcOffset, int count) throws IOException {
        super.writeBytes(buffer, srcOffset, mWriteOffset, count);
        this.mWriteOffset += count;
    }

    /**
     * 获取当前读取的偏移量
     *
     * @return
     */
    public int getReadOffset() {
        return mReadOffset;
    }

    /**
     * 获取当前写入的音频偏移量
     *
     * @return
     */
    public int getWriteOffset() {
        return mWriteOffset;
    }

    public int getReadAbleSize(){
        return getWriteOffset() - getReadOffset();
    }

    public int getWritAbleSize(){
        return length() - getWriteOffset();
    }

    public void setSaveFile(File file, boolean append) throws FileNotFoundException {
        this.mSaveFile = file;
        this.mFileOutputStream = new FileOutputStream(mSaveFile,append);
    }

    public void setSaveFile(File file) throws FileNotFoundException {
        setSaveFile(file, true);
    }

    /**
     * 将当前已经读取的内容保存到文件
     *
     * @throws IOException
     */
    public synchronized void save() throws IOException {
        long time = SystemClock.elapsedRealtime();
        //获取当前读取的位置
        int readOffset = getReadOffset();

        if (mFileOutputStream != null) {
            byte[] bytes=new byte[SAVE_BUFFER_LENGTH];
            int curOffset = 0;
            int length = 0;
            //复制到文件中
            while (curOffset <= readOffset) {
                int readLength = readOffset - curOffset >= SAVE_BUFFER_LENGTH ?SAVE_BUFFER_LENGTH: readOffset - curOffset;
                readBytes(bytes, curOffset, 0, readLength);
                mFileOutputStream.write(bytes,0,readLength );
                curOffset += readLength;
                Log.d(TAG, "save() called readOffset = " + readOffset +" curOffset = " +curOffset+" readLength = " +readLength );
                if (curOffset == readOffset) {
                    break;
                }
            }
        }
        //移动当前的数据到内存映射开始位置
        int remaindLength = mWriteOffset - readOffset;
        if (remaindLength > 0) {
            byte[] remaindBytes = new byte[remaindLength];
            //可移动数据量大小
            readBytes(remaindBytes, readOffset, 0, remaindLength);
            writeBytes(remaindBytes, 0, 0, remaindLength);
            Log.d(TAG, "save() called remaindLength = "+remaindLength + "mWriteOffset = "+mWriteOffset + "readOffset = "+readOffset   );
        }
        this.mReadOffset = 0;
        this.mWriteOffset = remaindLength;
        Log.d(TAG, "save() called" + " cost time = " + (SystemClock.elapsedRealtime() - time));
    }

    @Override
    public void close() {
        super.close();
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
