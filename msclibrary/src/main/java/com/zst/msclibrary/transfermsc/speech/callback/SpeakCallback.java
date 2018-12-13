package com.zst.msclibrary.transfermsc.speech.callback;

/*
 * Auth: DELL-5490
 * Date: 2018/11/3
 */
public interface SpeakCallback {
    void onSpeakStart();
    void onSpeakEnd();
    void onSpeakError(String reason);
}
