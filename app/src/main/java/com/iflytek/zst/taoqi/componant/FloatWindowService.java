package com.iflytek.zst.taoqi.componant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.iflytek.zst.taoqi.R;

public class FloatWindowService extends Service {
    WindowManager.LayoutParams layoutParams;
    WindowManager windowManager;
    RelativeLayout relativeLayout;
    public FloatWindowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // 新建悬浮窗控件
            RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_floatwindow,null);
            relativeLayout.setOnTouchListener(new FloatingOnTouchListener());

            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.width = 200;
            layoutParams.height = 200;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.x = 300;
            layoutParams.y = 300;

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(relativeLayout, layoutParams);
        }
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(relativeLayout);
        super.onDestroy();
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,FloatWindowService.class);
        context.startService(intent);
    }

    public static void actionStop(Context context){
        Intent intent = new Intent(context,FloatWindowService.class);
        context.stopService(intent);
    }
}
