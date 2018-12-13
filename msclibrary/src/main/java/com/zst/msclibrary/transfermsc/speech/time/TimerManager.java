package com.zst.msclibrary.transfermsc.speech.time;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Auth: DELL-5490
 * Date: 2018/11/9
 */
public class TimerManager {

    private Callback mCallback;
    private Timer mTime;
    private Map<Integer, TimerTask> map = new HashMap<>();

    public TimerManager(Callback callback){
        this.mCallback = callback;
        this.map.clear();
        this.mTime = new Timer();
    }

    public void startTime(final int id, long delay){
        cancelTimer(id);
        TimerTask task = new TimerTask(){
            @Override
            public void run() {
                mCallback.onTimeout(id);
                map.remove(id);
            }
        };
        mTime.schedule(task, delay);
        map.put(id, task);
    }

    public void cancelTimer(int id){
        TimerTask task = map.get(id);
        if(task != null){
            task.cancel();
            task = null;
            map.remove(id);
        }
    }

    public interface Callback{
        void onTimeout(int id);
    }
}
