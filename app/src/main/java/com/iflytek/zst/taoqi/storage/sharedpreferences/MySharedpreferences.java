package com.iflytek.zst.taoqi.storage.sharedpreferences;

import android.content.Context;

import com.iflytek.zst.taoqi.TaoQiApplication;

/**
 * Created by DELL-5490 on 2018/12/13.
 */

public class MySharedpreferences extends SharedPreferencesAdapter {

    protected MySharedpreferences() {
        super(TaoQiApplication.getContext());
    }

    @Override
    protected String getSharePreferencesName() {
        return TaoQiApplication.getContext().getPackageName();
    }

    public static MySharedpreferences getInstance(){
        return InstanceHolder.mInstance;
    }

    public static class InstanceHolder{
        public static MySharedpreferences mInstance = new MySharedpreferences();
    }
}
