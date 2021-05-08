package com.danikoza.crazylogin;

import android.app.Application;

public class MyApp  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PhoneData.initHelper(getApplicationContext());
    }
}
