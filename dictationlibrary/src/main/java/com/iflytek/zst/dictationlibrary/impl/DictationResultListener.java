package com.iflytek.zst.dictationlibrary.impl;


import com.iflytek.zst.dictationlibrary.bean.MyResultBean;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public interface DictationResultListener {
    void onStartSpeech();
    void onEndSpeech();
    void onError(int errorCode);
    void onSentenceResult(MyResultBean orisBean);
    void onTransResult(MyResultBean transBean);
}
