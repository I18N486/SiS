package com.iflytek.zst.taoqi.ui.activity.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.iflytek.zst.taoqi.utils.ActivityCollector;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("BaseActivty", getClass().getSimpleName());
        ActivityCollector.addActivty(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 页面启动方法（每个界面实现自己的方法，最好做成静态方法）
     */
    //public abstract void actionStart();


}
