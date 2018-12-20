package com.iflytek.zst.dictationlibrary.bean;

import java.util.List;

/**
 * Created by DELL-5490 on 2018/12/20.
 */

public class NormalResultBean {

    /**
     * uuid : 344c8164333c896b8d93656ff847ab3aisap
     * results : [{"oriLangCountry":"cn","translated":"Hello, hello, now the translator and recognition are back in sync.","transLangCountry":"en","original":"你好，你好，现在翻译和识别同步返回。"}]
     * pluginTime : 132
     * rc : 0
     * allTime : 132
     * sourceNames : ["translate"]
     */

    private String uuid;
    private int pluginTime;
    private int rc;
    private int allTime;
    private List<ResultsBean> results;
    private List<String> sourceNames;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getPluginTime() {
        return pluginTime;
    }

    public void setPluginTime(int pluginTime) {
        this.pluginTime = pluginTime;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public int getAllTime() {
        return allTime;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public List<String> getSourceNames() {
        return sourceNames;
    }

    public void setSourceNames(List<String> sourceNames) {
        this.sourceNames = sourceNames;
    }

    public static class ResultsBean {
        /**
         * oriLangCountry : cn
         * translated : Hello, hello, now the translator and recognition are back in sync.
         * transLangCountry : en
         * original : 你好，你好，现在翻译和识别同步返回。
         */

        private String oriLangCountry;
        private String translated;
        private String transLangCountry;
        private String original;

        public String getOriLangCountry() {
            return oriLangCountry;
        }

        public void setOriLangCountry(String oriLangCountry) {
            this.oriLangCountry = oriLangCountry;
        }

        public String getTranslated() {
            return translated;
        }

        public void setTranslated(String translated) {
            this.translated = translated;
        }

        public String getTransLangCountry() {
            return transLangCountry;
        }

        public void setTransLangCountry(String transLangCountry) {
            this.transLangCountry = transLangCountry;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }
}
