package com.xuhao.android.libsocket.sdk.client.connection;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.libsocket.impl.exceptions.PurifyException;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;

import java.util.Iterator;

/**
 * Created by xuhao on 2017/6/5.
 */

public class DefaultReconnectManager extends AbsReconnectionManager {
    /**
     * 默认重连时间(后面会以指数次增加)
     */
    private static final long DEFAULT = 5 * 1000;
    /**
     * 最大连接失败次数,不包括断开异常
     */
    private static final int MAX_CONNECTION_FAILED_TIMES = 12;
    /**
     * 延时连接时间
     */
    private long mReconnectTimeDelay = DEFAULT;
    /**
     * 连接失败次数,不包括断开异常
     */
    private int mConnectionFailedTimes = 0;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mDetach) {
                SLog.i("ReconnectionManager already detached by framework.We decide gave up this reconnection mission!");
                return;
            }
            boolean isHolden = mConnectionManager.getOption().isConnectionHolden();

            if (!isHolden) {
                detach();
                return;
            }
            ConnectionInfo info = mConnectionManager.getConnectionInfo();
            SLog.i("Reconnect the server " + info.getIp() + ":" + info.getPort() + " ...");
            if (!mConnectionManager.isConnect()) {
                mConnectionManager.connect();
            }
        }
    };

    @Override
    public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
        if (isNeedReconnect(e)) {//break with exception
            reconnectDelay();
        } else {
            reset();
        }
    }

    @Override
    public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
        reset();
    }

    @Override
    public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
        if (e != null) {
            mConnectionFailedTimes++;
            if (mConnectionFailedTimes > MAX_CONNECTION_FAILED_TIMES) {
                reset();
                //连接失败达到阈值,需要切换备用线路.(依照现有DEFAULT值和指数增长逻辑,会在4分多钟时切换备用线路)
                ConnectionInfo originInfo = mConnectionManager.getConnectionInfo();
                ConnectionInfo backupInfo = originInfo.getBackupInfo();
                if (backupInfo != null) {
                    ConnectionInfo bbInfo = new ConnectionInfo(originInfo.getIp(), originInfo.getPort());
                    backupInfo.setBackupInfo(bbInfo);
                    if (!mConnectionManager.isConnect()) {
                        mConnectionManager.switchConnectionInfo(backupInfo);
                        SLog.i("Prepare switch to the backup line " + backupInfo.getIp() + ":" + backupInfo.getPort() + " ...");
                        mConnectionManager.connect();
                    }
                } else {
                    reconnectDelay();
                }
            } else {
                reconnectDelay();
            }
        }
    }

    /**
     * 是否需要重连
     *
     * @param e
     * @return
     */
    private boolean isNeedReconnect(Exception e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            if (e != null && !(e instanceof PurifyException)) {//break with exception
                Iterator<Class<? extends Exception>> it = mIgnoreDisconnectExceptionList.iterator();
                while (it.hasNext()) {
                    Class<? extends Exception> classException = it.next();
                    if (classException.isAssignableFrom(e.getClass())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private void reset() {
        mHandler.removeCallbacksAndMessages(null);
        mReconnectTimeDelay = DEFAULT;
        mConnectionFailedTimes = 0;
    }

    private void reconnectDelay() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(0, mReconnectTimeDelay);
        SLog.i("Reconnect after " + mReconnectTimeDelay + " mills ...");
        mReconnectTimeDelay = mReconnectTimeDelay * 2;//5+10+20+40 = 75 4次
        if (mReconnectTimeDelay >= DEFAULT * 10) {//DEFAULT * 10 = 50
            mReconnectTimeDelay = DEFAULT;
        }
    }

    @Override
    public void detach() {
        mDetach = true;
        reset();
        super.detach();
    }
}
