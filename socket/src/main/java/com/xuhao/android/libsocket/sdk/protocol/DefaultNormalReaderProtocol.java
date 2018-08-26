package com.xuhao.android.libsocket.sdk.protocol;

import com.xuhao.android.libsocket.utils.BytesUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DefaultNormalReaderProtocol implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return 4;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        ByteBuffer bb = ByteBuffer.allocate(header.length);
        bb.order(byteOrder);
        bb.put(header);
        return bb.getInt();
    }
}