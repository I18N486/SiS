package com.zst.msclibrary.transfermsc.openapi.handler;

import com.zst.msclibrary.transfermsc.openapi.entity.LatticeData;
import com.zst.msclibrary.transfermsc.openapi.exception.TransferException;

/**
 * Created by qbsong@iflytek.com on 2018/9/13 23:38.
 * 转写处理类的骨架类，方便用户不期望实现
 */
public class AbstractTransferHandler implements ITransferHandler {
    @Override
    public void handleTransferResult(String transferId, LatticeData latticeData) {

    }

    @Override
    public void handleTransferEnd(String transferId) {

    }

    @Override
    public void handleTransferError(String transferId, TransferException exception) {

    }

}
