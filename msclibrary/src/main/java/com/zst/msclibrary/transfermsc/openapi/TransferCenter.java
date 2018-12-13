package com.zst.msclibrary.transfermsc.openapi;

import com.zst.msclibrary.transfermsc.openapi.consts.RetCode;
import com.zst.msclibrary.transfermsc.openapi.consts.TransferResultType;
import com.zst.msclibrary.transfermsc.openapi.entity.TransferResponse;
import com.zst.msclibrary.transfermsc.openapi.exception.TransferException;
import com.zst.msclibrary.transfermsc.openapi.handler.ITransferHandler;
import com.zst.msclibrary.transfermsc.openapi.utils.Md5Utils;
import com.zst.msclibrary.transfermsc.openapi.utils.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TransferCenter {
    /**
     * ws url 格式
     */
    private static final String WS_URL_FORMAT = "ws://%s/transrt?appid=%s&sign=%s&time=%d";

    private static String mAppId;
    private static String mAppSecret;
    private static String mTransferHost;
    private static long mTimeout = 3000L;
    /**
     * 透传给引擎的参数，初始化的时候，可以不用？？开始的时候必须传递
     */
    private static Map<String, String> mEtraParams;
    private static ConcurrentMap<String, TransferClient> transferClientPool;

    /**
     * 转写中心初始化方法
     *
     * @param transferHost ：转写服务器主机地址，形如：127.0.0.1:1003 或域名 test.iflytek.com
     * @param appId        ：转写服务器的appId ，用于签名验证
     * @param appSecret    ：转写服务器的密钥，用于签名验证
     */
    public static void init(String transferHost, String appId, String appSecret)
            throws TransferException
    {
        init(transferHost, appId, appSecret, mTimeout, null);
    }

    /**
     * 转写中心初始化方法
     *
     * @param transferHost ：转写服务器主机地址，形如：127.0.0.1:1003 或域名 test.iflytek.com
     * @param appId        ：转写服务器的appId ，用于签名验证
     * @param appSecret    ：转写服务器的密钥，用于签名验证
     * @param timeout      ：转写请求的超时时间
     */
    public static void init(String transferHost, String appId, String appSecret, long timeout, Map<String, String> extraParams)
            throws TransferException
    {
        TransferCenter.mTransferHost = transferHost;
        TransferCenter.mAppId = appId;
        TransferCenter.mAppSecret = appSecret;
        if (StringUtils.isBlank(transferHost)) {
            throw new TransferException("transfer host empty exception.");
        }
        TransferCenter.mTimeout = timeout;
        TransferCenter.mEtraParams = extraParams;
        if (null != transferClientPool)
        {
            for (Map.Entry<String, TransferClient> entry : transferClientPool.entrySet()) {
                ((TransferClient)entry.getValue()).closeWsClient();
            }
            TransferCenter.transferClientPool.clear();
        }
        TransferCenter.transferClientPool = new ConcurrentHashMap();
    }

    public static TransferResponse start(TransferResultType resultType, ITransferHandler handler)
    {
        return start(resultType, mTimeout, null, handler);
    }

    /**
     * 开始转写，允许自定义转写地址
     *
     * @param resultType  ：转写结果类型
     * @param timeout     ：转写每项操作的超时时间
     * @param extraParams ：转写参数
     * @param handler     ：转写处理类
     * @return 转写响应结果，如果 code 不是 0000，则会话开始失败
     */
    public static TransferResponse start(TransferResultType resultType, long timeout, Map<String, String> extraParams, ITransferHandler handler)
    {
        String transferUrl = generateOpenApiUrl(mTransferHost, WS_URL_FORMAT);
        if (StringUtils.isEmpty(transferUrl)) {
            return new TransferResponse(RetCode.UNINIT_ERROR);
        }
        try
        {
            new URI(transferUrl);
        }
        catch (URISyntaxException e)
        {
            return new TransferResponse(RetCode.URL_INVALID);
        }
        TransferClient client = new TransferClient(transferUrl, timeout, resultType, handler);
        TransferResponse response = client.start(unionMap(extraParams, extraParams));
        if ((null != response) && (response.isSuccess())) {
            transferClientPool.put(response.getTransferId(), client);
        }
        return response;
    }

    private static Map<String, String> unionMap(Map<String, String> map1, Map<String, String> map2)
    {
        Map<String, String> unionMap = new HashMap();
        if ((null != map1) && (!map1.isEmpty())) {
            unionMap.putAll(map1);
        }
        if ((null != map2) && (!map2.isEmpty())) {
            unionMap.putAll(map2);
        }
        return unionMap;
    }

    /**
     * 发送音频字节，要求字节长度为 1280 。
     *
     * @param transferId ：转写 id
     * @param bytes      ：音频字节数组，要求长度为 1280
     */
    public static boolean sendBytes(String transferId, byte[] bytes)
    {
        if ((StringUtils.isBlank(transferId)) || (!transferClientPool.containsKey(transferId))) {
            return false;
        }
        if ((null == bytes) || (1280 != bytes.length)) {
            return false;
        }
        return transferClientPool.get(transferId).send(bytes);
    }

    /**
     * 结束转写会话，转写结果默认不丢弃
     *
     * @param transferId ：转写id
     * @return ：
     */
    public static TransferResponse end(String transferId)
    {
        return end(transferId, false);
    }

    /**
     * 结束转写会话
     *
     * @param transferId ：转写id
     * @param abort      ：是否丢弃未完成的转写结果
     * @return
     */
    public static TransferResponse end(String transferId, boolean abort)
    {
        if ((StringUtils.isBlank(transferId)) || (!transferClientPool.containsKey(transferId))) {
            return new TransferResponse(RetCode.TRANSFER_ID_INVALID, transferId);
        }

        TransferResponse response =  ((TransferClient)transferClientPool.get(transferId)).end(abort);
        transferClientPool.remove(transferId);
        return response;
    }

    /**
     * 构建 OpenAPI 相关的 url
     *
     * @param host      ：OpenAPI 主机地址
     * @param urlFormat ：OpenAPI url 的格式
     * @return ：url
     */
    private static String generateOpenApiUrl(String host, String urlFormat)
    {
        if ((StringUtils.isBlank(host)) || (StringUtils.isBlank(mAppId)) || (StringUtils.isBlank(mAppSecret))) {
            return "";
        }
        long time = System.currentTimeMillis();
        String sign = Md5Utils.md5Hex(mAppId + mAppSecret + time);
        return String.format(urlFormat, new Object[] { host, mAppId, sign, Long.valueOf(time) });
    }

    public static void destroy(){
        if (null != transferClientPool)
        {
            for (Map.Entry<String, TransferClient> entry : transferClientPool.entrySet()) {
                ((TransferClient)entry.getValue()).closeWsClient();
            }
            transferClientPool.clear();
        }
    }

}
