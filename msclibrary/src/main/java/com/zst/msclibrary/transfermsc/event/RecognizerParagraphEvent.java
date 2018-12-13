package com.zst.msclibrary.transfermsc.event;

/**
 * @author changwu on 2018/11/29 tel|wechat 18656086531
 * @des
 */
public class RecognizerParagraphEvent {
    private String recognizerResult;
    private String translateResult;
    private String fileName;

    public RecognizerParagraphEvent(String recognizerResult, String translateResult, String fileName) {
        this.recognizerResult = recognizerResult;
        this.translateResult = translateResult;
        this.fileName = fileName;
    }

    public String getRecognizerResult() {
        return recognizerResult;
    }

    public void setRecognizerResult(String recognizerResult) {
        this.recognizerResult = recognizerResult;
    }

    public String getTranslateResult() {
        return translateResult;
    }

    public void setTranslateResult(String translateResult) {
        this.translateResult = translateResult;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
