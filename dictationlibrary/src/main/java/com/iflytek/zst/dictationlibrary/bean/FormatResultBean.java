package com.iflytek.zst.dictationlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public class FormatResultBean implements Parcelable{
    //标识结果类型：apd，rpl
    public String pgs;
    //识别结果
    public String content;
    //需要替换的字串长度（当前子句中确认的字串长度）
    public int replace;
    //是否是最后一次消息返回（标志本次会话结束）
    public boolean isEnd;

    public FormatResultBean(){

    }

    protected FormatResultBean(Parcel in) {
        pgs = in.readString();
        content = in.readString();
        replace = in.readInt();
        isEnd = in.readByte() != 0;
    }

    public static final Creator<FormatResultBean> CREATOR = new Creator<FormatResultBean>() {
        @Override
        public FormatResultBean createFromParcel(Parcel in) {
            return new FormatResultBean(in);
        }

        @Override
        public FormatResultBean[] newArray(int size) {
            return new FormatResultBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(pgs);
        parcel.writeString(content);
        parcel.writeInt(replace);
        parcel.writeByte((byte) (isEnd ? 1 : 0));
    }

    @Override
    public String toString() {
        return "FormatResultBean{" +
                "pgs=" + pgs +
                ", content='" + content + '\'' +
                ", replace=" + replace +
                ", isEnd=" + isEnd +
                '}';
    }
}
