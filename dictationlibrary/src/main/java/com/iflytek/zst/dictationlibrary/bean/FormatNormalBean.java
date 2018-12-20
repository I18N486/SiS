package com.iflytek.zst.dictationlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DELL-5490 on 2018/12/20.
 */

public class FormatNormalBean implements Parcelable{
    public String from;
    public String to;
    public String orisContent;
    public String transContent;

    public FormatNormalBean(){}

    protected FormatNormalBean(Parcel in) {
        from = in.readString();
        to = in.readString();
        orisContent = in.readString();
        transContent = in.readString();
    }

    public static final Creator<FormatNormalBean> CREATOR = new Creator<FormatNormalBean>() {
        @Override
        public FormatNormalBean createFromParcel(Parcel in) {
            return new FormatNormalBean(in);
        }

        @Override
        public FormatNormalBean[] newArray(int size) {
            return new FormatNormalBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(from);
        parcel.writeString(to);
        parcel.writeString(orisContent);
        parcel.writeString(transContent);
    }
}
