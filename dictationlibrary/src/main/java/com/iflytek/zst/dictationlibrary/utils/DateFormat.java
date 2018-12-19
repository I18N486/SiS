package com.iflytek.zst.dictationlibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DELL-5490 on 2018/12/17.
 */

public class DateFormat {
    public static final String DATE_SECOND = "yyyyMMdd_HHmm";

    public static String nowTimeFormat(){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_SECOND);
        String dateFormat = sdf.format(new Date());
        return dateFormat;
    }
}
