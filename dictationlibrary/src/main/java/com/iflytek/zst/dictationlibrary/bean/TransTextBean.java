package com.iflytek.zst.dictationlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DELL-5490 on 2018/12/19.
 */

public class TransTextBean implements Parcelable{
    public String appid;
    public String sn;
    public String from;
    public String to;
    public String srcStr;
    public String longitude;
    public String latitude;
    public int applicationType;

    public TransTextBean(){}

    protected TransTextBean(Parcel in) {
        appid = in.readString();
        sn = in.readString();
        from = in.readString();
        to = in.readString();
        srcStr = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        applicationType = in.readInt();
    }

    public static final Creator<TransTextBean> CREATOR = new Creator<TransTextBean>() {
        @Override
        public TransTextBean createFromParcel(Parcel in) {
            return new TransTextBean(in);
        }

        @Override
        public TransTextBean[] newArray(int size) {
            return new TransTextBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(appid);
        parcel.writeString(sn);
        parcel.writeString(from);
        parcel.writeString(to);
        parcel.writeString(srcStr);
        parcel.writeString(longitude);
        parcel.writeString(latitude);
        parcel.writeInt(applicationType);
    }
}
