package com.iflytek.zst.taoqi.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.TaoQiApplication;
import com.iflytek.zst.taoqi.bean.MemoBean;
import com.iflytek.zst.taoqi.componant.adapter.MemoAdapter;
import com.iflytek.zst.taoqi.ui.activity.base.BaseActivity;
import com.iflytek.zst.taoqi.utils.Utils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MemoActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView memoList;
    RealmResults<MemoBean> memoBeanList;
    List<MemoBean> memoBeansList;
    ImageView btnMusic;
    ObjectAnimator animator;
    Realm mRealm;
    MemoAdapter adapter;
    ImageView btnFind;
    EditText findText;
    Button btnCancel;
    LinearLayout mCreate;
    FrameLayout parent;
    LinearLayoutManager manager;
    PopupWindow popupWindow;

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
        memoBeansList = mRealm.copyFromRealm(memoBeanList);
        adapter = new MemoAdapter(memoBeansList);
        setAdapterListener();
        popupWindow = new PopupWindow(this);
    }

    private void initView(){
        parent = findViewById(R.id.fm_parent);
        btnMusic = findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(btnMusic, "rotation", 0.0f, 359.0f);
        Utils.startMusicAnimator(animator);
        memoList = findViewById(R.id.memo_list);
        manager = new LinearLayoutManager(this);
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
    private RealmResults<MemoBean> queryAllMemo(){
        RealmResults<MemoBean> memoBeans = null;
        memoBeans = mRealm.where(MemoBean.class).findAll();
        //return mRealm.copyFromRealm(memoBeans);
        return memoBeans;
    }


    /**
     * 根据输入的搜索字串，进行条件查询
     * @param condition 搜索字串
     * @return
     */
    private List<MemoBean> queryFromFind(String condition){
        RealmResults<MemoBean> memoBeans = null;
        memoBeans = mRealm.where(MemoBean.class).contains("content",condition).findAll();
        return memoBeans;
    }


    /**
     * 根据index删除指定表中item
     * @param position
     */
    private void deleteItem(final int position){
        memoBeanList = queryAllMemo();
        if (memoBeanList==null || memoBeanList.size()==0){
            return;
        }
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memoBeanList.get(position).deleteFromRealm();
            }
        });
        memoBeansList.remove(position);
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
                MemoDetailActivity.actionStart(this,"");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeRecycler(queryAllMemo());
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    private void resumeRecycler(List<MemoBean> memoBeanList){
        memoBeansList = mRealm.copyFromRealm(memoBeanList);
        adapter = new MemoAdapter(memoBeanList);
        setAdapterListener();
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
                popupWindow.dismiss();
            }
        });
        LinearLayout cancel = view.findViewById(R.id.item_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelChoose(position);
                popupWindow.dismiss();
            }
        });
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                cancelChoose(position);
            }
        });
    }

    private void cancelChoose(int position){
        View hodler = manager.findViewByPosition(position);
        if (hodler != null) {
            LinearLayout llItem = hodler.findViewById(R.id.ll_memo_item);
            llItem.setBackgroundColor(TaoQiApplication.getContext().getResources().getColor(R.color.white));
        }
    }
}
