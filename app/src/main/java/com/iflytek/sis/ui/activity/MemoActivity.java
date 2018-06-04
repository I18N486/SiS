package com.iflytek.sis.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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
    ImageView btnFind;
    EditText findText;
    Button btnCancel;
    LinearLayout mCreate;
    FrameLayout parent;

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
        setAdapterListener();
    }

    private void initView(){
        parent = findViewById(R.id.fm_parent);
        btnMusic = findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(btnMusic, "rotation", 0.0f, 359.0f);
        Utils.startMusicAnimator(animator);
        memoList = findViewById(R.id.memo_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        memoList.setLayoutManager(manager);
        memoList.setAdapter(adapter);
        btnFind = findViewById(R.id.btn_find);
        btnFind.setOnClickListener(this);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        findText = findViewById(R.id.find_text);
        mCreate = findViewById(R.id.ll_create);
        mCreate.setOnClickListener(this);
    }

    /**
     * 查询所有数据
     * @return
     */
    private List<MemoBean> queryAllMemo(){
        RealmResults<MemoBean> memoBeans = mRealm.where(MemoBean.class).findAll();
        return mRealm.copyFromRealm(memoBeans);
    }


    /**
     * 根据输入的搜索字串，进行条件查询
     * @param condition 搜索字串
     * @return
     */
    private List<MemoBean> queryFromFind(String condition){
        RealmResults<MemoBean> memoBeans = mRealm.where(MemoBean.class).contains("content",condition).findAll();
        return mRealm.copyFromRealm(memoBeans);
    }


    /**
     * 根据index删除指定表中item
     * @param position
     */
    private void deleteItem(final int position){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memoBeanList.get(position).deleteFromRealm();
            }
        });
        memoBeanList.remove(position);
        adapter.notifyDataSetChanged();
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
            case R.id.btn_find:
                String condition = findText.getText().toString();
                if (condition!=null && !condition.equals("")){
                    resumeRecycler(queryFromFind(condition));
                }
                break;
            case R.id.btn_cancel:
                resumeRecycler(queryAllMemo());
                break;
            case R.id.ll_create:
                MemoDetailActivity.actionStart(this,-1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    private void resumeRecycler(List<MemoBean> memoBeanList){
        adapter = new MemoAdapter(memoBeanList);
        memoList.setAdapter(adapter);
    }

    private void setAdapterListener(){
        adapter.setmItemviewListener(new MemoAdapter.ItemviewClickListener() {
            @Override
            public void onClickListener(int position) {
                MemoDetailActivity.actionStart(MemoActivity.this,memoBeanList.get(position).getId());
            }
        });
        adapter.setmItemviewLongListener(new MemoAdapter.ItemviewLongclickListener() {
            @Override
            public void onClickListener(int position) {
                showPopuWindow(position);
            }
        });
    }

    private void showPopuWindow(final int position){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_memo_popup,null);
        LinearLayout delete = view.findViewById(R.id.item_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
            }
        });
        LinearLayout cancel = view.findViewById(R.id.item_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}
