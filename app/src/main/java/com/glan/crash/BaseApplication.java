package com.glan.crash;

import android.app.Application;

/**
 * @title:
 * @description:
 * @company: Netease
 * @author: GlanWang
 * @version: Created on 17/12/26.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GLExceptionHandler.getInstance().init(this);
    }
}
