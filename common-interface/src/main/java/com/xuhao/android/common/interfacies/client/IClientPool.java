package com.xuhao.android.common.interfacies.client;

public interface IClientPool<T, K> {

    void pool(T t);

    T find(K key);

    int size();
}
