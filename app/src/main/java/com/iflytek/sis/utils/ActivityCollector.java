package com.iflytek.sis.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL-5490 on 2018/4/11.
 */

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivty(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
