package com.iflytek.zst.dictationlibrary.utils;

import com.google.gson.Gson;
import com.iflytek.zst.dictationlibrary.bean.FormatNormalBean;
import com.iflytek.zst.dictationlibrary.bean.FormatResultBean;
import com.iflytek.zst.dictationlibrary.bean.NormalResultBean;
import com.iflytek.zst.dictationlibrary.bean.PgsResultbean;
import com.iflytek.zst.dictationlibrary.constants.DictationConstants;

import java.util.List;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public class DictationResultFormat {

    /**
     * 格式化pgs的json数据
     * @param jsonData
     * @return
     */
    public static FormatResultBean formatPgsIatResult(String jsonData){
        int start = 0;
        int end = 0;
        FormatResultBean formatResultBean = new FormatResultBean();
        StringBuilder contentBuffer = new StringBuilder();
        PgsResultbean pgsResultbean = new Gson().fromJson(jsonData,PgsResultbean.class);
        List<PgsResultbean.WsBean> wsBeans = pgsResultbean.getWs();
        if (pgsResultbean.getRg() != null && pgsResultbean.getRg().size()>0){
            start = pgsResultbean.getRg().get(0);
            end = pgsResultbean.getRg().get(1);
        }
        for (int i = 0;i<wsBeans.size();i++){
            //get（0），默认取候选词列表中的第一个
            contentBuffer.append(wsBeans.get(i).getCw().get(0).getW());
            if (pgsResultbean.getPgs().equals(DictationConstants.SENTENCEUPDATE) && i == end-start){
                formatResultBean.replace = contentBuffer.toString().length();
            }
        }
        formatResultBean.pgs = pgsResultbean.getPgs();
        formatResultBean.content = contentBuffer.toString();
        formatResultBean.isEnd = pgsResultbean.isLs();
        return formatResultBean;
    }

    /**
     * 格式化普通的json数据
     * @param jsonData
     * @return
     */
    public static FormatNormalBean formatNormalIatResult(String jsonData){
        FormatNormalBean formatNormalBean = new FormatNormalBean();
        NormalResultBean normalResultBean = new Gson().fromJson(jsonData,NormalResultBean.class);
        //默认取候选词列表中第一个(get(0))
        if (normalResultBean.getResults() != null) {
            NormalResultBean.ResultsBean resultsBean = normalResultBean.getResults().get(0);
            formatNormalBean.from = resultsBean.getOriLangCountry();
            formatNormalBean.to = resultsBean.getTransLangCountry();
            formatNormalBean.orisContent = resultsBean.getOriginal();
            formatNormalBean.transContent = resultsBean.getTranslated();
        }
        return formatNormalBean;
    }
}
