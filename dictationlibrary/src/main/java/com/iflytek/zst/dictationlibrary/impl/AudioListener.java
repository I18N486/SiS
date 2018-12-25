package com.iflytek.zst.dictationlibrary.impl;

/**
 * Created by DELL-5490 on 2018/12/24.
 */

public interface AudioListener {
    void onRecordBuffer(byte[] var1, int var2);

    void onError(int var1);
}
