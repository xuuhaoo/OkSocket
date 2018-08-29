package com.xuhao.android.server.impl.client.lfu;

class FrequencyNode extends Node {

    int mFrequency;

    NodeList mLfuCacheEntryList;

    public FrequencyNode(int frequency) {
        this.mFrequency = frequency;
        this.mLfuCacheEntryList = new NodeList();
    }

    public boolean equals(Object o) {
        return mFrequency == ((FrequencyNode) o).mFrequency;
    }

    public int hashCode() {
        return mFrequency * 31;
    }

    public String toString() {
        return Integer.toString(mFrequency);
    }
}
