package com.yyh.lib.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by yuyuhang on 15/12/7.
 */
public class App extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
