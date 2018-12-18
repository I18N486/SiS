package com.iflytek.zst.dictationibrary.impl;


import com.iflytek.cloud.SpeechError;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public interface DictationResultListener {
    void onStartSpeech();
    void onEndSpeech();
    void onSentenceUpdate(String content,boolean isLast);
    void onSentenceEnd(String content,boolean isLast);
    void onError(int errorCode);
}
