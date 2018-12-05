package com.iflytek.zst.taoqi.test.testeventbus;

import android.app.IntentService;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action){
                case TestEventbusActivity.INTENT_SERVICE_ACTION:
                    for (int i=0;i<10;i++){
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MyEvent myEvent = new MyEvent("Myevent "+i);
                        EventBus.getDefault().post(myEvent);
                    }
                    break;
            }
        }
    }
}
