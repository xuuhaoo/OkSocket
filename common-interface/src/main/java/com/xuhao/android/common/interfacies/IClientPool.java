package com.xuhao.android.common.interfacies;

public interface IClientPool<T, K> {

    void pool(T t);

    T find(K key);

    int size();
}
