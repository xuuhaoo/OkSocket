package com.xuhao.android.server.impl.clientpojo.lfu;

class LFUCacheEntry<K, V> extends Node {

    K mKey;

    V mValue;

    FrequencyNode mFrequencyNode;

    public LFUCacheEntry(K key, V value, FrequencyNode frequencyNode) {
        this.mKey = key;
        this.mValue = value;
        this.mFrequencyNode = frequencyNode;
    }

    public boolean equals(Object o) {
        LFUCacheEntry<K, V> entry = (LFUCacheEntry<K, V>) o;
        return mKey.equals(entry.mKey) &&
                mValue.equals(entry.mValue);
    }

    public int hashCode() {
        return mKey.hashCode() * 31 + mValue.hashCode() * 17;
    }

    public String toString() {
        return "[" + mKey.toString() + "," + mValue.toString() + "]";
    }
}
