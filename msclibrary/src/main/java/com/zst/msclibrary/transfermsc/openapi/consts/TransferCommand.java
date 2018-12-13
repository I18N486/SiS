package com.zst.msclibrary.transfermsc.openapi.consts;

/**
 * 转写控制命令
 * Created by qbsong@iflytek.com on 2018/9/7 16:42.
 */
public enum TransferCommand {
    OPEN("open"), BEGIN("begin"), END("end"), RESULT("result");

    private String command;

    TransferCommand(String command) {
        this.command = command;
    }

    /**
     * command getter
     *
     * @return ：return command with type java.lang.String
     */
    public String getCommand() {
        return command;
    }
}
