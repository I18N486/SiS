package com.iflytek.sis.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.iflytek.sis.utils.ActivityCollector;


public abstract class BaseActivity extends AppCompatActivity {

   public static Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("BaseActivty", getClass().getSimpleName());
        ActivityCollector.addActivty(this);
        context = this;
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
