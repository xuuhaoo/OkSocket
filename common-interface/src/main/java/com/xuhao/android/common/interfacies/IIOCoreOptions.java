package com.xuhao.android.common.interfacies;

import java.nio.ByteOrder;

public interface IIOCoreOptions {

    ByteOrder getReadByteOrder();

    int getMaxReadDataMB();

    IReaderProtocol getReaderProtocol();

    ByteOrder getWriteOrder();

    int getReadPackageBytes();

    int getWritePackageBytes();

}
