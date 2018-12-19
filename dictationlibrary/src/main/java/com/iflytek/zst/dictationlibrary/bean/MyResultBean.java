package com.iflytek.zst.dictationlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public class MyResultBean implements Parcelable{
    //标识结果类型：apd，rpl
    public String pgs;
    //识别结果
    public String content;
    //需要替换的字串长度（当前子句中确认的字串长度）
    public int replace;
    //是否是最后一次消息返回（标志本次会话结束）
    public boolean isEnd;

    public MyResultBean(){

    }

    protected MyResultBean(Parcel in) {
        pgs = in.readString();
        content = in.readString();
        replace = in.readInt();
        isEnd = in.readByte() != 0;
    }

    public static final Creator<MyResultBean> CREATOR = new Creator<MyResultBean>() {
        @Override
        public MyResultBean createFromParcel(Parcel in) {
            return new MyResultBean(in);
        }

        @Override
        public MyResultBean[] newArray(int size) {
            return new MyResultBean[size];
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
        return "MyResultBean{" +
                "pgs=" + pgs +
                ", content='" + content + '\'' +
                ", replace=" + replace +
                ", isEnd=" + isEnd +
                '}';
    }
}
