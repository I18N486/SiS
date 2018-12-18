package com.iflytek.zst.dictationibrary.impl;


import com.iflytek.zst.dictationibrary.bean.MyResultBean;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public interface DictationResultListener {
    void onStartSpeech();
    void onEndSpeech();
    void onSentenceUpdate(String content,boolean isLast);
    void onSentenceEnd(String content,boolean isLast);
    void onError(int errorCode);
    void onSentenceResult(MyResultBean myResultBean);
}
