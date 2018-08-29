package com.xuhao.android.common.interfacies.server;

public interface IClientPool<T, K> {

    void cache(T t);

    T find(K key);

    int size();
}
