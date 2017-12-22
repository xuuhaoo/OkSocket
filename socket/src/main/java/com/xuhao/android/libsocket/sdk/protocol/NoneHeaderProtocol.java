package com.xuhao.android.libsocket.sdk.protocol;

import com.xuhao.android.libsocket.sdk.bean.IHeaderProtocol;

import java.nio.ByteOrder;

/**
 * 无包头模式
 * Created by xuhao on 2017/12/22.
 */
public class NoneHeaderProtocol implements IHeaderProtocol{
    @Override
    public int getHeaderLength() {
        return 0;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        return -1;
    }

}
