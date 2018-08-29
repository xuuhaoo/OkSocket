package com.xuhao.android.server.impl.client;

import com.xuhao.android.common.interfacies.client.IClient;
import com.xuhao.android.common.interfacies.client.IClientPool;
import com.xuhao.android.server.impl.client.lfu.LFUCache;

public class ClientPool extends LFUCache<String, IClient> implements IClientPool<IClient, String> {

    public ClientPool(int capacity) {
        super(capacity);
    }

    @Override
    public void cache(IClient iClient) {
        super.set(iClient.getTag() + "_" + iClient.getIp(), iClient);
    }

    @Override
    public IClient find(String key) {
        return get(key);
    }

    @Override
    public int size() {
        return super.size();
    }
}
