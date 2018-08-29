package com.xuhao.android.server.impl.clientpojo;

import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.common.interfacies.server.IClientPool;
import com.xuhao.android.server.impl.clientpojo.lfu.LFUCache;

public class ClientPool extends LFUCache<String, IClient> implements IClientPool<IClient, String> {

    public ClientPool(int capacity) {
        super(capacity);
    }

    @Override
    public void cache(IClient iClient) {
        super.set(iClient.getTag() + "_" + iClient.getHostName(), iClient);
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
