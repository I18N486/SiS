package com.zst.msclibrary.transfermsc.openapi.exception;

/**
 * Created by qbsong@iflytek.com on 2018/9/14 10:44.
 * 转写异常
 */
public class TransferException extends RuntimeException {

    public TransferException(String errorMsg) {
        super(errorMsg);
    }

    public TransferException(Throwable cause) {
        super(cause);
    }

    public TransferException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }

}
