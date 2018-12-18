package com.iflytek.zst.taoqi.bean;

import java.util.TreeSet;

import io.realm.annotations.PrimaryKey;

/**
 * Created by DELL-5490 on 2018/7/3.
 */

public class VoiceTextBean {
    @PrimaryKey
    String id;

    String name = "test";
    String oris = "";
    String trans = "";
    int time;
    //标记集合
    TreeSet<MarkBean> marks;
    boolean hasPoint;
    //需要高亮显示的文字长度
    public int updateLength;
    //源语种
    public String langauge;

    //是否结束
    public boolean isEnd;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOris() {
        return oris;
    }

    public void setOris(String oris) {
        this.oris = oris;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public boolean isHasPoint() {
        return hasPoint;
    }

    public void setHasPoint(boolean hasPoint) {
        this.hasPoint = hasPoint;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public TreeSet<MarkBean> getMarks() {
        return marks;
    }

    public void setMarks(TreeSet<MarkBean> marks) {
        this.marks = marks;
    }


}
