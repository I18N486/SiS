package com.iflytek.zst.dictationlibrary.impl;


import com.iflytek.zst.dictationlibrary.bean.FormatNormalBean;
import com.iflytek.zst.dictationlibrary.bean.FormatResultBean;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public interface DictationResultListener {
    void onStartSpeech();
    void onEndSpeech();
    void onError(int errorCode);
    void onSentenceResult(FormatResultBean orisBean);
    void onTransResult(FormatResultBean transBean);
    void onAudioBytes(byte[] audioBytes);
    void onNoPgsResult(FormatNormalBean normalBean);
}
