package com.iflytek.sis.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.sis.R;
import com.iflytek.sis.bean.MemoBean;
import com.iflytek.sis.ui.view.LinedEditText;
import com.iflytek.sis.utils.Utils;

import org.feezu.liuli.timeselector.TimeSelector;

import java.util.UUID;

import io.realm.Realm;

public class MemoDetailActivity extends BaseActivity implements View.OnClickListener {

    String id;
    Realm mRealm;
    MemoBean memoBean = null;
    LinedEditText memoContent;
    ImageView memoMenu;
    ImageView memoSave;
    LinearLayout llTip;
    TextView tipTime;
    boolean isremind = false;
    long time;

    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);
        initData();
        initView();
        //获取闹钟的管理者
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    private void initView() {
        TextView title = findViewById(R.id.memo_title);
        title.setText("备忘录");
        memoMenu = findViewById(R.id.memo_menu);
        memoMenu.setOnClickListener(this);
        memoSave = findViewById(R.id.memo_save);
        memoSave.setOnClickListener(this);
        memoContent = findViewById(R.id.memo_content);
        memoContent.setPaperColor(getResources().getColor(R.color.mint_green));
        if (null != memoBean) {
            memoContent.setText(memoBean.getContent());
            isremind = memoBean.isRemind();
            time = memoBean.getTime();
        }
        ImageView btnRemind = findViewById(R.id.btn_remind);
        btnRemind.setOnClickListener(this);
        ImageView btnCamera = findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(this);
        llTip = findViewById(R.id.ll_detail_tip);
        if (isremind) {
            llTip.setVisibility(View.VISIBLE);
        }
        tipTime = findViewById(R.id.tv_tip_time);
        tipTime.setText(Utils.longtime2String(time));
        Button remindCancel = findViewById(R.id.remind_cancel);
        remindCancel.setOnClickListener(this);
    }

    private void initData() {
        mRealm = Realm.getDefaultInstance();
        id = getIntent().getStringExtra("id");
        if (id != null || !id.equals("")) {
            //id = ""，说明属于新建item
            memoBean = queryFromId();
        }
    }


    public static void actionStart(Context context, String id) {
        Intent intent = new Intent(context, MemoDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private MemoBean queryFromId() {
        MemoBean memoBean = mRealm.where(MemoBean.class).equalTo("id", id).findFirst();
        return memoBean;
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.memo_menu:
                break;
            case R.id.memo_save:
                addNewMemo();
                break;
            case R.id.btn_remind:
                showTimeSelector();
                break;
            case R.id.remind_cancel:
                isremind = false;
                llTip.setVisibility(View.GONE);
                break;
        }
    }

    private void addNewMemo() {
        if (memoContent.getText() != null && !memoContent.getText().toString().equals("")) {
            if (id == null || id.equals("")) {
                memoBean = new MemoBean();
                memoBean.setId(Utils.getUUID());
            }
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                if (isremind) {
                    memoBean.setRemind(isremind);
                    memoBean.setTime(time);
                    Intent intent = new Intent();
                    intent.setAction("com.zst.love.lsq.RING");
                    //将来时态的跳转
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MemoDetailActivity.this, 0x101, intent, 0);
                    //设置闹钟
                    alarmManager.set(AlarmManager.RTC_WAKEUP, Utils.getMillionsFromString("2018-06-06 19:10"), pendingIntent);
                } else {
                    memoBean.setRemind(isremind);
                }
                memoBean.setContent(memoContent.getText().toString());
                mRealm.copyToRealmOrUpdate(memoBean);
                }
            });
        }
        this.finish();
    }

    private void showTimeSelector() {
        TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                MemoDetailActivity.this.time = Utils.getMillionsFromString(time);
                isremind = true;
                llTip.setVisibility(View.VISIBLE);
                tipTime.setText(time);
            }
        }, Utils.getCurrentTime(), "2021-12-21 13:14");
        timeSelector.setIsLoop(false);
        timeSelector.show();
    }

    @Override
    public void onBackPressed() {
        addNewMemo();
        this.finish();
    }
}
