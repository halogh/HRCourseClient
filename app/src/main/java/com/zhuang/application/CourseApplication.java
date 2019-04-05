package com.zhuang.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhuang on 2019/3/29.
 */

public class CourseApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
