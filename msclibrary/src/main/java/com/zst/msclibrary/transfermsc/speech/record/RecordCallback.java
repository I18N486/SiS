package com.zst.msclibrary.transfermsc.speech.record;

/**
 * Created by jiwang on 2018/10/14.
 * 备注:
 */
public interface RecordCallback {
    void onRecord(byte[] audio);
    void onError(int code);
}
