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
import com.iflytek.sis.utils.Utils;

import java.util.UUID;

import io.realm.Realm;

public class MemoDetailActivity extends BaseActivity implements View.OnClickListener{

    String id;
    Realm mRealm;
    MemoBean memoBean = null;
    LinedEditText memoContent;
    ImageView memoMenu;
    ImageView memoSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);
        initData();
        initView();

    }

    private void initView() {
        TextView title = findViewById(R.id.memo_title);
        title.setText("备忘录");
        memoMenu = findViewById(R.id.memo_menu);
        memoMenu.setOnClickListener(this);
        memoSave = findViewById(R.id.memo_save);
        memoSave.setOnClickListener(this);
        memoContent = findViewById(R.id.memo_content);
        memoContent.setPaperColor(getResources().getColor(R.color.colorPrimary));
        if (null != memoBean) {
            memoContent.setText(memoBean.getContent());
        }
    }

    private void initData() {
        mRealm = Realm.getDefaultInstance();
        id = getIntent().getStringExtra("id");
        if (id != null || !id.equals("")){
            //id = ""，说明属于新建item
            memoBean = queryFromId();
        }
    }


    public static void actionStart(Context context,String id){
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
            case R.id.memo_menu:
                break;
            case R.id.memo_save:
                addNewMemo();
                break;
        }
    }

    private void addNewMemo(){
        if (memoContent.getText() != null || !memoContent.getText().toString().equals("")){
            if (id == null || id.equals("")) {
                memoBean = new MemoBean();
                memoBean.setId(Utils.getUUID());
            }
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    memoBean.setContent(memoContent.getText().toString());
                    mRealm.copyToRealmOrUpdate(memoBean);
                }
            });
        }
        this.finish();
    }
}
