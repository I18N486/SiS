package com.zst.msclibrary.transfermsc.openapi.entity;

import com.zst.msclibrary.transfermsc.openapi.consts.RetCode;

import java.util.Collections;
import java.util.Map;

/**
 * Created by qbsong@iflytek.com on 2018/9/14 11:44.
 * 转写操作的响应结果，如开始、结束、发送等
 */
public class TransferResponse {
    /**
     * 返回状态码，以 SE （SDK Error）开头的表示SDK的错误，其他的表示服务器的错误
     */
    private String code;
    /**
     * 状态码的描述
     */
    private String desc;
    /**
     * 转写结果
     */
    private String transferId;
    /**
     * 服务器的业务参数
     */
    private Map data;

    public TransferResponse() {
    }

    public TransferResponse(WebSocketResponse response, String transferId) {
        this(response.getCode(), response.getDesc(), transferId, response.getData());
    }

    public TransferResponse(WebSocketResponse response) {
        this(response.getCode(), response.getDesc(), "", response.getData());
    }

    /**
     * 默认为 SDK 错误
     *
     * @param retCode
     */
    public TransferResponse(RetCode retCode) {
        this(retCode.getRetCode(), retCode.getDesc(), "", Collections.emptyMap());

    }

    /**
     * 默认为 SDK 错误
     *
     * @param retCode
     */
    public TransferResponse(RetCode retCode, String transferId) {
        this(retCode.getRetCode(), retCode.getDesc(), transferId, Collections.emptyMap());
    }

    public TransferResponse(RetCode retCode, String transferId, Map data) {
        this(retCode.getRetCode(), retCode.getDesc(), transferId, data);
    }

    public TransferResponse(String code, String desc, String transferId, Map data) {
        this.code = code;
        this.desc = desc;
        this.transferId = transferId;
        this.data = data;
    }

    /**
     * 检测转写返回是否成功
     *
     * @return ：
     */
    public boolean isSuccess() {
        return RetCode.SUCCESS.getRetCode().equals(this.code);
    }

    /**
     * code getter
     *
     * @return ：return code with type java.lang.String
     */
    public String getCode() {
        return code;
    }

    /**
     * code setter
     *
     * @param code : code with type java.lang.String
     * @return : TransferResponse
     */
    public TransferResponse setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * desc getter
     *
     * @return ：return desc with type java.lang.String
     */
    public String getDesc() {
        return desc;
    }

    /**
     * desc setter
     *
     * @param desc : desc with type java.lang.String
     * @return : TransferResponse
     */
    public TransferResponse setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    /**
     * transferId getter
     *
     * @return ：return transferId with type java.lang.String
     */
    public String getTransferId() {
        return transferId;
    }

    /**
     * transferId setter
     *
     * @param transferId : transferId with type java.lang.String
     * @return : TransferResponse
     */
    public TransferResponse setTransferId(String transferId) {
        this.transferId = transferId;
        return this;
    }

    /**
     * data getter
     *
     * @return ：return data with type java.util.Map
     */
    public Map getData() {
        return data;
    }

    /**
     * data setter
     *
     * @param data : data with type java.util.Map
     * @return : TransferResponse
     */
    public TransferResponse setData(Map data) {
        this.data = data;
        return this;
    }

    /**
     * 判断 data 是否包含某个键值
     *
     * @param key ：键值
     * @return ：检测结果
     */
    public boolean dataContainsKey(String key) {
        return null != this.data && this.data.containsKey(key);
    }
}
