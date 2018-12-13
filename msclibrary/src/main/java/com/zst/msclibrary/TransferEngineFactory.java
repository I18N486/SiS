package com.zst.msclibrary;

import android.content.Context;

import com.zst.msclibrary.impl.IMscUtil;
import com.zst.msclibrary.transfermsc.TransferMscUtil;

/**
 * Created by DELL-5490 on 2018/12/3.
 */

public class TransferEngineFactory {

    /**
     * 转写引擎
     */
    public static final int TRANSFERENGINE = 0;
    /**
     * 听写引擎
     */
    public static final int DICTATIONENGINE = 1;
    /**
     * AI+引擎
     */
    public static final int AIENGINE = 2;

    public static IMscUtil createMscUtil(Context context,int type, String appid){
        IMscUtil iMscUtil;
        switch (type){
            case TRANSFERENGINE:
                iMscUtil= TransferMscUtil.getInstance();
                iMscUtil.initMsc(context.getApplicationContext(), appid);
                return iMscUtil;
            case DICTATIONENGINE:

            case AIENGINE:
                //todo 返回AI+引擎实例
                return null;
            default:
                return null;
        }
    }
}
