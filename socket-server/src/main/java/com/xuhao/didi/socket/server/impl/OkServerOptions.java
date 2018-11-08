package com.xuhao.didi.socket.server.impl;


import com.xuhao.didi.core.iocore.interfaces.IIOCoreOptions;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.common.interfaces.default_protocol.DefaultNormalReaderProtocol;

import java.nio.ByteOrder;

public class OkServerOptions implements IIOCoreOptions {
    private static boolean isDebug;
    /**
     * 服务器连接能力数
     */
    private int mConnectCapacity;
    /**
     * 写入Socket管道中的字节序
     */
    private ByteOrder mWriteOrder;
    /**
     * 从Socket管道中读取字节序时的字节序
     */
    private ByteOrder mReadOrder;
    /**
     * Socket通讯中,业务层定义的数据包包头格式
     */
    private IReaderProtocol mReaderProtocol;
    /**
     * 发送时单个数据包的总长度
     */
    private int mWritePackageBytes;
    /**
     * 读取时单次读取的缓存字节长度,数值越大,读取效率越高.但是相应的系统消耗将越大
     */
    private int mReadPackageBytes;
    /**
     * 最大读取数据的兆数(MB)<br>
     * 防止数据体过大的数据导致前端内存溢出.
     */
    private int mMaxReadDataMB;

    private OkServerOptions() {
    }

    public static void setIsDebug(boolean isDebug) {
        OkServerOptions.isDebug = isDebug;
    }

    public static OkServerOptions getDefault() {
        OkServerOptions okOptions = new OkServerOptions();
        okOptions.mReaderProtocol = new DefaultNormalReaderProtocol();
        okOptions.mConnectCapacity = 50;
        okOptions.mMaxReadDataMB = 10;
        okOptions.mWritePackageBytes = 100;
        okOptions.mReadPackageBytes = 50;
        okOptions.mReadOrder = ByteOrder.BIG_ENDIAN;
        okOptions.mWriteOrder = ByteOrder.BIG_ENDIAN;
        return okOptions;
    }

    public static class Builder {
        private OkServerOptions mOptions;

        public Builder() {
            mOptions = getDefault();
        }

        public Builder(OkServerOptions options) {
            OkServerOptions clone = new OkServerOptions();
            clone.mReaderProtocol = options.mReaderProtocol;
            clone.mConnectCapacity = options.mConnectCapacity;
            clone.mMaxReadDataMB = options.mMaxReadDataMB;
            clone.mWritePackageBytes = options.mWritePackageBytes;
            clone.mReadPackageBytes = options.mReadPackageBytes;
            clone.mReadOrder = options.mReadOrder;
            clone.mWriteOrder = options.mWriteOrder;
            mOptions = clone;
        }

        public Builder setConnectCapacity(int connectCapacity) {
            mOptions.mConnectCapacity = connectCapacity;
            return this;
        }

        public Builder setWriteOrder(ByteOrder writeOrder) {
            mOptions.mWriteOrder = writeOrder;
            return this;
        }

        public Builder setReadOrder(ByteOrder readOrder) {
            mOptions.mReadOrder = readOrder;
            return this;
        }

        public Builder setReaderProtocol(IReaderProtocol readerProtocol) {
            mOptions.mReaderProtocol = readerProtocol;
            return this;
        }

        public Builder setWritePackageBytes(int writePackageBytes) {
            mOptions.mWritePackageBytes = writePackageBytes;
            return this;
        }

        public Builder setReadPackageBytes(int readPackageBytes) {
            mOptions.mReadPackageBytes = readPackageBytes;
            return this;
        }

        public Builder setMaxReadDataMB(int maxReadDataMB) {
            mOptions.mMaxReadDataMB = maxReadDataMB;
            return this;
        }

        public OkServerOptions build() {
            return mOptions;
        }
    }


    public int getConnectCapacity() {
        return mConnectCapacity;
    }

    @Override
    public ByteOrder getReadByteOrder() {
        return mReadOrder;
    }

    @Override
    public int getMaxReadDataMB() {
        return mMaxReadDataMB;
    }

    @Override
    public IReaderProtocol getReaderProtocol() {
        return mReaderProtocol;
    }

    @Override
    public ByteOrder getWriteByteOrder() {
        return mWriteOrder;
    }

    @Override
    public int getReadPackageBytes() {
        return mReadPackageBytes;
    }

    @Override
    public int getWritePackageBytes() {
        return mWritePackageBytes;
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }
}
