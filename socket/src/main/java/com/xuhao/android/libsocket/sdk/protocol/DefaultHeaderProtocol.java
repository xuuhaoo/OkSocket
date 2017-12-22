package com.xuhao.android.libsocket.sdk.protocol;

import com.xuhao.android.libsocket.sdk.bean.IHeaderProtocol;
import com.xuhao.android.libsocket.utils.BytesUtils;

import java.nio.ByteOrder;

public class DefaultHeaderProtocol implements IHeaderProtocol {

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