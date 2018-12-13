package com.zst.msclibrary.transfermsc.speech.msg;

/*
 * Auth: DELL-5490
 * Date: 2018/11/19
 */
public class UploadResultMsg {
    String speakText;
    String transText;
    String fileName;

    public String getSpeakText() {
        return speakText;
    }

    public void setSpeakText(String speakText) {
        this.speakText = speakText;
    }

    public String getTransText() {
        return transText;
    }

    public void setTransText(String transText) {
        this.transText = transText;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
