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
        int start = 0;
        int end = 0;
        MyResultBean myResultBean = new MyResultBean();
        StringBuilder contentBuffer = new StringBuilder();
        DictationResultbean dictationResultbean = new Gson().fromJson(jsonData,DictationResultbean.class);
        List<DictationResultbean.WsBean> wsBeans = dictationResultbean.getWs();
        if (dictationResultbean.getRg().size()>0){
            start = dictationResultbean.getRg().get(0);
            end = dictationResultbean.getRg().get(1);
        }
        for (int i = 0;i<wsBeans.size();i++){
            //get（0），默认取候选词列表中的第一个
            contentBuffer.append(wsBeans.get(i).getCw().get(0).getW());
            if (dictationResultbean.getPgs().equals(Constants.SENTENCEUPDATE) && i == end-start){
                myResultBean.replace = contentBuffer.toString().length();
            }
        }
        myResultBean.pgs = dictationResultbean.getPgs();
        myResultBean.content = contentBuffer.toString();
        myResultBean.isEnd = dictationResultbean.isLs();
        return myResultBean;
    }
}
