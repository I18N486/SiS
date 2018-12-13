package com.zst.msclibrary.transfermsc.openapi.consts;

/**
 * Created by qbsong@iflytek.com on 2018/9/27 20:32.
 * 热词的类型枚举
 */
public enum HotWordType {
    /**
     * 全局热词
     */
    GLOBAL("global"),
    /**
     * 个性化热词
     */
    INDIVIDUAL("individual");

    private String type;

    HotWordType(String type) {
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
