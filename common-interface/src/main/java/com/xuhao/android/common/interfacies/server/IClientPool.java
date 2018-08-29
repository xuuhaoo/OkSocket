package com.xuhao.android.common.interfacies.server;

import com.xuhao.android.common.interfacies.client.msg.ISendable;

public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
