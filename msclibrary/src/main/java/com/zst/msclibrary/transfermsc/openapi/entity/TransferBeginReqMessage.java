package com.zst.msclibrary.transfermsc.openapi.entity;

import com.zst.msclibrary.transfermsc.openapi.consts.TransferCommand;

/**
 * Created by qbsong@iflytek.com on 2018/9/7 13:50.
 * 开始转写的请求参数
 */
public class TransferBeginReqMessage {
    private String action;
    private String format;
    private String resType;

    public TransferBeginReqMessage() {
        this.action = TransferCommand.BEGIN.getCommand();
        this.format = "pcm";
        this.resType = "0";
    }

    public TransferBeginReqMessage(String resType) {
        this.action = TransferCommand.BEGIN.getCommand();
        this.format = "pcm";
        this.resType = resType;
    }

    public TransferBeginReqMessage(String format, String resType) {
        this.action = TransferCommand.BEGIN.getCommand();
        this.format = format;
        this.resType = resType;
    }

    /**
     * action getter
     *
     * @return ：return action with type java.lang.String
     */
    public String getAction() {
        return action;
    }

    /**
     * action setter
     *
     * @param action : action with type java.lang.String
     * @return : TransferBeginReqMessage
     */
    public TransferBeginReqMessage setAction(String action) {
        this.action = action;
        return this;
    }

    /**
     * format getter
     *
     * @return ：return format with type java.lang.String
     */
    public String getFormat() {
        return format;
    }

    /**
     * format setter
     *
     * @param format : format with type java.lang.String
     * @return : TransferBeginReqMessage
     */
    public TransferBeginReqMessage setFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * resType getter
     *
     * @return ：return resType with type java.lang.String
     */
    public String getResType() {
        return resType;
    }

    /**
     * resType setter
     *
     * @param resType : resType with type java.lang.String
     * @return : TransferBeginReqMessage
     */
    public TransferBeginReqMessage setResType(String resType) {
        this.resType = resType;
        return this;
    }
}
