package com.iflytek.sis.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.iflytek.sis.R;

public class GdMapActivity extends AppCompatActivity {

    MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gd_map);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        AMap aMap = null;
        if (aMap == null){
            aMap = mapView.getMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    public static void actionStart(Context context){
        Intent intent = new Intent(context,GdMapActivity.class);
        context.startActivity(intent);
    }
}
