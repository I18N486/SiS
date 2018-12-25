package com.iflytek.zst.dictationlibrary.utils;

import android.util.ArrayMap;

/**
 * Created by 胡青青 on 2018/12/11.
 * 邮箱 qqhu2@iflytek.com
 */
public class LanguageUtils {
    public static final String P_LANGUAGE_JA = "ja";  //日
    public static final String P_LANGUAGE_KO = "ko";  //韩
    public static final String P_LANGUAGE_FR = "fr";  //法
    public static final String P_LANGUAGE_ES = "es";  //西
    public static final String P_LANGUAGE_RU = "ru";  //俄
    public static final String P_LANGUAGE_CN = "cn";  //中
    public static final String P_LANGUAGE_EN = "en";  //英
    public static final String P_LANGUAGE_RU_RU = "俄";
    public static final String P_LANGUAGE_CN_CN = "中";
    public static final String P_LANGUAGE_JA_JA = "日";
    public static final String P_LANGUAGE_FR_FR = "法";
    public static final String P_LANGUAGE_ES_ES = "西";
    public static final String P_LANGUAGE_KO_KO = "韩";
    public static final String P_LANGUAGE_EN_EN = "英";

    public static final String PP_LANGUAGE_RU_RU = "俄语";
    public static final String PP_LANGUAGE_CN_CN = "中文";
    public static final String PP_LANGUAGE_JA_JA = "日语";
    public static final String PP_LANGUAGE_FR_FR = "法语";
    public static final String PP_LANGUAGE_ES_ES = "西班牙语";
    public static final String PP_LANGUAGE_KO_KO = "韩语";
    public static final String PP_LANGUAGE_EN_EN = "英语";


    public static final ArrayMap<String, String> languages = new ArrayMap<>();

    static {

        languages.put(P_LANGUAGE_RU, P_LANGUAGE_RU_RU);
        languages.put(P_LANGUAGE_CN, P_LANGUAGE_CN_CN);
        languages.put(P_LANGUAGE_JA, P_LANGUAGE_JA_JA);
        languages.put(P_LANGUAGE_FR, P_LANGUAGE_FR_FR);
        languages.put(P_LANGUAGE_ES, P_LANGUAGE_ES_ES);
        languages.put(P_LANGUAGE_EN, P_LANGUAGE_EN_EN);
        languages.put(P_LANGUAGE_KO, P_LANGUAGE_KO_KO);

        languages.put(P_LANGUAGE_RU_RU, P_LANGUAGE_RU);
        languages.put(P_LANGUAGE_CN_CN, P_LANGUAGE_CN);
        languages.put(P_LANGUAGE_JA_JA, P_LANGUAGE_JA);
        languages.put(P_LANGUAGE_FR_FR, P_LANGUAGE_FR);
        languages.put(P_LANGUAGE_ES_ES, P_LANGUAGE_ES);
        languages.put(P_LANGUAGE_EN_EN, P_LANGUAGE_EN);
        languages.put(P_LANGUAGE_KO_KO, P_LANGUAGE_KO);

        languages.put(PP_LANGUAGE_RU_RU, P_LANGUAGE_RU);
        languages.put(PP_LANGUAGE_CN_CN, P_LANGUAGE_CN);
        languages.put(PP_LANGUAGE_JA_JA, P_LANGUAGE_JA);
        languages.put(PP_LANGUAGE_FR_FR, P_LANGUAGE_FR);
        languages.put(PP_LANGUAGE_ES_ES, P_LANGUAGE_ES);
        languages.put(PP_LANGUAGE_EN_EN, P_LANGUAGE_EN);
        languages.put(PP_LANGUAGE_KO_KO, P_LANGUAGE_KO);




    }

}
