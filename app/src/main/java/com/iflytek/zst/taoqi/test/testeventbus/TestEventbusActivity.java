package com.iflytek.zst.taoqi.test.testeventbus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.iflytek.zst.taoqi.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestEventbusActivity extends AppCompatActivity {

    public static final String INTENT_SERVICE_ACTION = "eventbus_action";
    @BindView(R.id.btn_test)
    Button btnTest;
    @Inject
    DaggerClass daggerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_eventbus);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        DaggerDaggerComponent.create().inject(this);
        Log.e("dagger",daggerClass.hashCode()+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(MyEvent event) {
        Log.e("TestEventbusActivity", "msg = " + event.msg);
    }

    @OnClick(R.id.btn_test)
    public void onClick(){
        Intent intent = new Intent(this,MyIntentService.class);
        intent.setAction(TestEventbusActivity.INTENT_SERVICE_ACTION);
        startService(intent);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,TestEventbusActivity.class);
        context.startActivity(intent);
    }
}
