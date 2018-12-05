package com.iflytek.zst.taoqi.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.ui.activity.base.BaseActivity;

public class RingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
    }

    private void stop(View view){
        this.finish();
    }
}
