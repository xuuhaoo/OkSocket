package com.xuhao.android.libsocket.sdk;

import com.xuhao.android.libsocket.sdk.bean.IHeaderProtocol;
import com.xuhao.android.libsocket.sdk.connection.AbsReconnectionManager;
import com.xuhao.android.libsocket.sdk.connection.DefaultReconnectManager;
import com.xuhao.android.libsocket.utils.BytesUtils;

import java.nio.ByteOrder;

/**
 * OkSocket参数配置类<br>
 * Created by xuhao on 2017/5/16.
 */
public class OkSocketOptions {
    /**
     * 框架是否是调试模式
     */
    protected static boolean isDebug;
    /**
     * Socket通讯模式
     * <p>
     * 请注意:<br>
     * 阻塞式仅支持冷切换(断开后切换)<br>
     * 非阻塞式可以热切换<br>
     * </p>
     */
    private IOThreadMode mIOThreadMode;
    /**
     * 连接是否管理保存<br>
     * <p>
     * true:连接将会保存在管理器中,进行性能优化和断线重连<br>
     * false:不会保存在管理器中,对于已经保存的会进行删除,将不进行性能优化和断线重连.
     * </p>
     */
    private boolean isConnectionHolden;
    /**
     * 写入Socket管道中给服务器的字节序
     */
    private ByteOrder mWriteOrder;
    /**
     * 从Socket管道中读取字节序时的字节序
     */
    private ByteOrder mReadByteOrder;
    /**
     * Socket通讯中,业务层定义的数据包包头格式
     */
    private IHeaderProtocol mHeaderProtocol;
    /**
     * 发送给服务器时单个数据包的总长度
     */
    private int mSendSinglePackageBytes;
    /**
     * 从服务器读取时单次读取的缓存字节长度,数值越大,读取效率越高.但是相应的系统消耗将越大
     */
    private int mReadSingleTimeBufferBytes;
    /**
     * 脉搏频率单位是毫秒
     */
    private long mPulseFrequency;
    /**
     * 脉搏丢失次数<br>
     * 大于或等于丢失次数时将断开该通道的连接<br>
     * 抛出{@link com.xuhao.android.libsocket.impl.exceptions.DogDeadException}
     */
    private int mPulseFeedLoseTimes;
    /**
     * 后台存活时间(分钟)
     * -1为永久存活
     */
    private int mBackgroundLiveMinute;
    /**
     * 连接超时时间(秒)
     */
    private int mConnectTimeoutSecond;
    /**
     * 最大读取数据的兆数(MB)<br>
     * 防止服务器返回数据体过大的数据导致前端内存溢出.
     */
    private int mMaxReadDataMB;
    /**
     * 是否使用阻塞Socket(不可修改,暂不提供修改方法)
     */
    private boolean isBlockSocket;
    /**
     * 重新连接管理器
     * TODO: 目前仅有阻塞式支持重连管理器逻辑,非阻塞式还未支持
     */
    private AbsReconnectionManager mReconnectionManager;

    private OkSocketOptions() {
    }

