package com.zst.msclibrary.transfermsc.transfer;

/*
 * Auth: DELL-5490
 * Date: 2018/11/19
 */
public class TransferBean {
    String appid;
    String sn;
    String from;
    String to;
    String srcStr;
    String longitude;
    String latitude;
    int applicationType;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSrcStr() {
        return srcStr;
    }

    public void setSrcStr(String srcStr) {
        this.srcStr = srcStr;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(int applicationType) {
        this.applicationType = applicationType;
    }
}
