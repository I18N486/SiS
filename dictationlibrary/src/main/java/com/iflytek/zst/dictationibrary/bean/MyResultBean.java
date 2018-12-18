package com.iflytek.zst.dictationibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DELL-5490 on 2018/12/18.
 */

public class MyResultBean implements Parcelable{
    public int type;
    public String content;
    public boolean isEnd;

    public MyResultBean(){

    }

    protected MyResultBean(Parcel in) {
        type = in.readInt();
        content = in.readString();
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

        parcel.writeInt(type);
        parcel.writeString(content);
        parcel.writeByte((byte) (isEnd ? 1 : 0));
    }
}
