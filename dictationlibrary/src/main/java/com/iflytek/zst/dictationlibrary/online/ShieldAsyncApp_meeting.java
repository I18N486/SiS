package com.iflytek.zst.dictationlibrary.online;

import android.util.Log;

import com.iflytek.fsp.shield.android.sdk.constant.HttpConstant;
import com.iflytek.fsp.shield.android.sdk.constant.SdkConstant;
import com.iflytek.fsp.shield.android.sdk.enums.Method;
import com.iflytek.fsp.shield.android.sdk.enums.ParamPosition;
import com.iflytek.fsp.shield.android.sdk.http.ApiCallback;
import com.iflytek.fsp.shield.android.sdk.http.ApiClient;
import com.iflytek.fsp.shield.android.sdk.http.ApiRequest;
import com.iflytek.fsp.shield.android.sdk.http.BaseApp;
import com.iflytek.fsp.shield.android.sdk.http.MultipartFile;
import com.iflytek.zst.dictationlibrary.BuildConfig;

import java.io.File;

public class ShieldAsyncApp_meeting extends BaseApp {
    private static final String TAG = "ShieldAsyncApp_meeting";

    public ShieldAsyncApp_meeting() {
        this.apiClient = new ApiClient();
        this.appId = "86d8f06f3c274e14a08e8b7eb6add718";
        this.appSecret = "706702DC1ADCB25BEC8E4C3A8629ACA8";
        this.host = BuildConfig.API_URL;

        this.httpPort = 80;
        this.httpsPort = 443;
        this.stage = "RELEASE";
        this.publicKey = "305C300D06092A864886F70D0101010500034B003048024100942C52152ECD8AE57AD53EF5C23AEE4039A71ACFABF0FDBD4F151E962940B1075E2CBAE5DA6122E9C08D1AF9D0579A03246A72266589C73061EDF515C0F07FA70203010001";
    }

    private static class SingletonHolder{
        public static final ShieldAsyncApp_meeting instance = new ShieldAsyncApp_meeting();
    }

    public static ShieldAsyncApp_meeting getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Version:201806291336526937
     */
    public <T> void lectureUpload(String filePath, String md5, ApiCallback<T> apiCallback, Object tag) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/meeting/lectureUpload", SdkConstant.AUTH_TYPE_DEFAULT, "f31d3ec3d31e411bac1a63114ff47454");

        MultipartFile file = new MultipartFile(new File(filePath));
        apiRequest.addParam("file", file, ParamPosition.MULTIFORM, false);

        apiRequest.addParam("md5", md5, ParamPosition.QUERY, false);

        asyncInvoke(apiRequest, apiCallback, tag);
    }

    /**
     * Version:201807091431345276
     */
    public <T> void meetingVoiceUpload(MultipartFile file, String md5, ApiCallback<T> apiCallback, Object tag) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/meeting/meetingVoiceUpload", SdkConstant.AUTH_TYPE_DEFAULT, "5b44af4e3f90435d8c8c8e4a9aab60a0");
        Log.d(TAG, "host = " + this.host);

        apiRequest.addParam("file", file, ParamPosition.MULTIFORM, false);

        apiRequest.addParam("md5", md5, ParamPosition.QUERY, false);

        asyncInvoke(apiRequest, apiCallback, tag);
    }

    /**
     * Version:201805241050315522
     */
    public <T> void txtTrans(Object body, ApiCallback<T> apiCallback, Object tag) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/txtTrans/trans", SdkConstant.AUTH_TYPE_DEFAULT, "5b44af4e3f90435d8c8c8e4a9aab60a0");
        apiRequest.addBody(body);
        asyncInvoke(apiRequest, apiCallback, tag);
    }

    /**
     * Version:201809051205146284
     */
    public <T> void updateMeeting(String filePath, String meetingId, String meetingKey, ApiCallback<T> apiCallback, Object tag) {
        ApiRequest apiRequest = new ApiRequest(HttpConstant.SCHEME_HTTP, Method.POST, "/meeting/updateMeeting", SdkConstant.AUTH_TYPE_DEFAULT, "5b44af4e3f90435d8c8c8e4a9aab60a0");
        MultipartFile file = new MultipartFile(new File(filePath));
        apiRequest.addParam("file", file, ParamPosition.MULTIFORM, false);

        apiRequest.addParam("meetingId", meetingId, ParamPosition.QUERY, false);

        apiRequest.addParam("meetingKey", meetingKey, ParamPosition.QUERY, false);

        asyncInvoke(apiRequest, apiCallback, tag);
        //LogUtil.d("hj", "api Request: "+apiRequest.toString());
    }

}