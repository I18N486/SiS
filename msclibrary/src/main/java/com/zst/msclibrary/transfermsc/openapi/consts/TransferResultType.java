package com.zst.msclibrary.transfermsc.openapi.consts;

/**
 * 转写结果类型
 * Created by qbsong@iflytek.com on 2018/9/7 17:19.
 */
public enum TransferResultType {
    SENTENCE("0"), WORDS("1");

    private String type;

    TransferResultType(String type) {
        this.type = type;
    }

    /**
     * type getter
     *
     * @return ：return type with type java.lang.String
     */
    public String getType() {
        return type;
    }
}
