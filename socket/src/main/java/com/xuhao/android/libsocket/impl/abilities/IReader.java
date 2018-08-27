package com.xuhao.android.libsocket.impl.abilities;

import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IReader {

    @WorkerThread
    void read() throws RuntimeException;

    @MainThread
    void setOption(OkSocketOptions option);

    void close();
}
