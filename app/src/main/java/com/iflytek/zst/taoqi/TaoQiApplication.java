package com.iflytek.zst.taoqi;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.iflytek.zst.dictationlibrary.online.RecognizerEngine;
import com.iflytek.zst.taoqi.constant.Constants;
import com.lzy.okgo.OkGo;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by DELL-5490 on 2018/5/30.
 */

public class TaoQiApplication extends MultiDexApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("sisRealm.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        OkGo.getInstance().init(this);
        RecognizerEngine.init(context, Constants.APPID,Constants.APP_ID_HUIYI);
    }

    public static Context getContext() {
        return context;
    }
}
