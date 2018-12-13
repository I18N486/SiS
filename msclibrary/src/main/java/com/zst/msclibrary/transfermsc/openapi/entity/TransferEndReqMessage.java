package com.zst.msclibrary.transfermsc.openapi.entity;


import com.zst.msclibrary.transfermsc.openapi.consts.TransferCommand;

/**
 * Created by qbsong@iflytek.com on 2018/9/7 13:51.
 */
public class TransferEndReqMessage {
    private String action;
    private boolean abort;

    public TransferEndReqMessage() {
        this.action = TransferCommand.END.getCommand();
        abort = false;
    }

    public TransferEndReqMessage(boolean abort) {
        this.action = TransferCommand.END.getCommand();
        this.abort = abort;
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
     * @return : TransferEndReqMessage
     */
    public TransferEndReqMessage setAction(String action) {
        this.action = action;
        return this;
    }

    /**
     * abort getter
     *
     * @return ：return abort with type boolean
     */
    public boolean isAbort() {
        return abort;
    }

    /**
     * abort setter
     *
     * @param abort : abort with type boolean
     * @return : TransferEndReqMessage
     */
    public TransferEndReqMessage setAbort(boolean abort) {
        this.abort = abort;
        return this;
    }
}