    public static class Builder {
        /**
         * 是否使用阻塞Socket(不可修改,暂不提供修改方法)
         */
        private boolean isBlockSocket;
        /**
         * Socket通讯模式
         * <p>
         * 请注意:<br>
         * 阻塞式仅支持冷切换(断开后切换)<br>
         * 非阻塞式可以热切换<br>
         * </p>
         */
        private IOThreadMode mIOThreadMode;
        /**
         * 脉搏频率单位是毫秒
         */
        private long mPulseFrequency;
        /**
         * 最大读取数据的兆数(MB)<br>
         * 防止服务器返回数据体过大的数据导致前端内存溢出.
         */
        private int mMaxReadDataMB;
        /**
         * Socket通讯中,业务层定义的数据包包头格式
         */
        private IHeaderProtocol mHeaderProtocol;
        /**
         * 后台存活时间(分钟)
         * -1为永久存活
         */
        private int mBackgroundLiveMinute;
        /**
         * 连接超时时间(秒)
         */
        private int mConnectTimeoutSecond;
        /**
         * 发送给服务器时单个数据包的总长度
         */
        private int mSinglePackageBytes;
        /**
         * 从服务器读取时单次读取的缓存字节长度,数值越大,读取效率越高.但是相应的系统消耗将越大
         */
        private int mReadSingleTimeBufferBytes;
        /**
         * 写入Socket管道中给服务器的字节序
         */
        private ByteOrder mWriteOrder;
        /**
         * 从Socket管道中读取字节序时的字节序
         */
        private ByteOrder mReadByteOrder;
        /**
         * 连接是否管理保存<br>
         * <p>
         * true:连接将会保存在管理器中,进行性能优化和断线重连<br>
         * false:不会保存在管理器中,对于已经保存的会进行删除,将不进行性能优化和断线重连.
         * </p>
         */
        private boolean isConnectionHolde;
        /**
         * 脉搏丢失次数<br>
         * 大于或等于丢失次数时将断开该通道的连接<br>
         * 抛出{@link com.xuhao.android.libsocket.impl.exceptions.DogDeadException}
         */
        private int mPulseFeedLoseTimes;
        /**
         * 重新连接管理器
         * TODO: 目前仅有阻塞式支持重连管理器逻辑,非阻塞式还未支持
         */
        private AbsReconnectionManager mReconnectionManager;

        public Builder() {
        }

        public Builder(OkSocketOptions okOptions) {
            isBlockSocket = okOptions.isBlockSocket;

            mIOThreadMode = okOptions.mIOThreadMode;
            mPulseFrequency = okOptions.mPulseFrequency;
            mMaxReadDataMB = okOptions.mMaxReadDataMB;
            mHeaderProtocol = okOptions.mHeaderProtocol;
            mBackgroundLiveMinute = okOptions.mBackgroundLiveMinute;
            mConnectTimeoutSecond = okOptions.mConnectTimeoutSecond;
            mSinglePackageBytes = okOptions.mSendSinglePackageBytes;
            mReadSingleTimeBufferBytes = okOptions.mReadSingleTimeBufferBytes;
            mWriteOrder = okOptions.mWriteOrder;
            mReadByteOrder = okOptions.mReadByteOrder;
            isConnectionHolde = okOptions.isConnectionHolden;
            mPulseFeedLoseTimes = okOptions.mPulseFeedLoseTimes;
            mReconnectionManager = okOptions.mReconnectionManager;
        }

        public Builder setIOThreadMode(IOThreadMode IOThreadMode) {
            mIOThreadMode = IOThreadMode;
            return this;
        }

        public Builder setMaxReadDataMB(int maxReadDataMB) {
            mMaxReadDataMB = maxReadDataMB;
            return this;
        }

        public Builder setHeaderProtocol(IHeaderProtocol headerProtocol) {
            mHeaderProtocol = headerProtocol;
            return this;
        }

        public Builder setPulseFrequency(long pulseFrequency) {
            mPulseFrequency = pulseFrequency;
            return this;
        }

        public Builder setConnectionHolde(boolean connectionHolde) {
            isConnectionHolde = connectionHolde;
            return this;
        }

        public Builder setPulseFeedLoseTimes(int pulseFeedLoseTimes) {
            mPulseFeedLoseTimes = pulseFeedLoseTimes;
            return this;
        }

        public Builder setWriteOrder(ByteOrder writeOrder) {
            mWriteOrder = writeOrder;
            return this;
        }

        public Builder setReadByteOrder(ByteOrder readByteOrder) {
            mReadByteOrder = readByteOrder;
            return this;
        }

        public Builder setBackgroundLiveMinute(int backgroundLiveMinute) {
            mBackgroundLiveMinute = backgroundLiveMinute;
            return this;
        }

        public Builder setReadSingleTimeBufferBytes(int readSingleTimeBufferBytes) {
            mReadSingleTimeBufferBytes = readSingleTimeBufferBytes;
            return this;
        }

        public Builder setConnectTimeoutSecond(int connectTimeoutSecond) {
            mConnectTimeoutSecond = connectTimeoutSecond;
            return this;
        }

        public Builder setSinglePackageBytes(int singlePackageBytes) {
            mSinglePackageBytes = singlePackageBytes;
            return this;
        }

