package com.iflytek.sis.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.iflytek.sis.R;
import com.iflytek.sis.componant.MusicService;
import com.iflytek.sis.constant.Constants;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnMusic;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //设置类型
//        intent.setType("audio/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, 1);
        MusicService.actionStart(this, Constants.MUSIC_START);
    }

    private void initView() {
        btnMusic = findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(btnMusic, "rotation", 0.0f, 359.0f);
        animator.setDuration(6000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
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
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_music:
                MusicService.actionStart(this,Constants.MUSIC_PAUSE_GOON);
                if (animator == null){
                    break;
                } else {
                    if (animator.isPaused()){
                        animator.resume();
                    } else {
                        animator.pause();
                    }
                }
                break;
        }
    }
}
