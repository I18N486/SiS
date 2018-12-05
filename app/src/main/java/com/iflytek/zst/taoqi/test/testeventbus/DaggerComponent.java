package com.iflytek.zst.taoqi.test.testeventbus;

import dagger.Component;

/**
 * Created by DELL-5490 on 2018/11/26.
 */

@Component
public interface DaggerComponent {
    void inject(TestEventbusActivity eventbusActivity);
}
