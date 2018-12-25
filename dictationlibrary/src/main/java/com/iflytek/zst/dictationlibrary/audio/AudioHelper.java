package com.iflytek.zst.dictationlibrary.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.iflytek.zst.dictationlibrary.constants.DictationConstants;
import com.iflytek.zst.dictationlibrary.impl.AudioListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL-5490 on 2018/12/21.
 */

public class AudioHelper {
    //设置音频源（使用mic）
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    //设置采样率(16K)
    private static final int SAMPLERATE_INHZ = 16*1000;
    //设置采样精度(8 bit)
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    //设置捕获音频声道数(单声道)
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    //指定缓冲区大小
    private int bufferSizeInBytes;
    //是否正在录音
    private boolean isRecording = false;

    //使用线程池执行录音
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    AudioRecord audioRecord;
    public AudioHelper(Context context){
        initData();
    }

    private void initData(){
        bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLERATE_INHZ,CHANNEL_CONFIG,AUDIO_FORMAT);
        audioRecord = new AudioRecord(AUDIO_SOURCE,SAMPLERATE_INHZ,CHANNEL_CONFIG,AUDIO_FORMAT,bufferSizeInBytes);
    }

    public void startRecord(String str, AudioListener audioListener){
        if (executorService != null){
            isRecording = true;
            executorService.execute(new RecordTask(audioListener));
        } else {
            audioListener.onError(DictationConstants.ERROR_THREAD_NULL);
        }
    }

    public void stopRecord(){
        isRecording = false;
        if (audioRecord != null){
            if (audioRecord.getRecordingState() == AudioRecord.STATE_INITIALIZED){//初始化成功
                audioRecord.stop();
            }
            if (audioRecord != null){
                audioRecord.release();
            }
        }
//        PcmUtils pcmUtils = new PcmUtils();
//        pcmUtils.pcmToWav("/sdcard/taoqi/test.pcm","/sdcard/taoqi/test.wav",0,null);
    }


    public boolean isRecording(){
        return isRecording;
    }

    private class RecordTask implements Runnable{

        private AudioListener audioListener;

        public RecordTask(AudioListener audioListener){
            this.audioListener = audioListener;
        }

        @Override
        public void run() {
            if (audioRecord != null){
                if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED ) {
                    //进行初始化
                    initData();
                }
                byte[] byteBuffer = new byte[bufferSizeInBytes];
                audioRecord.startRecording();
                while (isRecording && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                    if (audioListener != null){
                        int end = audioRecord.read(byteBuffer,0,byteBuffer.length);
                        byte[] byteData = new byte[end];
                        for (int i= 0;i<end;i++){
                            byteData[i] = byteBuffer[i];
                        }
//                        PcmUtils.createPcm("/sdcard/taoqi/test.pcm",byteData);
                        audioListener.onRecordBuffer(byteData,0);
                    }
                }
            } else {
                if (audioListener != null){
                    audioListener.onError(DictationConstants.ERROR_RECORD_NULL);
                }
            }
        }
    }
}
