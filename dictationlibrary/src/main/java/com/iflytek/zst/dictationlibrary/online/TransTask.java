package com.iflytek.zst.dictationlibrary.online;

import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.iflytek.fsp.shield.android.sdk.http.ApiCallback;
import com.iflytek.fsp.shield.android.sdk.http.ApiProgress;
import com.iflytek.fsp.shield.android.sdk.http.ApiResponse;
import com.iflytek.zst.dictationlibrary.bean.FormatResultBean;
import com.iflytek.zst.dictationlibrary.bean.TransResponseBean;
import com.iflytek.zst.dictationlibrary.bean.TransTextBean;
import com.iflytek.zst.dictationlibrary.impl.DictationResultListener;
import com.iflytek.zst.dictationlibrary.online.transtask.AbsTask;
import com.iflytek.zst.dictationlibrary.utils.MyLogUtils;
import com.iflytek.zst.dictationlibrary.utils.StringUtils;

/**
 * Created by DELL-5490 on 2018/12/20.
 */

public class TransTask extends AbsTask {
    private static final String TAG = "TransTask";
    private String appId;
    private String from;
    private String to;
    private FormatResultBean formatResultBean;
    private DictationResultListener resultListener;

    @Override
    public void run() {
        //开启后执行锁
        setConditionLockAfterRun(true);
        //设置后执行锁最长时长10s
        setConditionLockAfterRunTime(10000);
        transTextByWebApi(appId,from,to, formatResultBean,resultListener);
    }


    /**
     * 调用后台翻译接口进行翻译
     * @param appId  appid
     * @param from  源语种
     * @param to 目标语种
     * @param formatResultBean 数据bean
     * @param resultListener  结果回调
     */
    public void transTextByWebApi(String appId, String from, String to, final FormatResultBean formatResultBean,
                                  final DictationResultListener resultListener) {
        MyLogUtils.d(TAG,"call transtext: oris= "+ formatResultBean.content);
        if (StringUtils.isEmptyOrSpaces(formatResultBean.content)) {
            //空字串翻译拦截,同时释放后执行锁
            setConditionLockAfterRun(false);
            return;
        }

        TransTextBean transTextBean = new TransTextBean();
        transTextBean.appid = appId;
        transTextBean.sn = Build.SERIAL;
        transTextBean.from = from;
        transTextBean.to = to;
        transTextBean.srcStr = formatResultBean.content;
        transTextBean.latitude = "123.23";
        transTextBean.longitude = "132.519";
        transTextBean.applicationType = 1;
        ShieldAsyncApp_meeting.getInstance().txtTrans(transTextBean, new ApiCallback<String>() {
            @Override
            public void onDownloadProgress(ApiProgress apiProgress) {

            }

            @Override
            public void onHttpDone() {
                //完成翻译请求，释放后执行锁
                setConditionLockAfterRun(false);
            }

            @Override
            public void onSuccess(ApiResponse apiResponse, String o) {
                MyLogUtils.d(TAG,"transTextByWebApi response= "+o);
                TransResponseBean responseBean = new Gson().fromJson(o,TransResponseBean.class);
                if (o != null && "1000".equals(responseBean.getCode())){
                    if (responseBean.getData() != null){
                        MyLogUtils.d(TAG,"transTextByWebApi success,the target= "+responseBean.getData().getTargetTxt());
                        FormatResultBean transBean = new FormatResultBean();
                        transBean.content = responseBean.getData().getTargetTxt();
                        transBean.pgs = formatResultBean.pgs;
                        transBean.replace = 0;
                        transBean.isEnd = formatResultBean.isEnd;
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


    public void setData(String appId, String from, String to, FormatResultBean formatResultBean, DictationResultListener resultListener){
        this.appId = appId;
        this.from = from;
        this.to = to;
        this.formatResultBean = formatResultBean;
        this.resultListener = resultListener;
    }
}
