package com.glan.crash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * @title: 处理为捕获异常
 * @description:
 * @company: Netease
 * @author: GlanWang
 * @version: Created on 17/12/26.
 */

public class GLExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "ExceptionHandler";
    private static volatile GLExceptionHandler mInstance;

    private Thread.UncaughtExceptionHandler mSystemDefaultHandler;
    private WeakReference<Activity> mLastActivity;

    private GLExceptionHandler() {}

    public static GLExceptionHandler getInstance() {
        if (mInstance == null) {
            synchronized (GLExceptionHandler.class) {
                if (mInstance == null) {
                    mInstance = new GLExceptionHandler();
                }
            }
        }
        return mInstance;
    }


    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(new DefaultActivityLifecycleCallback());

        // 暂存系统指定的 UncaughtException 处理器
        mSystemDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置当前的类为程序默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

    }


    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        //处理为捕获的异常信息

        Log.e(TAG, e.toString(), e);

        handleCrashUI(t, e);
    }

    /**
     * 自定义处理崩溃 UI
     * @param t
     * @param e
     */
    private void handleCrashUI(final Thread t, final Throwable e) {
        //当前线程已经产生了异常，所以我们新起一个线程进行处理 UI
        new Thread() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    if(mLastActivity != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mLastActivity.get());
                        builder.setTitle("不好意思崩溃了")
                                .setMessage("快进行反馈告诉程序员GG吧")
                                .setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        dealExceptionBySysHandler(t, e);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dealExceptionBySysHandler(t, e);
                            }
                        });
                        builder.create().show();
                    }
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void dealExceptionBySysHandler(Thread t, Throwable e) {
        if(mSystemDefaultHandler != null) {
            //给系统处理
            mSystemDefaultHandler.uncaughtException(t, e);
        } else {
            //强制干掉进程
            android.os.Process.killProcess(android.os.Process.myUid());
        }


    }


    class DefaultActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mLastActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mLastActivity = null;
        }
    }

}
