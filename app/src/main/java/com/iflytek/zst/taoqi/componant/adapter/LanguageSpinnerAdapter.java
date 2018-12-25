package com.iflytek.zst.taoqi.componant.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

/**
 * Created by DELL-5490 on 2018/12/25.
 */

public class LanguageSpinnerAdapter extends ArrayAdapter {

    public LanguageSpinnerAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.add("英");
        this.add("日");
        this.add("韩");
        this.add("法");
        this.add("西");
        this.add("俄");
    }


}
