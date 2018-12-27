package com.iflytek.zst.taoqi.bean;

import java.util.TreeSet;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by DELL-5490 on 2018/7/3.
 */

public class VoiceTextBean extends RealmObject{
    @PrimaryKey
    int id;

    String name = "test";
    String oris = "";
    String trans = "";
    int time;
    //标记集合
    RealmList<MarkBean> marks;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public RealmList<MarkBean> getMarks() {
        return marks;
    }

    public void setMarks(RealmList<MarkBean> marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "VoiceTextBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", oris='" + oris + '\'' +
                ", trans='" + trans + '\'' +
                ", time=" + time +
                ", marks=" + marks +
                ", hasPoint=" + hasPoint +
                ", updateLength=" + updateLength +
                ", langauge='" + langauge + '\'' +
                ", isEnd=" + isEnd +
                '}';
    }
}
