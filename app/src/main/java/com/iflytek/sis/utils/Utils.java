package com.iflytek.sis.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.animation.LinearInterpolator;

import com.iflytek.sis.componant.MusicService;
import com.iflytek.sis.constant.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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


    /**
     * 获取uuid，作为表的主键
     * @return
     */
    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }

    /**
     * 获取系统当前时间
     * return 2018-04-19 10:23
     */
    public static String getCurrentTime() {
        String time = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        time = format.format(date);
        return time;
    }


    /**
     * 转换时间格式字符串为毫秒值
     * @param time 时间格式字符串
     * @return 毫秒值
     */
    public static long getMillionsFromString(String time){
        long result = 0l;
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = format.parse(time);
            result = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 转换long型time为时间格式
     * @param longTime long型time
     * @return 时间格式字串
     */
    public static String longtime2String(long longTime){
        String time = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(longTime);
        time = format.format(date);
        return time;
    }
}
