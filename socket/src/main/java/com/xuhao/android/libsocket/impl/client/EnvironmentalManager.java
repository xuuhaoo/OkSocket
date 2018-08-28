package com.xuhao.android.libsocket.impl.client;

import android.os.Handler;
import android.os.Message;

import com.xuhao.android.common.utils.ActivityStack;
import com.xuhao.android.libsocket.impl.exceptions.PurifyException;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 节能管理器
 * Created by xuhao on 2017/5/22.
 */

public class EnvironmentalManager {
    public static final long DELAY_CONNECT_MILLS = 1000;

    private static class InstanceHolder {
        private static EnvironmentalManager INSTANCE = new EnvironmentalManager();
    }

    /**
     * 后台存活时间(毫秒)
     * -1为永久存活,取值范围[1000,Long.MAX]
     */
    private long mBackgroundLiveMills = -1;

    private ManagerHolder mHolder;

    private boolean isInit;

    private List<IConnectionManager> mPurifyList = new ArrayList<>();

    private boolean isPurify = false;

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

    public void init(ManagerHolder holder) {
        if (isInit) {
            return;
        }
        isInit = true;
        this.mHolder = holder;
        ActivityStack.addStackChangedListener(mChangedAdapter);
        mBackgroundLiveMills = -1;
    }

    private ActivityStack.OnStackChangedAdapter mChangedAdapter = new ActivityStack.OnStackChangedAdapter() {
        @Override
        public void onAppPause() {
            mHandler.removeCallbacksAndMessages(null);
            if (mBackgroundLiveMills > 0) {
                long backLiveMills = mBackgroundLiveMills;
                backLiveMills = backLiveMills < DELAY_CONNECT_MILLS ? DELAY_CONNECT_MILLS : backLiveMills;
                mHandler.sendEmptyMessageDelayed(0, backLiveMills);
            }
        }

        @Override
        public void onAppResume() {
            mHandler.removeCallbacksAndMessages(null);
            if (isPurify) {
                isPurify = false;
                restore();
            }
        }
    };

    private void restore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (IConnectionManager manager : mPurifyList) {
                    manager.connect();
                }
            }
        }, DELAY_CONNECT_MILLS);

    }

    public void setBackgroundLiveMills(long backgroundLiveMills) {
        mBackgroundLiveMills = backgroundLiveMills;
    }

    public long getBackgroundLiveMills() {
        return mBackgroundLiveMills;
    }
}
