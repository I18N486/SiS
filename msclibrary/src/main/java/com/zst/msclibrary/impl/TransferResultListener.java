package com.zst.msclibrary.impl;

import com.zst.msclibrary.transfermsc.speech.msg.SentenceResultEvent;

/**
 * Created by DELL-5490 on 2018/12/3.
 */

public interface TransferResultListener {
    /**
     *
     * @param results 中间子句识别结果
     * @param index 标记不确定的识别结果位置，index = results.length()时为转写引擎返回结果
     * @param isLast 是否是最后一个句子
     */
    void onSentenceUpdate(String results, int index, boolean isLast);

    /**
     *
     * @param results 最后确定的识别结果
     * @param isLast 是否是最后一个句子
     */
    void onSentenceEnd(String results, boolean isLast);

    /**
     *
     * @param code 错误码
     * @param message 错误描述
     */
    void onError(String code,String message);

    /**
     *
     */
    void onRecordStop();

    /**
     *
     * @param targetTxt 翻译结果
     * @param type 返回类型     0：中间子句  1：完整子句
     */
    void onTransSucess(String targetTxt, int type);

    /**
     *
     * @param bytes 音频数据
     */
    void onAudioBytes(byte[] bytes);

    /**
     * 切换引擎回调
     */
    void exchangeEngine();

    /**
     *
     * @param sentenceResult 转写引擎识别以及翻译结果
     */
    void onMscResults(SentenceResultEvent sentenceResult);
}
