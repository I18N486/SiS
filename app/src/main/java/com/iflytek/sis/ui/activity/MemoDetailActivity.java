package com.iflytek.sis.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.sis.R;
import com.iflytek.sis.bean.MemoBean;
import com.iflytek.sis.ui.view.LinedEditText;

import io.realm.Realm;

public class MemoDetailActivity extends BaseActivity implements View.OnClickListener{

    int id;
    Realm mRealm;
    MemoBean memoBean = null;
    Toolbar mToolbar;
    LinedEditText memoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);
        initData();
        initView();

    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        TextView title = mToolbar.findViewById(R.id.toolbar_title);
        title.setText("备忘录");
        ImageView menu = mToolbar.findViewById(R.id.btn_menu);
        menu.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        memoContent = findViewById(R.id.memo_content);
        memoContent.setPaperColor(getResources().getColor(R.color.colorPrimary));
        if (null != memoBean) {
            memoContent.setText(memoBean.getContent());
        }
    }

    private void initData() {
        mRealm = Realm.getDefaultInstance();
        id = getIntent().getIntExtra("id",-1);
        if (-1 != id){
            //id = -1，说明属于新建item
            memoBean = queryFromId();
        }
    }


    public static void actionStart(Context context,int id){
        Intent intent = new Intent(context,MemoDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    private MemoBean queryFromId(){
        MemoBean memoBean = mRealm.where(MemoBean.class).equalTo("id",id).findFirst();
        return memoBean;
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }
}
