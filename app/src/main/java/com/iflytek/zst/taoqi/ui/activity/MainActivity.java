package com.iflytek.zst.taoqi.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.bean.MemoBean;
import com.iflytek.zst.taoqi.componant.FloatWindowService;
import com.iflytek.zst.taoqi.componant.MusicService;
import com.iflytek.zst.taoqi.constant.Constants;
import com.iflytek.zst.taoqi.test.testeventbus.TestEventbusActivity;
import com.iflytek.zst.taoqi.ui.activity.base.BaseActivity;
import com.iflytek.zst.taoqi.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_background)
    ImageView mainBackground;
    @BindView(R.id.btn_memo)
    TextView btnMemo;
    @BindView(R.id.btn_diary)
    TextView btnDiary;
    @BindView(R.id.btn_transfer)
    TextView btnTransfer;
    @BindView(R.id.btn_location)
    TextView btnLocation;
    @BindView(R.id.btn_music)
    ImageView btnMusic;
    @BindView(R.id.btn_test)
    TextView btnTest;
    private ObjectAnimator animator;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initView();
        requesPermission();
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //设置类型
//        intent.setType("audio/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, 1);
        MusicService.actionStart(this, Constants.MUSIC_START);
        //TestEventbusActivity.actionStart(this);
    }

    private void initData() {
        mRealm = Realm.getDefaultInstance();
    }

    private void initView() {
        animator = ObjectAnimator.ofFloat(btnMusic, "rotation", 0.0f, 359.0f);
        Utils.startMusicAnimator(animator);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                //String path = getPath(this, uri);
                //file = new File("file://" + path);

            } else {//4.4一下系统调用方法
                String realPathFromURI = getRealPathFromURI(uri);
            }
        } else if (resultCode == 0) {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, FloatWindowService.class));
            } else {
                Toast.makeText(this, "你拒绝了悬浮窗权限", Toast.LENGTH_SHORT).show();
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
        FloatWindowService.actionStop(this);
        mRealm.close();
        super.onDestroy();
    }

    @OnClick({R.id.btn_memo, R.id.btn_diary, R.id.btn_transfer, R.id.btn_location, R.id.btn_music,R.id.btn_test})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_music:
                Utils.musicBtnClick(this, animator);
                break;
            case R.id.btn_memo:
                for (int i = 0; i < 5; i++) {
                    final MemoBean memoBean = new MemoBean();
                    memoBean.setId(Utils.getUUID());
                    memoBean.setContent(i + "测试realm，这是测试数据！");
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
            case R.id.btn_transfer:
                break;
            case R.id.btn_location:
                GdMapActivity.actionStart(this);
                break;
            case R.id.btn_test:
                testStartFloat();
                break;
        }
    }

    private void requesPermission() {
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
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SYSTEM_ALERT_WINDOW
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

    private void testStartFloat() {
        if (Settings.canDrawOverlays(this)) {
            FloatWindowService.actionStart(this);
        } else {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), 0);
        }
    }
}
