package com.xuhao.didi.common.common_interfacies.server;


import com.xuhao.didi.core.iocore.interfaces.ISendable;

public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
