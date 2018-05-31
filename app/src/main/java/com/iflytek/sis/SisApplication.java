package com.iflytek.sis;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by DELL-5490 on 2018/5/30.
 */

public class SisApplication extends Application {

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
    }

    public static Context getContext() {
        return context;
    }
}
