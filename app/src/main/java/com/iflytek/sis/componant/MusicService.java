package com.iflytek.sis.componant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.iflytek.sis.constant.Constants;

import java.io.IOException;
import java.util.Random;

public class MusicService extends Service {

    private MediaPlayer player;
    private AssetManager assetManager;
    private Random random;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        assetManager = getAssets();
        random = new Random();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String tag = intent.getStringExtra("tag");
        if (tag.equals(Constants.MUSIC_START)) {
            try {
                AssetFileDescriptor fileDescriptor = assetManager.openFd(random.nextInt(2)+".mp3");
                player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                player.prepare();
                player.setLooping(true);
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (tag.equals(Constants.MUSIC_PAUSE_GOON)){
            if (player.isPlaying()){
                player.pause();
            } else {
                player.start();
            }
        } else if (tag.equals(Constants.MUSIC_STOP)){
            player.stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static void actionStart(Context context,String tag){
        Intent intent = new Intent(context,MusicService.class);
        intent.putExtra("tag",tag);
        context.startService(intent);
    }

    public static void actionStop(Context context){
        Intent intent = new Intent(context,MusicService.class);
        context.stopService(intent);
    }

    @Override
    public void onDestroy() {
        player.release();
        super.onDestroy();
    }
}
