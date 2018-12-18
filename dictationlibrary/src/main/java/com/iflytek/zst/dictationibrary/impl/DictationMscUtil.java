package com.iflytek.zst.dictationibrary.impl;

import android.content.Context;

import com.iflytek.zst.dictationibrary.online.RecognizerEngine;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public class DictationMscUtil implements IMscUtil {
    private RecognizerEngine recognizerEngine;

    private DictationMscUtil(){
        recognizerEngine = RecognizerEngine.getInstance();
    }

    private static class SingletonHolder{
        public static final DictationMscUtil instance = new DictationMscUtil();
    }

    /**
     * 获取单例
     * @return
     */
    public static DictationMscUtil getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 初始化
     * @param context
     * @param appId
     */
    public static void init(Context context,String appId){
        RecognizerEngine.init(context,appId);
    }



    @Override
    public void startRecogn(DictationResultListener resultListener, String filePath) {
        recognizerEngine.startRecogn(resultListener,filePath);
    }
}
