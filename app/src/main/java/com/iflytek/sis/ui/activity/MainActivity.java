package com.iflytek.sis.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.iflytek.sis.R;
import com.iflytek.sis.bean.MemoBean;
import com.iflytek.sis.componant.MusicService;
import com.iflytek.sis.constant.Constants;
import com.iflytek.sis.utils.Utils;

import java.util.UUID;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnMusic;
    private ObjectAnimator animator;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        requesPermission();
        GdMapActivity.actionStart(this);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //设置类型
//        intent.setType("audio/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, 1);
        MusicService.actionStart(this, Constants.MUSIC_START);
    }

    private void initData() {
        mRealm = Realm.getDefaultInstance();
    }

    private void initView() {
        btnMusic = findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(btnMusic, "rotation", 0.0f, 359.0f);
        Utils.startMusicAnimator(animator);
        Button add = findViewById(R.id.btn_add);
        add.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            Uri uri = data.getData();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                //String path = getPath(this, uri);
                //file = new File("file://" + path);

            } else {//4.4一下系统调用方法
                String realPathFromURI = getRealPathFromURI(uri);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    @Override
    protected void onDestroy() {
        MusicService.actionStop(this);
        mRealm.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_music:
                Utils.musicBtnClick(this,animator);
                break;
            case R.id.btn_add:
                for (int i=0;i<5;i++) {
                    final MemoBean memoBean = new MemoBean();
                    memoBean.setId(Utils.getUUID());
                    memoBean.setContent(i+"测试realm，这是测试数据！");
                    memoBean.setRemind(true);
                    memoBean.setTime(System.currentTimeMillis());
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mRealm.copyToRealmOrUpdate(memoBean);
                        }
                    });
                }
                MemoActivity.actionStart(this);
                break;
        }
    }

    private void requesPermission(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1001);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
