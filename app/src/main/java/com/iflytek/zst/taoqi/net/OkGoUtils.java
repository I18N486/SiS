package com.iflytek.zst.taoqi.net;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

/**
 * Created by DELL-5490 on 2018/11/28.
 */

public class OkGoUtils {
    public static void sendOkGoRequest(String url, StringCallback callback){
        OkGo.<String>get(url).execute(callback);
    }
}
