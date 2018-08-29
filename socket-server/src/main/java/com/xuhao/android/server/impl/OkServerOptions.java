package com.xuhao.android.server.impl;

import com.xuhao.android.common.interfacies.IIOCoreOptions;
import com.xuhao.android.common.interfacies.IReaderProtocol;

import java.nio.ByteOrder;

public class OkServerOptions implements IIOCoreOptions {

    private int mConnectCapacity;


    public int getConnectCapacity() {
        return mConnectCapacity;
    }

    public static OkServerOptions getDefault() {
        return new OkServerOptions();
    }

    @Override
    public ByteOrder getReadByteOrder() {
        return null;
    }

    @Override
    public int getMaxReadDataMB() {
        return 0;
    }

    @Override
    public IReaderProtocol getReaderProtocol() {
        return null;
    }

    @Override
    public ByteOrder getWriteOrder() {
        return null;
    }

    @Override
    public int getReadPackageBytes() {
        return 0;
    }

    @Override
    public int getWritePackageBytes() {
        return 0;
    }
}
