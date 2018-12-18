package com.iflytek.zst.dictationibrary.online;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.zst.dictationibrary.bean.MyResultBean;
import com.iflytek.zst.dictationibrary.constants.Constants;
import com.iflytek.zst.dictationibrary.impl.DictationResultListener;
import com.iflytek.zst.dictationibrary.impl.IMscUtil;
import com.iflytek.zst.dictationibrary.utils.DictationResultFormat;
import com.iflytek.zst.dictationibrary.utils.JsonParser;
import com.iflytek.zst.dictationibrary.utils.MyLogUtils;

import java.util.HashMap;

/**
 * Created by DELL-5490 on 2018/12/17.
 */

public class RecognizerEngine {
    private static final String TAG = "RecognizerEngine";

    private static SpeechRecognizer mAsr;

    private DictationResultListener resultListener = null;
    private String lastContent = "";

    private RecognizerEngine(){
    }
    private static class SingletonHolder{
        public static final RecognizerEngine instance = new RecognizerEngine();
    }

    /**
     * 获取单例
     * @return
     */
    public static RecognizerEngine getInstance(){
        return SingletonHolder.instance;
    }


    /**
     * 初始化监听器
     */
    private static InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            MyLogUtils.d(TAG,"oninit: "+i);
            if (i != ErrorCode.SUCCESS) {
                MyLogUtils.e(TAG,"初始化失败，错误码：" + i);
            }
        }
    };

    /**
     * 初始化
     * @param context
     * @param appId
     */
    public static void init(Context context,String appId){
        SpeechUtility.createUtility(context, "appid=" + appId);
        mAsr = SpeechRecognizer.createRecognizer(context,mInitListener);
    }


    /**
     * 默认参数设置
     */
    private void setParam(String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"setParam error,the masr is null");
            return;
        }
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎类型（TYPE_CLOUD 在线引擎；TYPE_LOCAL 离线引擎）
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

        //设置音频源(不设置则默认是1，代表sdk使用mediarecorder进行录音识别；-1，使用音频源，配合写音频方式；
        // -2，读取音频文件方式识别，传入ASR_SOURCE_PATH路径；-3，使用 AIMIC 方式( -3 )下（仅唤醒下有效），SDK通过麦克风硬件录音)
        mAsr.setParameter(SpeechConstant.AUDIO_SOURCE,"1");

        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mAsr.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mAsr.setParameter(SpeechConstant.VAD_EOS, "10000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mAsr.setParameter(SpeechConstant.ASR_PTT, "1");

        //开启pgs功能，实时转写
        mAsr.setParameter(SpeechConstant.ASR_DWA, "wpgs");

        // 设置语言
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        if (filePath != null && !"".equals(filePath.trim())) {
            if (filePath.endsWith("wav")){
                mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            } else if (filePath.endsWith("pcm")){
                mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"pcm");
            } else {
                MyLogUtils.e(TAG,"the audio file path is error,Only PCM and wav formats are supported!");
            }
            mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, filePath);
        }
    }


    /**
     * 听写监听器
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener(){

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            MyLogUtils.d(TAG,"onVolumeChanged "+ i);
        }

        @Override
        public void onBeginOfSpeech() {
            MyLogUtils.d(TAG,"onBeginOfSpeech");
            if (resultListener != null){
                resultListener.onStartSpeech();
            }
        }

        @Override
        public void onEndOfSpeech() {
            MyLogUtils.d(TAG,"onEndOfSpeech");
            if (resultListener != null){
                resultListener.onEndSpeech();
            }
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            MyLogUtils.d(TAG,"原始识别结果："+recognizerResult.getResultString());
            MyResultBean myResultBean = DictationResultFormat.formatIatResult(recognizerResult.getResultString());
            MyLogUtils.d(TAG,"数据格式化后："+myResultBean.toString());
            HashMap<String,String> mAsrResultsMap = JsonParser.parseIatResults(recognizerResult.getResultString());
            MyLogUtils.d(TAG,"二次数据格式化后："+mAsrResultsMap.toString());
            resultListener.onSentenceResult(myResultBean);

//            if (myResultBean.type == Constants.TYPE_UPDATE){
//                lastContent = myResultBean.content;
//                resultListener.onSentenceUpdate(lastContent,myResultBean.isEnd);
//            } else if (myResultBean.type == Constants.TYPE_END){
////                if (myResultBean.isEnd) {
////                    //如果是结束会话，最后一次apd结果直接作为最终字串
////                    resultListener.onSentenceEnd(lastContent + myResultBean.content, myResultBean.isEnd);
////                } else {
////                    //不是结束会话，中间子句apd不作为最终字串
////                    resultListener.onSentenceEnd(lastContent, myResultBean.isEnd);
////                }
//                resultListener.onSentenceEnd(lastContent + myResultBean.content, myResultBean.isEnd);
//                lastContent = "";
//            }
        }

        @Override
        public void onError(SpeechError speechError) {
            MyLogUtils.e(TAG,"recognize error,msg: "+speechError.toString());
            resultListener.onError(speechError.getErrorCode());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    /**
     * 通用识别，不带pgs效果，使用sdk默认录音
     * @param filePath 音频文件保存全路径（设置为null则不保存）
     */
    public void startRecogn(DictationResultListener dictationResultListener,String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"startRecogn error,the masr is null");
            return;
        }
        resultListener = dictationResultListener;
        //开始识别之前先设置引擎参数
        setParam(filePath);
        int retCode = mAsr.startListening(mRecognizerListener);
        if (retCode != ErrorCode.SUCCESS){
            MyLogUtils.e(TAG,"听写识别失败，错误码："+retCode);
        }
    }



}
