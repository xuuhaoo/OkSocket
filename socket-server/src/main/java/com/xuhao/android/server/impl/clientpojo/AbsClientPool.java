package com.xuhao.android.server.impl.clientpojo;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbsClientPool<K, V> {
    private LinkedHashMap<K, V> mHashMap = new LinkedHashMap<>();

    private int mCapacity;

    public AbsClientPool(int capacity) {
        mCapacity = capacity;
    }

    void set(K key, V value) {
        V old = mHashMap.get(key);
        if (old != null) {
            onCacheDuplicate(key, old);
        }
        if (mCapacity == mHashMap.size()) {
            Map.Entry<K, V> entry = getTail();
            onCacheFull(entry.getKey(), entry.getValue());
        }

        if (mHashMap.containsKey(key)) {
            return;
        }

        if (mCapacity == mHashMap.size()) {
            return;
        }
        mHashMap.put(key, value);
    }

    V get(K key) {
        return mHashMap.get(key);
    }

    void remove(K key) {
        mHashMap.remove(key);
        if (mHashMap.isEmpty()) {
            onCacheEmpty();
        }
    }

    int size() {
        return mHashMap.size();
    }

    void echoRun(Echo echo) {
        if (echo == null) {
            return;
        }
        Iterator<Map.Entry<K, V>> iterator = mHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> node = iterator.next();
            echo.onEcho(node.getKey(), node.getValue());
        }
    }

    interface Echo<K, V> {
        void onEcho(K key, V value);
    }

    private Map.Entry<K, V> getTail() {
        if (mHashMap.isEmpty()) {
            return null;
        }
        Iterator<Map.Entry<K, V>> iterator = mHashMap.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }

    abstract void onCacheFull(K key, V lastOne);

    abstract void onCacheDuplicate(K key, V oldOne);

    abstract void onCacheEmpty();
}
