package com.iflytek.zst.dictationlibrary.online;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.fsp.shield.android.sdk.http.ApiCallback;
import com.iflytek.fsp.shield.android.sdk.http.ApiProgress;
import com.iflytek.fsp.shield.android.sdk.http.ApiResponse;
import com.iflytek.zst.dictationlibrary.bean.MyResultBean;
import com.iflytek.zst.dictationlibrary.bean.TransResponseBean;
import com.iflytek.zst.dictationlibrary.bean.TransTextBean;
import com.iflytek.zst.dictationlibrary.impl.DictationResultListener;
import com.iflytek.zst.dictationlibrary.utils.DictationResultFormat;
import com.iflytek.zst.dictationlibrary.utils.JsonParser;
import com.iflytek.zst.dictationlibrary.utils.MyLogUtils;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL-5490 on 2018/12/17.
 */

public class RecognizerEngine {
    private static final String TAG = "RecognizerEngine";
    private static String TRANSAPPID;

    private static SpeechRecognizer mAsr;

    private DictationResultListener resultListener = null;
    private boolean isRunning = false;
    private ExecutorService executorService;

    private RecognizerEngine(){
        executorService = Executors.newFixedThreadPool(1);
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
    public static void init(Context context,String appId,String transAppID){
        SpeechUtility.createUtility(context, "appid=" + appId);
        mAsr = SpeechRecognizer.createRecognizer(context,mInitListener);
        TRANSAPPID = transAppID;
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
            if (resultListener != null){
                resultListener.onAudioBytes(bytes);
            }
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
            if (resultListener != null) {
                resultListener.onSentenceResult(myResultBean);
                transTextByWebApi(myResultBean);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            MyLogUtils.e(TAG,"recognize error,msg: "+speechError.toString());
            if (resultListener != null) {
                resultListener.onError(speechError.getErrorCode());
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private RecognizerListener tsRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            MyLogUtils.d(TAG,"原始识别结果："+recognizerResult.getResultString());
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 通用识别，带pgs效果，使用sdk默认录音（当前引擎不支持长听写，最长1分钟后自动停止识别转写）
     * @param filePath 音频文件保存全路径（设置为null则不保存）
     */
    public void startRecognWithPgs(DictationResultListener dictationResultListener,String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"startRecognWithPgs error,the masr is null");
            return;
        }
        resultListener = dictationResultListener;
        //开始识别之前先设置引擎参数
        setParam(filePath);
        //开启pgs功能，实时转写
        mAsr.setParameter(SpeechConstant.ASR_DWA, "wpgs");
        if (!mAsr.isListening()) {
            isRunning = true;
            int retCode = mAsr.startListening(mRecognizerListener);
            if (retCode != ErrorCode.SUCCESS) {
                isRunning = false;
                MyLogUtils.e(TAG, "听写识别失败，错误码：" + retCode);
            }
        } else {
            //引擎已经在识别状态
            MyLogUtils.d(TAG,"当前引擎已处于识别状态，无需重复开启");
        }
    }


    public void startRecognNoPgs(String filePath){
        if (mAsr == null){
            MyLogUtils.e(TAG,"startRecognNoPgs error,the masr is null");
            return;
        }
        //开始识别之前先设置引擎参数
        setParam(filePath);
        //启用翻译(！！！注意启用同步翻译时不可开启pgs功能)
        mAsr.setParameter(SpeechConstant.ASR_SCH,"1");
        //翻译通道
        mAsr.setParameter(SpeechConstant.ADD_CAP,"translate");
        //设置原始语种
        mAsr.setParameter( SpeechConstant.ORI_LANG, "cn" );
        //设置目标语种
        mAsr.setParameter( SpeechConstant.TRANS_LANG, "en" );
        if (!mAsr.isListening()) {
            isRunning = true;
            int retCode = mAsr.startListening(tsRecognizerListener);
            if (retCode != ErrorCode.SUCCESS) {
                isRunning = false;
                MyLogUtils.e(TAG, "听写识别失败，错误码：" + retCode);
            }
        } else {
            //引擎已经在识别状态
            MyLogUtils.d(TAG,"当前引擎已处于识别状态，无需重复开启");
        }
    }

    /**
     * 引擎是否在运行
     * @return
     */
    public boolean isRunning(){
        return isRunning && mAsr!= null && mAsr.isListening();
    }


    /**
     * 停止听写，该方法保证所有识别信息返回
     * @param handler 发送信息，告知主线程引擎已完成停止
     * @param what msg.what
     */
    public void stopRecogn(final Handler handler, final int what){
        if (mAsr == null){
            return;
        }
        //该方法调用之后需要过一段时间引擎才会完成结束，所以需要在子线程中轮询引擎状态，保证引擎的所有消息完全返回，状态一致
        mAsr.stopListening();
        if (executorService != null){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if (mAsr.isListening()){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            isRunning = false;
                            handler.sendEmptyMessage(what);
                            break;
                        }
                    }
                }
            });
        }
    }


    /**
     * 停止听写，该方法会立即停止识别，后续消息立即停止返回
     */
    public void cancelRecogn(){
        if (mAsr == null){
            return;
        }
        mAsr.cancel();
    }


    /**
     * 调用后台翻译接口进行翻译
     * @param myResultBean
     */
    public void transTextByWebApi(final MyResultBean myResultBean) {
        MyLogUtils.d(TAG,"call transtext: oris= "+myResultBean.content);
        if (myResultBean.content == null || myResultBean.content.isEmpty()) {
            //空字串翻译拦截
            return;
        }

        TransTextBean transTextBean = new TransTextBean();
        transTextBean.appid = TRANSAPPID;
        transTextBean.sn = Build.SERIAL;
        transTextBean.from = "cn";
        transTextBean.to = "en";
        transTextBean.srcStr = myResultBean.content;
        transTextBean.latitude = "123.23";
        transTextBean.longitude = "132.519";
        transTextBean.applicationType = 1;
        ShieldAsyncApp_meeting.getInstance().txtTrans(transTextBean, new ApiCallback<String>() {
            @Override
            public void onDownloadProgress(ApiProgress apiProgress) {

            }

            @Override
            public void onHttpDone() {

            }

            @Override
            public void onSuccess(ApiResponse apiResponse, String o) {
                MyLogUtils.d(TAG,"transTextByWebApi response= "+o);
                TransResponseBean responseBean = new Gson().fromJson(o,TransResponseBean.class);
                if (o != null && "1000".equals(responseBean.getCode())){
                    if (responseBean.getData() != null){
                        MyLogUtils.d(TAG,"transTextByWebApi success,the target= "+responseBean.getData().getTargetTxt());
                        MyResultBean transBean = new MyResultBean();
                        transBean.content = responseBean.getData().getTargetTxt();
                        transBean.pgs = myResultBean.pgs;
                        transBean.replace = 0;
                        transBean.isEnd = myResultBean.isEnd;
                        if (resultListener != null) {
                            resultListener.onTransResult(transBean);
                        }
                    }
                }
            }

            @Override
            public void onFailed(ApiResponse apiResponse) {
                MyLogUtils.d(TAG,"transTextByWebApi failed" + apiResponse.toString());
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                MyLogUtils.e(TAG, "transTextByWebApi has happened exception");
            }
        }, null);
    }
}
