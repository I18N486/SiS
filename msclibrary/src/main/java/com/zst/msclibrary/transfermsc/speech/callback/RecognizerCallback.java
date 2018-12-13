package com.zst.msclibrary.transfermsc.speech.callback;


import com.zst.msclibrary.transfermsc.speech.msg.SentenceResultEvent;

/*
 * Auth: DELL-5490
 * Date: 2018/10/31
 */
public interface RecognizerCallback{
    void onAudioState(int state, String desc);
    void onSentenceReuslt(SentenceResultEvent result);
    void onRecongizerFinish(String speakResult, String transResult, String fileName);
}
