package com.zst.msclibrary.transfermsc.event;

/**
 * @author changwu on 2018/11/28 tel|wechat 18656086531
 * @des
 */
public class RecognizerStateEvent {

    private int state;
    private String failReason;

    public RecognizerStateEvent(int state, String failReason) {
        this.state = state;
        this.failReason = failReason;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
