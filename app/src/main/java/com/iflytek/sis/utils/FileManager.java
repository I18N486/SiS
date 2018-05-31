package com.iflytek.sis.utils;

import com.iflytek.sis.SisApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by DELL-5490 on 2018/5/30.
 */

public class FileManager {

    /**
     * copy assets中的文件到sdcard中
     *
     * @param from assets中的文件名
     * @param to   sdcard存储全路径
     */
    public static void copyFile(final String from, final String to) {
        InputStream inputStream;
        try {
            inputStream = SisApplication.getContext().getResources().getAssets().open(from);// assets文件夹下的文件
            File file = new File(to);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(to + "/hbeats.mp3");// 保存到本地的文件夹下的文件
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
