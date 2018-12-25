package com.iflytek.zst.dictationlibrary.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangshitao
 * Date 2018/7/7
 * Description :pcm格式的音频转换为wav格式的工具类
 */
public class PcmUtils {
    private int mBufferSize;  //缓存的音频大小
    private int mSampleRate = 8000;// 8000|16000
    private int mChannel = AudioFormat.CHANNEL_IN_STEREO;   //立体声
    private int mEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private static ExecutorService executorService;
    private static ExecutorService mMergeFielExectorService;

    public PcmUtils() {
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }

    public static void shutDownPool() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    /**
     * @param sampleRate sample rate、采样率
     * @param channel    channel、声道
     * @param encoding   Audio data format、音频格式
     */
    public PcmUtils(int sampleRate, int channel, int encoding) {
        this.mSampleRate = sampleRate;
        this.mChannel = channel;
        this.mEncoding = encoding;
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }

    /**
     * pcm文件转wav文件
     *
     * @param inFilename  源文件路径
     * @param outFilename 目标文件路径
     */
    public void pcmToWav(final String inFilename, final String outFilename, final int position, final PcmUtilsPcmToWavCallback pcmToWavCallback) {
        if (pcmToWavCallback != null)
        pcmToWavCallback.pcmToWavStart();
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(2);
        }
        executorService.execute(new Runnable() {
            FileInputStream in;
            FileOutputStream out;
            long totalAudioLen;
            long totalDataLen;
            long longSampleRate = mSampleRate;
            int channels = 2;
            long byteRate = 16 * mSampleRate * channels / 8;
            byte[] data = new byte[mBufferSize];

            @Override
            public void run() {
                Log.d("hj", "pcmToWav run");
                try {
                    in = new FileInputStream(inFilename);
                    out = new FileOutputStream(outFilename);
                    totalAudioLen = in.getChannel().size();
                    totalDataLen = totalAudioLen + 36;

                    writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                            longSampleRate, channels, byteRate);
                    while (in.read(data) != -1) {
                        out.write(data);
                    }
                    if (pcmToWavCallback != null)
                    pcmToWavCallback.pcmToWavFinish(position);
                } catch (IOException e) {
                    if (pcmToWavCallback != null)
                    pcmToWavCallback.pcmToWavFailed();
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (pcmToWavCallback != null)
                    pcmToWavCallback.pcmToWavEnd();
                }
            }
        });
    }


    /**
     * 加入wav文件头
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';  //WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;   // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16;  // bits per sample
        header[35] = 0;
        header[36] = 'd'; //data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }


    public static String getVoiceName() {
        return null;
    }

    /**
     * 合并pcm音频文件
     */
    public static final int BUFSIZE = 1024 * 8;

    public static void mergeFiles(final String outFile, final String[] files, final PcmUtilsMergeFilesCallback pcmUtilsCallback) {
        pcmUtilsCallback.mergeFilesStart();
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(2);
        }
        executorService.execute(new Runnable() {
            FileChannel outChannel = null;

            @Override
            public void run() {
                try {
                    outChannel = new FileOutputStream(outFile).getChannel();
                    for (String f : files) {
                        if (TextUtils.isEmpty(f)) {
                            continue;
                        }
                        File file = new File(f);
                        if (file.exists()) {
                            FileChannel fc = new FileInputStream(f).getChannel();
                            ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
                            while (fc.read(bb) != -1) {
                                bb.flip();
                                outChannel.write(bb);
                                bb.clear();
                            }
                            fc.close();
                        }
                    }
                    pcmUtilsCallback.mergeFilesFinish();
                } catch (IOException ioe) {
                    pcmUtilsCallback.mergeFilesFailed();
                    ioe.printStackTrace();
                } finally {
                    try {
                        if (outChannel != null) {
                            outChannel.close();
                        }
                        pcmUtilsCallback.mergeFilesEnd();
                    } catch (IOException ignore) {
                    }
                }
            }
        });
    }

    /**
     * 方法描述：传入音频文件全路径，得到音频时长
     *
     * @param path 音频文件全路径
     * @return 音频时长
     */
    public static int getAudioDuration(String path) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int duration = player.getDuration();
        player.release();//记得释放资源
        return duration;
    }


    /**
     * 将字节写入文件，生成pcm
     *
     * @param filePath
     * @param bytes
     */
    public static void createPcm(final String filePath, final byte[] bytes) {
        if (executorService == null) {
            executorService = Executors.newScheduledThreadPool(2);
        }
        executorService.execute(new MyTask(filePath, bytes));
    }

    static class MyTask implements Runnable {
        String filePath;
        byte[] pcmData;
        FileOutputStream fileOutputStream = null;

        public MyTask(String filePath, byte[] pcmData) {
            this.filePath = filePath;
            this.pcmData = pcmData;
        }

        @Override
        public void run() {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    if (!file.getParentFile().exists()){
                        file.mkdirs();
                    }
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                if (fileOutputStream != null && pcmData != null) {
                    fileOutputStream.write(pcmData);
                    fileOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface PcmUtilsMergeFilesCallback {
        void mergeFilesStart();

        void mergeFilesFinish();

        void mergeFilesFailed();

        void mergeFilesEnd();

    }


    public interface PcmUtilsPcmToWavCallback {
        void pcmToWavStart();

        void pcmToWavFinish(int position);

        void pcmToWavFailed();

        void pcmToWavEnd();
    }
}
