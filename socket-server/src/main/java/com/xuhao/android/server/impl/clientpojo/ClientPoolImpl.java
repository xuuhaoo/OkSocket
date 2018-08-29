package com.xuhao.android.server.impl.clientpojo;

import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.common.interfacies.server.IClientPool;
import com.xuhao.android.server.impl.clientpojo.lfu.LFUCache;
import com.xuhao.android.server.impl.clientpojo.lfu.LFUCacheCallback;
import com.xuhao.android.server.impl.clientpojo.lfu.LFUCacheEntry;

public class ClientPoolImpl extends LFUCache<String, IClient> implements IClientPool<IClient, String>,
        LFUCacheCallback<String, Client> {

    public ClientPoolImpl(int capacity) {
        super(capacity);
        setLFUChangedCallback(this);
    }

    @Override
    public void cache(IClient client) {
        super.set(client.getUniqueTag(), client);
    }

    @Override
    public IClient find(String key) {
        return get(key);
    }

    public void unCache(IClient iClient) {
        delete(iClient.getUniqueTag());
    }

    public void unCache(String key) {
        delete(key);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void onCacheAdd(LFUCacheEntry<String, Client> cachedValue) {
        //do nothing
    }

    @Override
    public void onCacheRemove(LFUCacheEntry<String, Client> cachedValue) {
        //do nothing
    }

    @Override
    public void onCacheFull(LFUCacheEntry<String, Client> cachedValue) {
        cachedValue.mValue.disconnect();
    }

    @Override
    public void onCacheEmpty() {
        //do nothing
    }
}
