package com.zst.msclibrary.transfermsc.transfer;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.iflytek.fsp.shield.android.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.android.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.android.sdk.enums.Method;
import com.iflytek.fsp.shield.android.sdk.http.ApiCallback;
import com.iflytek.fsp.shield.android.sdk.http.ApiClient;
import com.iflytek.fsp.shield.android.sdk.http.ApiProgress;
import com.iflytek.fsp.shield.android.sdk.http.ApiRequest;
import com.iflytek.fsp.shield.android.sdk.http.ApiResponse;
import com.iflytek.fsp.shield.android.sdk.http.BaseApp;

/*
 * Auth: DELL-5490
 * Date: 2018/11/19
 */
public class MeetingTransfer extends BaseApp {

    public static final String SUCCESS_CODE = "1000";

    private Object wait = new Object();

    private String response = "";

    public MeetingTransfer() {
        this.apiClient = new ApiClient();
        this.appId = "e5588d9a5014425aad7cea42f855ec1b";
        this.appSecret = "129FC07BB18DBF43286849F11F8E6AB1";
        this.host = "172.31.3.80";
        // 武研翻译外网
//        this.host = "61.191.24.229";
        this.httpPort = 8073;
        this.httpsPort = 443;
//        this.httpsPort = 8074;
        this.stage = "RELEASE";
        this.publicKey = "305C300D06092A864886F70D0101010500034B003048024100AB05CF7B179A7EC3B0CDA5DAD1C370C8905D77685FE6AA13C9105DF6697F431F0D1493E52A96454E528FBCCBC257C9312A497A0854EEE91DE9C98586D739B6850203010001";
    }

    /**
     * Version:201805241050315522
     */
    public String txtTrans(Object body, Object tag) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/txtTrans/trans", SdkConstant.AUTH_TYPE_DEFAULT, "5b44af4e3f90435d8c8c8e4a9aab60a0");
        apiRequest.addBody(body);

        asyncInvoke(apiRequest, apiCallback, tag);
        try {
            synchronized (wait) {
                wait.wait(1000L);
            }
            return response;
        }
        catch (Exception e){

        }
        return response;
    }



    public ApiCallback<String> apiCallback = new ApiCallback<String>() {

        @Override
        public void onDownloadProgress(ApiProgress apiProgress) {

        }

        @Override
        public void onHttpDone() {

        }

        @Override
        public void onSuccess(ApiResponse apiResponse, String s) {
            Log.v("MeetingTransfer", "onSuccess " +s);
            TransferData data;
            try {
                data = JSON.parseObject(s, TransferData.class);
            } catch (ClassCastException exception) {
                Log.e("MeetingTransfer", "返回数据格式错误");
                return;
            }
            if(SUCCESS_CODE.equals(data.getCode())){
                response = data.getData().getTargetTxt();
            }
            else {
                response = "";
            }
            Log.v("MeetingTransfer", "response: " + response);
            synchronized (wait) {
                wait.notify();
            }
        }

        @Override
        public void onFailed(ApiResponse apiResponse) {
            synchronized (wait) {
                response = "";
                wait.notify();
            }
        }

        @Override
        public void onException(Exception e) {
            synchronized (wait) {
                response = "";
                wait.notify();
            }
        }
    };
}
