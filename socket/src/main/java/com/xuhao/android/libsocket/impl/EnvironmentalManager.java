package com.xuhao.android.libsocket.impl;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xuhao.android.libsocket.impl.exceptions.PurifyException;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 节能管理器
 * Created by xuhao on 2017/5/22.
 */

public class EnvironmentalManager {
    private static class InstanceHolder {
        private static EnvironmentalManager INSTANCE = new EnvironmentalManager();
    }

    private Application mApplication;

    private ManagerHolder mHolder;

    private boolean isInit;

    private OkSocketOptions mOkOptions;

    private List<IConnectionManager> mPurifyList = new ArrayList<>();

    private boolean isPurify = false;

    private int mCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isPurify = true;
                    mPurifyList.clear();
                    List<IConnectionManager> list = mHolder.getList();
                    for (IConnectionManager manager : list) {
                        manager.disconnect(new PurifyException("environmental disconnect"));
                    }
                    mPurifyList.addAll(list);
                    break;
            }
        }
    };

    private EnvironmentalManager() {

    }

    public static EnvironmentalManager getIns() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Application application, ManagerHolder holder, OkSocketOptions options) {
        if (isInit) {
            return;
        }
        isInit = true;
        this.mApplication = application;
        this.mHolder = holder;
        this.mOkOptions = options;
        this.mApplication.registerActivityLifecycleCallbacks(new OkSocketAppLifecycleListener());
    }

    private class OkSocketAppLifecycleListener implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            mCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            mHandler.removeMessages(0);
            if (isPurify) {
                isPurify = false;
                restore();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            mHandler.removeMessages(0);
            mCount--;
            if (mCount == 0) {
                if (mOkOptions.getBackgroundLiveMinute() > 0) {
                    mHandler.sendEmptyMessageDelayed(0, mOkOptions.getBackgroundLiveMinute() * 60 * 1000);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

    private void restore() {
        for (IConnectionManager manager : mPurifyList) {
            manager.connect();
        }
    }

    public void setOkOptions(OkSocketOptions okOptions) {
        if (okOptions == null) {
            return;
        }
        mOkOptions = okOptions;
    }
}
