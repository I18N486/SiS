package com.iflytek.zst.dictationibrary.utils;

import com.google.gson.Gson;
import com.iflytek.zst.dictationibrary.bean.DictationResultbean;
import com.iflytek.zst.dictationibrary.bean.MyResultBean;
import com.iflytek.zst.dictationibrary.constants.Constants;

import java.util.List;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public class DictationResultFormat {
    public static MyResultBean formatIatResult(String jsonData){
        MyResultBean myResultBean = new MyResultBean();
        StringBuilder contentBuffer = new StringBuilder();
        DictationResultbean dictationResultbean = new Gson().fromJson(jsonData,DictationResultbean.class);
        List<DictationResultbean.WsBean.CwBean> cwBeans = dictationResultbean.getWs().get(0).getCw();
        if (dictationResultbean.getPgs().equals(Constants.SENTENCEUPDATE)){
            for (int i = dictationResultbean.getRg().get(0);i<cwBeans.size();i++){
                contentBuffer.append(cwBeans.get(i).getW());
            }
            myResultBean.type = Constants.TYPE_UPDATE;
        } else if (dictationResultbean.getPgs().equals(Constants.SENTENCEEND)){
            for (int i = 0;i<cwBeans.size();i++){
                contentBuffer.append(cwBeans.get(i).getW());
            }
            myResultBean.type = Constants.TYPE_END;
        }
        myResultBean.content = contentBuffer.toString();
        myResultBean.isEnd = dictationResultbean.isLs();
        return myResultBean;
    }
}
