package com.xuhao.android.libsocket.sdk.protocol;

import java.nio.ByteOrder;

/**
 * 包头数据格式
 * Created by xuhao on 2017/5/22.
 */
public interface IHeaderProtocol {
    /**
     * 获得包头长度
     *
     * @return 包头的长度
     */
    int getHeaderLength();

    /**
     * 从传入的包头数据中获得包体长度
     *
     * @param header    包头原始数据
     * @param byteOrder 字节序类型
     * @return 包体长度
     */
    int getBodyLength(byte[] header, ByteOrder byteOrder);
}
