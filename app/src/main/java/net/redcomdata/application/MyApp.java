package net.redcomdata.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by lee on 2018/4/25 0025.
 */

public class MyApp extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }
}
