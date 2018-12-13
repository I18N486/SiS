package com.zst.msclibrary.impl;

import android.content.Context;

/**
 * Created by DELL-5490 on 2018/12/3.
 */

public interface IMscUtil {

    /**
     * 初始化msc
     * @param context
     * @param appId
     */
    void initMsc(Context context, String appId);

    /**
     * 开启引擎
     * @param resultListener 引擎回调接口
     */
    void startRecognize(TransferResultListener resultListener, String fileDir, boolean openRecord);

    /**
     * 停止引擎
     */
    void stopRecognize();

    /**
     * 设定识别语言
     * @param srcLanguage 识别语种
     */
    void setSrcLanguage(String srcLanguage);

    /**
     * 设定翻译语言
     * @param transLanguage 翻译语种
     */
    void setTransLanguage(String transLanguage);

    /**
     * 判断引擎是否正在运行
     * @return 引擎是否正在运行
     */
    boolean isRunning();

    /**
     * 手动写入音频数据
     * @param audioBuffer 音频数据流
     */
    void writeAudio(byte[] audioBuffer);

}
