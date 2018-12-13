package com.zst.msclibrary.transfermsc.speech.msg;

/*
 * Auth: DELL-5490
 * Date: 2018/10/26
 */
public class SentenceResultEvent {
    public String result;       //转写结果
    public String transResult;      //翻译结果
    public String type;         //一句话的消息类型 0:最终结果，1:为中间结果
    public String start;        //0: 一段话开始， 1： 表示一段话中间
    public String name;         //说话人
    public String sn;              //SN 号
    public long bg;           //开始时间
    public long ed;           //结束时间
//    public String textType;     //cn: 中文， en: 英文
}
