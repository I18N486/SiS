package com.iflytek.zst.taoqi.utils;

/**
 * Created by zst on 2018/12/19.
 */

public class StringUtils {

    /**
     * 判断字串是否为空，空字串包含：null，“”，“ ”
     * @param str
     * @return
     */
    public static boolean isEmptyOrSpaces(String str){
        if (str == null || "".equals(str.trim())){
            return true;
        } else {
            return false;
        }
    }
}
