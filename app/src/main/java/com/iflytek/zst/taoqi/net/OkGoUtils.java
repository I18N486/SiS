package com.iflytek.zst.taoqi.net;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.iflytek.zst.taoqi.constant.Constants;
import com.iflytek.zst.taoqi.storage.sharedpreferences.MySharedpreferences;
import com.iflytek.zst.taoqi.ui.activity.MainActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * Created by DELL-5490 on 2018/11/28.
 */

public class OkGoUtils {
    public static void sendOkGoRequest(String url, StringCallback callback){
        OkGo.<String>get(url).execute(callback);
    }

    public static void loadBingImage(final Context context, final ImageView imageView){
        String imageUrl = MySharedpreferences.getInstance().getString(Constants.BIYING_IMAGEKEY,null);
        if (imageUrl != null){
            Glide.with(context).load(imageUrl).into(imageView);
        } else {
            OkGoUtils.sendOkGoRequest(Constants.BIYING_IMAGEURL, new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String imageUrl = response.body();
                    MySharedpreferences.getInstance().putString(Constants.BIYING_IMAGEKEY, imageUrl);
                    Glide.with(context).load(imageUrl).into(imageView);
                }
            });
        }
    }
}
