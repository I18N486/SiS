package com.zst.msclibrary.transfermsc.openapi.handler;


import com.zst.msclibrary.transfermsc.openapi.entity.LatticeData;
import com.zst.msclibrary.transfermsc.openapi.exception.TransferException;

/**
 * Created by qbsong@iflytek.com on 2018/9/7 10:28.
 * 转写处理接口，即回调函数，每个实例持有一个进行转写过程的专项处理，同时也是为了多线程分离
 */
public interface ITransferHandler {
    /**
     * 处理转写结果
     *
     * @param transferId  ：转写id
     * @param latticeData ：转写结果
     */
    public void handleTransferResult(String transferId, LatticeData latticeData);

    /**
     * 处理转写结束，该结束是指引擎吐出结束标记位
     *
     * @param transferId ：转写id
     */
    public void handleTransferEnd(String transferId);

    /**
     * 处理转写过程中的各种异常
     *
     * @param transferId ：转写id
     * @param exception  ：转写异常
     */
    public void handleTransferError(String transferId, TransferException exception);

}
