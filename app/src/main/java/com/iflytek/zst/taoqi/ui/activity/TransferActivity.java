package com.iflytek.zst.taoqi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.ui.activity.base.BaseActivity;

public class TransferActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,TransferActivity.class);
        context.startActivity(intent);
    }
}
