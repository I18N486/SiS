package com.zst.msclibrary.transfermsc.openapi.entity;

import java.util.List;

/*
 * Auth: DELL-5490
 * Date: 2018/11/2
 */
public class LatticeData {
    private String type;
    private String text;
    private int sn;
    private long bg;
    private long ed;
    private boolean ls;
    private List<WsInfo> ws;

    public LatticeData() {
    }

    /**
     * type getter
     *
     * @return ：return type with type java.lang.String
     */
    public String getType() {
        return type;
    }

    /**
     * type setter
     *
     * @param type : type with type java.lang.String
     * @return : LatticeData
     */
    public LatticeData setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * text getter
     *
     * @return ：return text with type java.lang.String
     */
    public String getText() {
        return text;
    }

    /**
     * text setter
     *
     * @param text : text with type java.lang.String
     * @return : LatticeData
     */
    public LatticeData setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * sn getter
     *
     * @return ：return sn with type int
     */
    public int getSn() {
        return sn;
    }

    /**
     * sn setter
     *
     * @param sn : sn with type int
     * @return : LatticeData
     */
    public LatticeData setSn(int sn) {
        this.sn = sn;
        return this;
    }

    /**
     * bg getter
     *
     * @return ：return bg with type long
     */
    public long getBg() {
        return bg;
    }

    /**
     * bg setter
     *
     * @param bg : bg with type long
     * @return : LatticeData
     */
    public LatticeData setBg(long bg) {
        this.bg = bg;
        return this;
    }

    /**
     * ed getter
     *
     * @return ：return ed with type long
     */
    public long getEd() {
        return ed;
    }

    /**
     * ed setter
     *
     * @param ed : ed with type long
     * @return : LatticeData
     */
    public LatticeData setEd(long ed) {
        this.ed = ed;
        return this;
    }

    /**
     * ls getter
     *
     * @return ：return ls with type boolean
     */
    public boolean isLs() {
        return ls;
    }

    /**
     * ls setter
     *
     * @param ls : ls with type boolean
     * @return : LatticeData
     */
    public LatticeData setLs(boolean ls) {
        this.ls = ls;
        return this;
    }

    /**
     * ws getter
     *
     * @return ：return ws with type java.util.List<com.iflytem.audiotm.sdk.entity.WsInfo>
     */
    public List<WsInfo> getWs() {
        return ws;
    }

    /**
     * ws setter
     *
     * @param ws : ws with type java.util.List<com.iflytem.audiotm.sdk.entity.WsInfo>
     * @return : LatticeData
     */
    public LatticeData setWs(List<WsInfo> ws) {
        this.ws = ws;
        return this;
    }
}
