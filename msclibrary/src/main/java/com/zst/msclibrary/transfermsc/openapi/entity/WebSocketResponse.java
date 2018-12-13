package com.zst.msclibrary.transfermsc.openapi.entity;

import java.util.Map;

/**
 * websocket 返回消息
 */
public class WebSocketResponse {
    private String code;
    private String desc;
    private String action; //begin,send,end,result
    private Map data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
