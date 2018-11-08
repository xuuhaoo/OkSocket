package com.xuhao.didi.socket.client.sdk.client;

import java.io.Serializable;

/**
 * 连接信息服务类
 * Created by xuhao on 2017/5/16.
 */
public final class ConnectionInfo implements Serializable, Cloneable {
    /**
     * IPV4地址
     */
    private String mIp;
    /**
     * 连接服务器端口号
     */
    private int mPort;
    /**
     * 当此IP地址Ping不通时的备用IP
     */
    private ConnectionInfo mBackupInfo;

    public ConnectionInfo(String ip, int port) {
        this.mIp = ip;
        this.mPort = port;
    }

    /**
     * 获取传入的IP地址
     *
     * @return ip地址
     */
    public String getIp() {
        return mIp;
    }

    /**
     * 获取传入的端口号
     *
     * @return 端口号
     */
    public int getPort() {
        return mPort;
    }

    /**
     * 获取备用的Ip和端口号
     *
     * @return 备用的端口号和IP地址
     */
    public ConnectionInfo getBackupInfo() {
        return mBackupInfo;
    }

    /**
     * 设置备用的IP和端口号,可以不设置
     *
     * @param backupInfo 备用的IP和端口号信息
     */
    public void setBackupInfo(ConnectionInfo backupInfo) {
        mBackupInfo = backupInfo;
    }

    @Override
    public ConnectionInfo clone() {
        ConnectionInfo connectionInfo = new ConnectionInfo(mIp, mPort);
        if (mBackupInfo != null) {
            connectionInfo.setBackupInfo(mBackupInfo.clone());
        }
        return connectionInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ConnectionInfo)) { return false; }

        ConnectionInfo connectInfo = (ConnectionInfo) o;

        if (mPort != connectInfo.mPort) { return false; }
        return mIp.equals(connectInfo.mIp);
    }

    @Override
    public int hashCode() {
        int result = mIp.hashCode();
        result = 31 * result + mPort;
        return result;
    }
}
