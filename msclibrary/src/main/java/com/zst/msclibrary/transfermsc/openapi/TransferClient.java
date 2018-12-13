package com.zst.msclibrary.transfermsc.openapi;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.zst.msclibrary.transfermsc.openapi.consts.RetCode;
import com.zst.msclibrary.transfermsc.openapi.consts.TransferCommand;
import com.zst.msclibrary.transfermsc.openapi.consts.TransferResultType;
import com.zst.msclibrary.transfermsc.openapi.entity.LatticeData;
import com.zst.msclibrary.transfermsc.openapi.entity.TransferBeginReqMessage;
import com.zst.msclibrary.transfermsc.openapi.entity.TransferEndReqMessage;
import com.zst.msclibrary.transfermsc.openapi.entity.TransferResponse;
import com.zst.msclibrary.transfermsc.openapi.entity.WebSocketResponse;
import com.zst.msclibrary.transfermsc.openapi.exception.TransferException;
import com.zst.msclibrary.transfermsc.openapi.handler.ITransferHandler;
import com.zst.msclibrary.transfermsc.openapi.utils.NumberUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class TransferClient {

    /**
     * 沉睡间隔时长 3 ms
     */
    private static final int sleepInterval = 3;
    /**
     * 转写id
     */
    private String transferId;
    /**
     * ws 链接
     */
    private String transferUrl;
    /**
     * 转写结果
     */
    private TransferResultType resultType;
    /**
     * 连接、等待的超时时间
     */
    private long timeout;
    /**
     * 发送序号
     */
    private int sendSequence = 0;
    /**
     * 转写过程中的回调函数
     */
    private ITransferHandler mTransferHandler;
    /**
     * 与 OpenAPI 的 websocket 通信对象，不对外暴露
     */
    private WebSocketClient webSocketClient;
    /**
     * 转写操作返回结果
     */
    private TransferResponse transferResponse;
    /**
     * 转写客户端操作的状态结果
     */
    private ConcurrentMap<String, Boolean> actionStatusMap = new ConcurrentHashMap();

    private TransferClient(){

    }

    /**
     * @param transferUrl     ：websocket链接
     * @param timeout         ：操作等待超时时间，如果该参数值小于 connectTimeout 的值，则取较大值
     * @param resultType      ：转写类型，
     * @param transferHandler ：转写过程个性化处理类
     */
    protected TransferClient(String transferUrl, long timeout, TransferResultType resultType, final ITransferHandler transferHandler){
        this.transferUrl = transferUrl;
        this.resultType = resultType;
        this.mTransferHandler = transferHandler;
        this.timeout = timeout;

        Map<String, String> headers = new HashMap();
        headers.put("X-Ca-Appid", "");
        headers.put("X-Ca-Version", "1.0");
        headers.put("X-Ca-Time", String.valueOf(System.currentTimeMillis()));
        try {
            this.webSocketClient = new WebSocketClient(new URI(transferUrl), new Draft_6455(), headers, Long.valueOf(timeout).intValue()) {

                /**
                 * websocket 连接建立成功后的响应时事件
                 * @param serverHandshake ：握手
                 */
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    actionStatusMap.put(TransferCommand.OPEN.getCommand(), Boolean.valueOf(true));
                }

                /**
                 * websocket 接收到的服务器消息
                 * @param responseString ：消息字符串
                 */
                @Override
                public void onMessage(String responseString) {
                    WebSocketResponse response = JSON.parseObject(responseString, WebSocketResponse.class);
                    String command = response.getAction();
                    transferResponse = new TransferResponse(response);
                    if (!RetCode.SUCCESS.getRetCode().equals(response.getCode()))//如果返回不成功，标记命令失败
                    {
                        actionStatusMap.put(command, Boolean.valueOf(false));
                        return;
                    }
                    if (command.equals(TransferCommand.BEGIN.getCommand()))
                    {
                        Object sid = response.getData().get("sid");
                        boolean beginFlag = (null != sid);
                        transferId = beginFlag ? sid.toString() : null;
                        actionStatusMap.put(command, beginFlag);
                        transferResponse = new TransferResponse(response, transferId);
                        return;
                    }
                    if (command.equals(TransferCommand.END.getCommand()))
                    {
                        LatticeData latticeData = JSON.parseObject(JSON.toJSONString(response.getData()), LatticeData.class);
                        mTransferHandler.handleTransferResult(transferId, latticeData);
                        return;
                    }
                    if (command.equals(TransferCommand.RESULT.getCommand()))
                    {
                        LatticeData latticeData = (LatticeData)JSON.parseObject(JSON.toJSONString(response.getData()), LatticeData.class);
                        mTransferHandler.handleTransferResult(transferId, latticeData);
                        if ((null != latticeData) && (latticeData.isLs())) {
                            mTransferHandler.handleTransferEnd(transferId);
                        }
                        return;
                    }
                    mTransferHandler.handleTransferError(transferId, new TransferException(String.format("unknown request command [%s] exception .", new Object[] { command })));
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.v("TransferClient", "onClose s " + s);
                    for (TransferCommand command : TransferCommand.values()) {
                        actionStatusMap.put(command.getCommand(), Boolean.valueOf(false));
                    }
                    mTransferHandler.handleTransferEnd(transferId);
                }

                @Override
                public void onError(Exception e) {
                    Log.v("TransferClient", "onError");
                    for (TransferCommand command : TransferCommand.values()) {
                        actionStatusMap.put(command.getCommand(), Boolean.valueOf(false));
                    }
                    mTransferHandler.handleTransferError(transferId, new TransferException(e));
                }
            };
        }catch (Exception e){
            e.printStackTrace();
            this.webSocketClient = null;
        }
    }

    /**
     * 开始转写，把 connect 写到了开始中，这样就可以重复开始了。
     *
     * @param extraParams ：可扩展参数，具体规则由服务端设定
     * @return ：返回开始是否成功
     */
    protected TransferResponse start(Map<String, String> extraParams){
        if (this.webSocketClient == null) {
            return new TransferResponse(RetCode.INIT_ERROR);
        }
        this.webSocketClient.connect();
        if ((!checkAndWait(TransferCommand.OPEN)) || (!this.webSocketClient.isOpen())) {
            return new TransferResponse(RetCode.INIT_ERROR);
        }
        TransferBeginReqMessage beginReqMessage = new TransferBeginReqMessage(this.resultType.getType());

        this.webSocketClient.send(JSON.toJSONString(beginReqMessage));
        if ((!checkAndWait(TransferCommand.BEGIN)) || (this.webSocketClient.isClosed()))
        {
            closeWsClient();
            if (this.actionStatusMap.containsKey(TransferCommand.BEGIN.getCommand()))
            {
                this.actionStatusMap.remove(TransferCommand.BEGIN.getCommand());
                if (null == this.transferResponse) {
                    return new TransferResponse(RetCode.START_ERROR, this.transferId);
                }
                return this.transferResponse;
            }
            return new TransferResponse(RetCode.BEGIN_TIMEOUT);
        }
        return this.transferResponse;
    }

    /**
     * 发送音频数据，返回发送是否成功。在发送方法内进行数据组装，添加发送序号
     *
     * @param bytes ：字节数组，大小为 1280
     * @return ：
     */
    protected boolean send(byte[] bytes)
    {
        if ((null == bytes) || (1280 != bytes.length) || (!isAvailable())) {
            return false;
        }
        this.sendSequence += 1;
        byte[] data = new byte[8 + bytes.length];
        System.arraycopy(NumberUtils.intToByte(this.sendSequence), 0, data, 0, 4);
        System.arraycopy(NumberUtils.intToByte(0), 0, data, 4, 4);
        System.arraycopy(bytes, 0, data, 8, bytes.length);
//        TransferCenter.sendBytes(this.transferId, data);
        this.webSocketClient.send(data);
        return true;
    }

    /**
     * 结束转写
     *
     * @param abort ：是否丢弃
     * @return ：结束转写的返回结果
     */
    protected TransferResponse end(boolean abort)
    {
        if ((null == this.webSocketClient) || (this.webSocketClient.isClosing()) || (this.webSocketClient.isClosed())) {
            return new TransferResponse(RetCode.SUCCESS, this.transferId);
        }
        TransferEndReqMessage endReqMessage = new TransferEndReqMessage(abort);
        this.webSocketClient.send(JSON.toJSONString(endReqMessage));
        for (int index = 0; index < 3; index++) {
            if (checkAndWait(TransferCommand.END)) {
                break;
            }
        }
        if (!checkAndWait(TransferCommand.END))
        {
            if (actionStatusMap.containsKey(TransferCommand.END.getCommand()))
            {
                actionStatusMap.remove(TransferCommand.END.getCommand());
                return transferResponse;
            }
            return new TransferResponse(RetCode.END_TIMEOUT);
        }
        closeWsClient();
        return this.transferResponse;
    }

    /**
     * websocket 连接不为空且出于开启状态
     *
     * @return ：
     */
    protected boolean isAvailable()
    {
        return (null != this.webSocketClient) && (this.webSocketClient.isOpen());
    }

    /**
     * 检测并等待命令，供握手、开始、结束等命令使用，检测之后，及时删除
     *
     * @param command ：转写命令
     * @return ：结果
     */
    private boolean checkAndWait(TransferCommand command)
    {
        long start = System.currentTimeMillis();
        try
        {
            while ((!this.actionStatusMap.containsKey(command.getCommand())) && (System.currentTimeMillis() - start <= this.timeout)) {
                TimeUnit.MILLISECONDS.sleep(sleepInterval);
            }
        }
        catch (InterruptedException e)
        {
            return false;
        }
        return (this.actionStatusMap.containsKey(command.getCommand())) && (((Boolean)this.actionStatusMap.get(command.getCommand())).booleanValue());
    }

    /**
     * 关闭 websocket client
     */
    protected void closeWsClient()
    {
        if ((null != this.webSocketClient) && (
                (this.webSocketClient.isOpen()) || (this.webSocketClient.isConnecting()))) {
            this.webSocketClient.close();
        }
    }

    public String getTransferId()
    {
        return this.transferId;
    }

    public String getTransferUrl()
    {
        return this.transferUrl;
    }

    public TransferResultType getResultType()
    {
        return this.resultType;
    }

    public long getTimeout()
    {
        return this.timeout;
    }

}