        public Builder setReconnectionManager(
                AbsReconnectionManager reconnectionManager) {
            mReconnectionManager = reconnectionManager;
            return this;
        }

        public OkSocketOptions build() {
            OkSocketOptions okOptions = new OkSocketOptions();

            okOptions.isDebug = isDebug;
            okOptions.mIOThreadMode = mIOThreadMode;
            okOptions.mPulseFrequency = mPulseFrequency;
            okOptions.mMaxReadDataMB = mMaxReadDataMB;
            okOptions.mHeaderProtocol = mHeaderProtocol;
            okOptions.mBackgroundLiveMinute = mBackgroundLiveMinute;
            okOptions.mConnectTimeoutSecond = mConnectTimeoutSecond;
            okOptions.mSendSinglePackageBytes = mSinglePackageBytes;
            okOptions.mReadSingleTimeBufferBytes = mReadSingleTimeBufferBytes;
            okOptions.mWriteOrder = mWriteOrder;
            okOptions.mReadByteOrder = mReadByteOrder;
            okOptions.isBlockSocket = isBlockSocket;
            okOptions.isConnectionHolden = isConnectionHolde;
            okOptions.mPulseFeedLoseTimes = mPulseFeedLoseTimes;
            okOptions.mReconnectionManager = mReconnectionManager;
            return okOptions;
        }
    }

    public IOThreadMode getIOThreadMode() {
        return mIOThreadMode;
    }

    public long getPulseFrequency() {
        return mPulseFrequency;
    }

    public int getBackgroundLiveMinute() {
        return mBackgroundLiveMinute;
    }

    public int getSendSinglePackageBytes() {
        return mSendSinglePackageBytes;
    }

    public int getReadSingleTimeBufferBytes() {
        return mReadSingleTimeBufferBytes;
    }

    public int getConnectTimeoutSecond() {
        return mConnectTimeoutSecond;
    }

    public ByteOrder getWriteOrder() {
        return mWriteOrder;
    }

    public IHeaderProtocol getHeaderProtocol() {
        return mHeaderProtocol;
    }

    public int getMaxReadDataMB() {
        return mMaxReadDataMB;
    }

    public boolean isConnectionHolden() {
        return isConnectionHolden;
    }

    public ByteOrder getReadByteOrder() {
        return mReadByteOrder;
    }

    public boolean isBlockSocket() {
        return isBlockSocket;
    }

    public int getPulseFeedLoseTimes() {
        return mPulseFeedLoseTimes;
    }

    public AbsReconnectionManager getReconnectionManager() {
        return mReconnectionManager;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static OkSocketOptions getDefault() {
        OkSocketOptions okOptions = new OkSocketOptions();
        okOptions.mBackgroundLiveMinute = 1;
        okOptions.mPulseFrequency = 5 * 1000;
        okOptions.mIOThreadMode = IOThreadMode.DUPLEX;
        okOptions.mHeaderProtocol = new DefaultHeaderProtocol();
        okOptions.mMaxReadDataMB = 10;
        okOptions.mConnectTimeoutSecond = 3;
        okOptions.mSendSinglePackageBytes = 50;
        okOptions.mReadSingleTimeBufferBytes = 50;
        okOptions.mReadByteOrder = ByteOrder.BIG_ENDIAN;
        okOptions.mWriteOrder = ByteOrder.BIG_ENDIAN;
        okOptions.isBlockSocket = true;
        okOptions.isConnectionHolden = true;
        okOptions.mPulseFeedLoseTimes = 5;
        okOptions.mReconnectionManager = new DefaultReconnectManager();
        return okOptions;
    }

    /**
     * 线程模式
     */
    public enum IOThreadMode {
        /**
         * 单工通讯
         */
        SIMPLEX,
        /**
         * 双工通讯
         */
        DUPLEX;
    }

    private static class DefaultHeaderProtocol implements IHeaderProtocol {

        @Override
        public int getHeaderLength() {
            return 4;
        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            if (header == null || header.length == 0) {
                return 0;
            }
            if (ByteOrder.BIG_ENDIAN.toString().equals(byteOrder.toString())) {
                return BytesUtils.bytesToInt2(header, 0);
            } else {
                return BytesUtils.bytesToInt(header, 0);
            }
        }
    }
}