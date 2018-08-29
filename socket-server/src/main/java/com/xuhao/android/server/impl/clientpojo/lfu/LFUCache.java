package com.xuhao.android.server.impl.clientpojo.lfu;

import java.util.HashMap;

public class LFUCache<K, V> {
    /**
     * Key mValue store for LFU cache
     * The mKey is normal mKey and mValue is a reference to
     * a node in mFrequency list. This node will point to
     * its parent which is mHead of linkedlist for particular
     * mFrequency
     */
    HashMap<K, LFUCacheEntry<K, V>> mKvStore;

    /**
     * A Doubly linked list of mFrequency nodes
     */
    NodeList mFreqList;

    /**
     * HashMap for storing mFrequencyNode entries
     */
    HashMap<Integer, FrequencyNode> mFrequencyMap;

    /**
     * Capacity of cache
     */
    int mCapacity;

    /**
     * current size of Cache
     */
    int mSize;

    /**
     * callback when cache changed
     */
    LFUCacheCallback mCallback;

    public LFUCache(int capacity) {
        this.mCapacity = capacity;
        mSize = 0;
        mKvStore = new HashMap<K, LFUCacheEntry<K, V>>();
        mFreqList = new NodeList();
        mFrequencyMap = new HashMap<Integer, FrequencyNode>();
    }

    protected void delete(K key) {
        LFUCacheEntry<K, V> entry = mKvStore.get(key);
        delete(entry);
    }

    protected void delete(LFUCacheEntry<K, V> entry) {
        if (!mKvStore.containsKey(entry.mKey)) {
            return;
        }

        mKvStore.remove(entry.mKey);
        entry.mFrequencyNode.mLfuCacheEntryList.remove(entry);
        if (entry.mFrequencyNode.mLfuCacheEntryList.mLength <= 0) {
            mFrequencyMap.remove(entry.mFrequencyNode.mFrequency);
            mFreqList.remove(entry.mFrequencyNode);
        }
        mSize--;
        if (mCallback != null) {
            mCallback.onCacheRemove(entry);
        }

        if (mSize == 0) {
            mCallback.onCacheEmpty();
        }
    }


    private FrequencyNode getFrequencyNode(int frequency) {
        if (!mFrequencyMap.containsKey(frequency - 1) &&
                !mFrequencyMap.containsKey(frequency) &&
                frequency != 1) {
            System.out.println("Request for Frequency Node " + frequency +
                    " But " + frequency + " or " + (frequency - 1) +
                    " Doesn't exist");
            return null;
        }

        if (!mFrequencyMap.containsKey(frequency)) {
            FrequencyNode newFrequencyNode = new FrequencyNode(frequency);
            if (frequency != 1)
                mFreqList.insertAfter(mFrequencyMap.get(frequency - 1),
                        newFrequencyNode);
            else
                mFreqList.prepend(newFrequencyNode);
            mFrequencyMap.put(frequency, newFrequencyNode);
        }

        return mFrequencyMap.get(frequency);
    }

    protected void set(K key, V value) {
        if (mCapacity == 0)
            return;
        FrequencyNode newFrequencyNode = null;
        if (mKvStore.containsKey(key)) {
            /* Remove old mKey if exists */
            newFrequencyNode = getFrequencyNode(mKvStore.get(key).mFrequencyNode.mFrequency + 1);
            delete(mKvStore.get(key));
        } else if (mSize == mCapacity) {
            /* If cache mSize if full remove first element from freq list */
            FrequencyNode fNode = (FrequencyNode) mFreqList.mHead;
            LFUCacheEntry<K, V> entry = (LFUCacheEntry<K, V>) fNode.mLfuCacheEntryList.mHead;
            if (mCallback != null) {
                mCallback.onCacheFull(entry);
            }
            System.out.println("Cache full. Removed entry " + entry);
        }
        if (newFrequencyNode == null)
            newFrequencyNode = getFrequencyNode(1);
        LFUCacheEntry<K, V> entry = new LFUCacheEntry<K, V>(key, value,
                newFrequencyNode);
        mKvStore.put(key, entry);
        newFrequencyNode.mLfuCacheEntryList.append(entry);
        mSize++;
        if (mCallback != null) {
            mCallback.onCacheAdd(entry);
        }
        System.out.println("Set new " + entry + " entry, cache mSize: " + mSize);
    }


    protected V get(K key) {
        if (!mKvStore.containsKey(key) || mCapacity == 0) {
            return null;
        }

        LFUCacheEntry<K, V> entry = mKvStore.get(key);
        FrequencyNode newFrequencyNode = getFrequencyNode(entry.mFrequencyNode.mFrequency + 1);
        entry.mFrequencyNode.mLfuCacheEntryList.remove(entry);
        newFrequencyNode.mLfuCacheEntryList.append(entry);
        if (entry.mFrequencyNode.mLfuCacheEntryList.mLength <= 0) {
            mFrequencyMap.remove(entry.mFrequencyNode.mFrequency);
            mFreqList.remove(entry.mFrequencyNode);
        }
        entry.mFrequencyNode = newFrequencyNode;

        return entry.mValue;
    }

    protected int size() {
        return mSize;
    }

    protected void setLFUChangedCallback(LFUCacheCallback callback) {
        mCallback = callback;
    }
}





