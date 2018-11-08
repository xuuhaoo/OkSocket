package com.xuhao.didi.socket.client.sdk.client.connection;


import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.socket.client.impl.exceptions.PurifyException;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.common.interfaces.basic.AbsLoopThread;

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
    private volatile long mReconnectTimeDelay = DEFAULT;
    /**
     * 连接失败次数,不包括断开异常
     */
    private int mConnectionFailedTimes = 0;

    private ReconnectTestingThread mReconnectTestingThread;

    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        if (isNeedReconnect(e)) {//break with exception
            reconnectDelay();
        } else {
            resetThread();
            resetTimes();
        }
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
        resetThread();
        resetTimes();
    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
        if (e != null) {
            mConnectionFailedTimes++;
            if (mConnectionFailedTimes > MAX_CONNECTION_FAILED_TIMES) {
                resetThread();
                resetTimes();
                //连接失败达到阈值,需要切换备用线路.
                ConnectionInfo originInfo = mConnectionManager.getConnectionInfo();
                ConnectionInfo backupInfo = originInfo.getBackupInfo();
                if (backupInfo != null) {
                    ConnectionInfo bbInfo = new ConnectionInfo(originInfo.getIp(), originInfo.getPort());
                    backupInfo.setBackupInfo(bbInfo);
                    synchronized (mConnectionManager) {
                        if (!mConnectionManager.isConnect()) {
                            SLog.i("Prepare switch to the backup line " + backupInfo.getIp() + ":" + backupInfo.getPort() + " ...");
                            mConnectionManager.switchConnectionInfo(backupInfo);
                            reconnectDelay();
                        }
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

    /**
     * 重置重连次数和延迟时间
     */
    private synchronized void resetTimes() {
        mReconnectTimeDelay = DEFAULT;
        mConnectionFailedTimes = 0;
    }

    /**
     * 重置重连线程,关闭线程
     */
    private synchronized void resetThread() {
        if (mReconnectTestingThread != null) {
            mReconnectTestingThread.shutdown();
        }
        mReconnectTestingThread = null;
    }

    /**
     * 开始延迟重连
     */
    private synchronized void reconnectDelay() {
        if (mReconnectTestingThread == null) {
            mReconnectTestingThread = new ReconnectTestingThread();
        }
        SLog.i("Reconnect after " + mReconnectTimeDelay + " mills ...");
        mReconnectTimeDelay = mReconnectTimeDelay * 2;//5+10+20+40 = 75 4次
        if (mReconnectTimeDelay >= DEFAULT * 10) {//DEFAULT * 10 = 50
            mReconnectTimeDelay = DEFAULT;
        }
    }

    @Override
    public void detach() {
        resetThread();
        resetTimes();
        super.detach();
    }

    private class ReconnectTestingThread extends AbsLoopThread {

        @Override
        protected void runInLoopThread() throws Exception {
            if (mDetach) {
                SLog.i("ReconnectionManager already detached by framework.We decide gave up this reconnection mission!");
                shutdown();
                return;
            }
            boolean isHolden = mConnectionManager.getOption().isConnectionHolden();

            if (!isHolden) {
                detach();
                shutdown();
                return;
            }
            synchronized (mConnectionManager) {
                ConnectionInfo info = mConnectionManager.getConnectionInfo();
                SLog.i("Reconnect the server " + info.getIp() + ":" + info.getPort() + " ...");
                if (!mConnectionManager.isConnect()) {
                    mConnectionManager.connect();
                } else {
                    shutdown();
                }
            }

            //延迟执行
            try {
                Thread.sleep(mReconnectTimeDelay);
            } catch (InterruptedException e) {
            }
        }

        @Override
        protected void loopFinish(Exception e) {
        }
    }
}
