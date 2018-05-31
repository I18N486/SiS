package com.iflytek.sis.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.iflytek.sis.R;
import com.iflytek.sis.bean.MemoBean;
import com.iflytek.sis.componant.adapter.MemoAdapter;
import com.iflytek.sis.utils.Utils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MemoActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView memoList;
    List<MemoBean> memoBeanList;
    ImageView btnMusic;
    ObjectAnimator animator;
    Realm mRealm;
    MemoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        initData();
        initView();
    }

    private void initData() {
        mRealm = Realm.getDefaultInstance();
        memoBeanList = queryAllMemo();
        adapter = new MemoAdapter(memoBeanList);
        adapter.setmItemviewListener(new MemoAdapter.ItemviewClickListener() {
            @Override
            public void onClickListener() {

            }
        });
        adapter.setmItemviewLongListener(new MemoAdapter.ItemviewLongclickListener() {
            @Override
            public void onClickListener() {

            }
        });
    }

    private void initView(){
        btnMusic = findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(btnMusic, "rotation", 0.0f, 359.0f);
        Utils.startMusicAnimator(animator);
        memoList = findViewById(R.id.memo_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        memoList.setLayoutManager(manager);
        memoList.setAdapter(adapter);
    }

    private List<MemoBean> queryAllMemo(){
        RealmResults<MemoBean> memoBeans = mRealm.where(MemoBean.class).findAll();
        return mRealm.copyFromRealm(memoBeans);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,MemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_music:
                Utils.musicBtnClick(this,animator);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }
}
