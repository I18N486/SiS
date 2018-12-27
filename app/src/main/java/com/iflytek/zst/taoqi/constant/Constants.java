package com.iflytek.zst.taoqi.constant;

/**
 * Created by DELL-5490 on 2018/5/30.
 */

public class Constants {

    //听写引擎识别appid
    public static final String APPID = "5c173ea4";
    //翻译引擎appid
    public static final String APP_ID_HUIYI = "5ae146f9";//慧议

    //音乐控制指令tag
    public static final String MUSIC_START = "START";
    public static final String MUSIC_PAUSE_GOON = "PAUSE&GOON";
    public static final String MUSIC_STOP = "STOP";

    //必应每日图片url
    public static final String BIYING_IMAGEURL = "http://guolin.tech/api/bing_pic";
    //图片存储key（sharedpreferences）
    public static final String BIYING_IMAGEKEY = "by_image_key";


    //引擎支持的语种
    public static final String LANGUAGE_CHINESE = "中";
    public static final String LANGUAGE_ENGLISH = "英";
    public static final String LANGUAGE_JAPANESE = "日";
    public static final String LANGUAGE_KOREAN = "韩";
    public static final String LANGUAGE_FRENCH = "法";
    public static final String LANGUAGE_SPANISH = "西";
    public static final String LANGUAGE_RUSSIAN = "俄";

    //每段会话最长字数限制
    public static final int MAXSENTENCELENGTH = 200;
    //主显示界面可显示的最大数目
    public static final int MAXSHOWSIZE = 50;
    //主显示界面每次下拉加载的最大数目
    public static final int MAXSWIPEREFRESHSIZE = 10;

    //引擎结束消息what
    public static final int WHAT_DICTATIONEND = 101;
}
