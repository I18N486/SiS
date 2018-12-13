package com.zst.msclibrary.transfermsc.openapi.consts;

/**
 * 返回码定义
 */
public class RetCode {

    /**
     * 接口成功返回码
     */
    public static final RetCode SUCCESS = new RetCode("0000", "成功");

    public static final RetCode UNINIT_ERROR = new RetCode("SE1001", "客户端未初始化");
    public static final RetCode INIT_ERROR = new RetCode("SE1002", "转写主机连接超时失败");
    public static final RetCode BEGIN_TIMEOUT = new RetCode("SE1003", "开始请求超时错误");
    public static final RetCode END_TIMEOUT = new RetCode("SE1004", "结束请求超时错误");
    public static final RetCode CLIENT_ERROR = new RetCode("SE1005", "转写对象过期（可能由多次init引起）");
    public static final RetCode START_ERROR = new RetCode("SE1006", "转写开始异常");

    public static final RetCode PCM_INVALID_ERROR = new RetCode("SE2001", "pcm 文件路径非法");
    public static final RetCode PCM_NOT_EXISTS = new RetCode("SE2002", "pcm 文件不存在");
    public static final RetCode PCM_TRANSFER_ERROR = new RetCode("SE2003", "pcm 文件读取异常");
    public static final RetCode TRANSFER_ID_INVALID = new RetCode("SE2004", "转写 ID 非法，ID 被释放或转写中心重复初始化");
    public static final RetCode TRANSFER_HOST_INVALID = new RetCode("SE2005", "转写主机地址非法");
    public static final RetCode URL_INVALID = new RetCode("SE2006", "转写服务地址非法");


    public static final RetCode GET_HOTWORDS_ERROR = new RetCode("SE3001", "请求获取热词失败");
    public static final RetCode SAVE_HOTWORDS_ERROR = new RetCode("SE3003", "请求保存热词失败");


    /**
     * 错误码定义
     */
    private String retCode;

    /**
     * 错误描述
     */
    private String desc;

    public RetCode(String retCode, String desc) {
        this.retCode = retCode;
        this.desc = desc;
    }

    /**
     * retCode getter
     *
     * @return ：return retCode with type java.lang.String
     */
    public String getRetCode() {
        return retCode;
    }

    /**
     * retCode setter
     *
     * @param retCode : retCode with type java.lang.String
     * @return : RetCode
     */
    public RetCode setRetCode(String retCode) {
        this.retCode = retCode;
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
     * @return : RetCode
     */
    public RetCode setDesc(String desc) {
        this.desc = desc;
        return this;
    }
}
