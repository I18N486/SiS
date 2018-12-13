package com.zst.msclibrary.transfermsc.speech.iat;

import android.text.TextUtils;
import android.util.Log;

import com.zst.msclibrary.transfermsc.openapi.entity.LatticeData;
import com.zst.msclibrary.transfermsc.speech.msg.SentenceResultEvent;
import com.zst.msclibrary.transfermsc.speech.time.TimerManager;

/*
 * Auth: DELL-5490
 * Date: 2018/11/15
 */
public class SegemProcess<T extends BaseRecognizer> {
    private static final String TAG = "SegemProcess";

    public static final int TYPE_1 = 0x01;
    public static final int TYPE_2 = 0x02;
    public static final int TEXT_MAX_SHOW_LENGTH = 300;
    public int type;

    private static final String MEDIAN_RESULT = "1";
    private static final String FINAL_RESULT = "0";

    public String lastRecType = "0";
    private int mTextMaxLength = 150;
    private String mResult = "";

    private static final int ID_SEGEM_TIME = 0x01;

    public String WORD_REGEX = "";
    private static final String PUNCT_REGEX = "^[^\\u4e00-\\u9fa5A-Za-z1-9]+";

    /**
     *
     */
    public String[] segemWord = new String[]{"第一", "第二"};
    public boolean isTimeout = false;
    public boolean isCheck = false;

    public boolean isFirst = true;
    public int mSequence = 0;

    private TimerManager mTimerManager;
    private long SPEAK_TIME  = 3000L;
    private T t;

    private String sNo;

    public SegemProcess(T t) {
        mTimerManager = new TimerManager(timeCallback);
        this.t = t;
        type = TYPE_1;
//        sNo = MyApplication.sNo;
        setWordRegex();
    }

    public void setWordRegex() {
        WORD_REGEX = "^(";
        for (int i = 0; i < segemWord.length - 1; i++) {
            WORD_REGEX = WORD_REGEX.concat(segemWord[i]).concat("|");
        }
        WORD_REGEX = WORD_REGEX.concat(segemWord[segemWord.length - 1]);
        WORD_REGEX = WORD_REGEX.concat(").*");
    }

    public void initParam(){
        isTimeout = false;
        isCheck = false;
        isFirst = true;
        mSequence = 0;
    }

    /**
     * @param latticeData
     */
    public void processResult(LatticeData latticeData){
        if (type == TYPE_1) {
            processResult_Type1(latticeData);
        } else if (type == TYPE_2) {
            processResult_Type2(latticeData);
        }
    }


    public void processEnd(){
        mTimerManager.cancelTimer(ID_SEGEM_TIME);
        isTimeout = false;
        isCheck = false;
        isFirst = true;
        mSequence = 0;
    }
    /**
     * 分段处理(接长度分段)
     * @param latticeData
     */
    public void processResult_Type1(LatticeData latticeData) {
//        pringLog("processResult Enter " + JSON.toJSONString(latticeData));
        if(null == latticeData || TextUtils.isEmpty(latticeData.getText())){
            //数据为空，不处理
            return;
        }
        notifySpeakResult(latticeData);
        mTimerManager.cancelTimer(ID_SEGEM_TIME);
        if (FINAL_RESULT.equals(latticeData.getType())) {
            String result = "";
            if (mSequence == 0) {
                result = latticeData.getText().replaceAll(PUNCT_REGEX, "");
            } else {
                result = latticeData.getText();
            }
            mSequence++;
            mResult = mResult.concat(result);
//            this.t.notifySpeakResult(latticeData);

            //文本长度达到指定长度后，进行分段处理
            if (mResult.length() >= mTextMaxLength) {
                this.t.segmenRecord();
                isFirst = true;
                mSequence = 0;
                mResult = "";
            }
            else {
                mTimerManager.startTime(ID_SEGEM_TIME, SPEAK_TIME);
            }
        }
        lastRecType = latticeData.getType();
    }


    /**
     * 分段处理(关键字分段)
     * @param latticeData
     */
    public void processResult_Type2(LatticeData latticeData) {
        if(null == latticeData || TextUtils.isEmpty(latticeData.getText())){
            return;
        }
//        pringLog("processResult Enter " + JSON.toJSONString(latticeData));
        String result = latticeData.getText().replaceAll(PUNCT_REGEX, "");
        if (result.matches(WORD_REGEX) && isTimeout && !isCheck) {
            isTimeout = false;
            isCheck = true;
            isFirst = true;
            mSequence = 0;
        }
        notifySpeakResult(latticeData);
        mTimerManager.cancelTimer(ID_SEGEM_TIME);
        if (FINAL_RESULT.equals(latticeData.getType())) {
            this.t.notifySpeakResult(latticeData);
            if (mResult.length() >= TEXT_MAX_SHOW_LENGTH) {
                this.t.segmenRecord();
                mTimerManager.cancelTimer(ID_SEGEM_TIME);
                isCheck = false;
                isFirst = true;
                mSequence = 0;
                mResult = "";
                isTimeout = false;
            }
            else {
                mTimerManager.startTime(ID_SEGEM_TIME, SPEAK_TIME);
                isTimeout = false;
                if (isCheck) {
                    this.t.segmenRecord();
                    mSequence = 0;
                    isCheck = false;
                }
                latticeData.getType();
            }
        }
        lastRecType = latticeData.getType();
    }


    /**
     * 识别结果通知
     *
     * @param latticeData
     */
    public void notifySpeakResult(LatticeData latticeData) {
        SentenceResultEvent resultMsg = new SentenceResultEvent();
        if (isFirst) {
            resultMsg.start = "0";
            isFirst = false;
        } else {
            resultMsg.start = "1";
        }
        resultMsg.type = latticeData.getType();
        Log.v(getClass().getSimpleName(), "mSequence = " + mSequence);
        if (mSequence == 0) {
            resultMsg.result = latticeData.getText().replaceAll(PUNCT_REGEX, "");
        } else {
            resultMsg.result = latticeData.getText();
        }
//        resultMsg.name = SpUtils.getInstance().getDeviceName();
        resultMsg.sn = sNo;
        resultMsg.bg = latticeData.getBg();
        resultMsg.ed = latticeData.getEd();
        this.t.onSentenceReuslt(resultMsg);
    }

    /**
     * 超时判断处理
     */
    public TimerManager.Callback timeCallback = new TimerManager.Callback() {
        @Override
        public void onTimeout(int id) {
            Log.v(TAG, "onTimeout");
            t.onSpeakTimeout();
        }
    };

    /**
     *  超时处理.
     */
    public void onTimeOut() {
        if (type == TYPE_1) {
            if (FINAL_RESULT.equals(lastRecType)) {
                this.t.segmenRecord();
                isFirst = true;
                mSequence = 0;
                mResult = "";
            }
        } else if (type == TYPE_2) {
            isTimeout = true;
        }

    }

    private void pringLog(String msg) {
        Log.i(TAG, msg);
    }
}
