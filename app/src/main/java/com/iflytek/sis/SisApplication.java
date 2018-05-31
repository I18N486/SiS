package com.iflytek.sis;

import android.app.Application;
import android.content.Context;


/**
 * Created by DELL-5490 on 2018/5/30.
 */

public class SisApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
