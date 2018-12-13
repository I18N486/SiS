package com.zst.msclibrary.transfermsc;

import android.content.Context;

import com.zst.msclibrary.Constants;
import com.zst.msclibrary.impl.IMscUtil;
import com.zst.msclibrary.impl.TransferResultListener;
import com.zst.msclibrary.transfermsc.openapi.TransferCenter;
import com.zst.msclibrary.transfermsc.speech.iat.ChineseRecognizer;

/**
 * Created by DELL-5490 on 2018/12/3.
 * 转写引擎工具类：识别，翻译
 */
public class TransferMscUtil implements IMscUtil {

    private ChineseRecognizer mTranslate;
    private static Context mContext;
    private static String mAppId;

    private TransferMscUtil(){ }
    private static class SingletonHolder{
        private static final TransferMscUtil instance = new TransferMscUtil();
    }

    /**
     * 获取单例
     * @return mscutil单例
     */
    public static TransferMscUtil getInstance(){
        return SingletonHolder.instance;
    }


    /**
     * 初始化
     * @param appId
     */
    @Override
    public void initMsc(Context context, String appId){
        TransferCenter.init(Constants.TRANSFER_HOST, appId, Constants.TRANSFER_APP_SECRET,
                5_000L ,null);
        mContext = context;
        mAppId = appId;
    }


    /**
     * 开始识别,传递引擎回调实例
     * @param resultListener 回调实例
     */
    @Override
    public void startRecognize(TransferResultListener resultListener, String fileDir, boolean openRecord){
        mTranslate = new ChineseRecognizer(mContext, openRecord);
        mTranslate.setTranslateParams("cn", "en", mAppId, fileDir);
        if (mTranslate == null) {
            return;
        }
        mTranslate.startSpeak(resultListener);
    }

    //TODO 设置存储的文件路径


    /**
     * 结束识别
     */
    @Override
    public void stopRecognize(){
        mTranslate.endSpeak();
    }


    /**
     * 设置识别语种
     * @param srcLanguage
     */
    @Override
    public void setSrcLanguage(String srcLanguage){
        mTranslate.setTranslateParams(srcLanguage, null);
    }


    /**
     * 设置翻译语种
     * @param transLanguage
     */
    @Override
    public void setTransLanguage(String transLanguage){
        mTranslate.setTranslateParams(null, transLanguage);
    }


    /**
     * 引擎是否在运行
     * @return
     */
    @Override
    public boolean isRunning(){
        return mTranslate.isSpeaking;
    }

    @Override
    public void writeAudio(byte[] audioBuffer) {
        mTranslate.writeAudio(audioBuffer);
    }


}