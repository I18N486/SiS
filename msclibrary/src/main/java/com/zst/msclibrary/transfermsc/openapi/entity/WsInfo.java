package com.zst.msclibrary.transfermsc.openapi.entity;

/*
 * Auth: DELL-5490
 * Date: 2018/11/2
 */
public class WsInfo {
    private String w;//词
    private String wp;//词标识   n：普通词；s：顺滑词；p：标点；g：分段标识
    private long wb;//词的开始时间
    private long we;//词的结束时间

    public WsInfo() {
    }

    /**
     * w getter
     *
     * @return ：return w with type java.lang.String
     */
    public String getW() {
        return w;
    }

    /**
     * w setter
     *
     * @param w : w with type java.lang.String
     * @return : WsInfo
     */
    public WsInfo setW(String w) {
        this.w = w;
        return this;
    }

    /**
     * wp getter
     *
     * @return ：return wp with type java.lang.String
     */
    public String getWp() {
        return wp;
    }

    /**
     * wp setter
     *
     * @param wp : wp with type java.lang.String
     * @return : WsInfo
     */
    public WsInfo setWp(String wp) {
        this.wp = wp;
        return this;
    }

    /**
     * wb getter
     *
     * @return ：return wb with type long
     */
    public long getWb() {
        return wb;
    }

    /**
     * wb setter
     *
     * @param wb : wb with type long
     * @return : WsInfo
     */
    public WsInfo setWb(long wb) {
        this.wb = wb;
        return this;
    }

    /**
     * we getter
     *
     * @return ：return we with type long
     */
    public long getWe() {
        return we;
    }

    /**
     * we setter
     *
     * @param we : we with type long
     * @return : WsInfo
     */
    public WsInfo setWe(long we) {
        this.we = we;
        return this;
    }
}
