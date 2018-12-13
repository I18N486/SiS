package com.zst.msclibrary.transfermsc.transfer;

/*
 * Auth: DELL-5490
 * Date: 2018/11/19
 */
public class TransferData {

    /**
     * code : 1000
     * message : 翻译成功!
     * data : {"engineCode":"0","enginMessage":null,"targetTxt":"Hello, hello, now test the location of the translation office","sid":"its7731515b@ch47740ebb76d6477400"}
     */

    private String code;
    private String message;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * engineCode : 0
         * enginMessage : null
         * targetTxt : Hello, hello, now test the location of the translation office
         * sid : its7731515b@ch47740ebb76d6477400
         */

        private String engineCode;
        private Object enginMessage;
        private String targetTxt;
        private String sid;

        public String getEngineCode() {
            return engineCode;
        }

        public void setEngineCode(String engineCode) {
            this.engineCode = engineCode;
        }

        public Object getEnginMessage() {
            return enginMessage;
        }

        public void setEnginMessage(Object enginMessage) {
            this.enginMessage = enginMessage;
        }

        public String getTargetTxt() {
            return targetTxt;
        }

        public void setTargetTxt(String targetTxt) {
            this.targetTxt = targetTxt;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }
    }
}
