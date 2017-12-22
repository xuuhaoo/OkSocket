package com.xuhao.android.libsocket.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xuhao.android.libsocket.impl.exceptions.DogDeadException;
import com.xuhao.android.libsocket.sdk.bean.IPulse;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

/**
 * Created by xuhao on 2017/5/18.
 */

public class PulseManager implements IPulse {
    /**
     * 心跳事件
     */
    private static final int PULSE_WHAT = 0;
    /**
     * 喂养事件
     */
    private static final int FEED_WHAT = 1;
    /**
     * 数据包发送器
     */
    private IConnectionManager mManager;
    /**
     * 心跳数据包
     */
    private IPulseSendable mSendable;
    /**
     * 连接参数
     */
    private OkSocketOptions mOkOptions;
    /**
     * 当前频率
     */
    private long mCurrentFrequency;
    /**
     * 当前的线程模式
     */
    private OkSocketOptions.IOThreadMode mCurrentThreadMode;
    /**
     * 是否死掉
     */
    private boolean isDead = false;
    /**
     * 允许遗漏的次数
     */
    private int mLoseTimes = -1;
    /**
     * 脉搏计时器
     */
    private Handler mPulseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDead) {
                return;
            }
            switch (msg.what) {
                case PULSE_WHAT: {
                    if (mManager != null && mSendable != null) {
                        if (mOkOptions.getPulseFeedLoseTimes() != -1
                                && ++mLoseTimes >= mOkOptions.getPulseFeedLoseTimes()) {
                            mManager.disConnect(
                                    new DogDeadException("you need feed dog on time,otherwise he will die"));
                        } else {
                            mManager.send(mSendable);
                            pulse();
                        }
                    }
                    break;
                }
                case FEED_WHAT: {
                    mLoseTimes = -1;
                    break;
                }
            }
        }
    };

    PulseManager(IConnectionManager manager, OkSocketOptions okOptions) {
        mManager = manager;
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
    }

    public IPulse setPulseSendable(IPulseSendable sendable) {
        if (sendable != null) {
            mSendable = sendable;
        }
        return this;
    }

    public IPulseSendable getPulseSendable() {
        return mSendable;
    }

    @Override
    public void pulse() {
        privateDead();
        if (isDead) {
            return;
        }
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            mCurrentFrequency = mOkOptions.getPulseFrequency();
            mCurrentFrequency = mCurrentFrequency < 1000 ? 1000 : mCurrentFrequency;//间隔最小为一秒
            if (mPulseHandler != null) {
                mPulseHandler.sendEmptyMessageDelayed(PULSE_WHAT, mCurrentFrequency);
            }
        }
    }

    @Override
    public void trigger() {
        privateDead();
        if (isDead) {
            return;
        }
        if (mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            if (mPulseHandler != null) {
                mPulseHandler.sendEmptyMessage(PULSE_WHAT);
            }
        }
    }

    public void dead() {
        mLoseTimes = 0;
        isDead = true;
        privateDead();
    }

    @Override
    public void feed() {
        mPulseHandler.sendEmptyMessage(FEED_WHAT);
    }

    private void privateDead() {
        if (mPulseHandler != null) {
            mPulseHandler.removeMessages(PULSE_WHAT);
        }
    }

    public int getLoseTimes() {
        return mLoseTimes;
    }

    protected void setOkOptions(OkSocketOptions okOptions) {
        mOkOptions = okOptions;
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        if (mCurrentFrequency != mOkOptions.getPulseFrequency()) {
            pulse();
        }
    }


}
