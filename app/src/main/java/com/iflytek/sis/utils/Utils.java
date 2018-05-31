package com.iflytek.sis.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;

import com.iflytek.sis.componant.MusicService;
import com.iflytek.sis.constant.Constants;

/**
 * Created by DELL-5490 on 2018/5/31.
 */

public class Utils {

    /**
     * 开始音乐按钮动画
     * @param animator
     */
    public static void startMusicAnimator(ObjectAnimator animator){
        //持续时长6秒
        animator.setDuration(6000);
        //匀速
        animator.setInterpolator(new LinearInterpolator());
        //循环
        animator.setRepeatCount(-1);
        //一个流程走完重新开始
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }


    /**
     * 执行音乐按钮点击逻辑
     * @param animator
     */
    public static void musicBtnClick(Context context,ObjectAnimator animator){
        MusicService.actionStart(context, Constants.MUSIC_PAUSE_GOON);
        if (animator == null){
            return;
        } else {
            if (animator.isPaused()){
                animator.resume();
            } else {
                animator.pause();
            }
        }
    }
}
