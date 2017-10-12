package com.xuhao.android.libsocket.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xuhao.android.libsocket.impl.abilities.IConnectionSwitchListener;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhao on 2017/5/16.
 */

public class ManagerHolder {
    private Map<ConnectionInfo, IConnectionManager> mConnectionManagerMap = new HashMap<>();

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();
    }

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        mConnectionManagerMap.clear();
    }

    public IConnectionManager get(ConnectionInfo info, Context context, OkSocketOptions okOptions) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager != null) {
            if (!okOptions.isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(info);
                }
                return createNewManagerAndCache(info, context, okOptions);
            } else {
                manager.option(okOptions);
            }
            return manager;
        } else {
            return createNewManagerAndCache(info, context, okOptions);
        }
    }

    @NonNull
    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, Context context,
            OkSocketOptions okOptions) {
        AbsConnectionManager manager;
        if (okOptions.isBlockSocket()) {
            manager = new BlockConnectionManager(context, info, okOptions);
        } else {
            manager = new UnBlockConnectionManager(context, info, okOptions);
        }
        manager.setOnConnectionSwitchListener(new IConnectionSwitchListener() {
            @Override
            public void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo,
                    ConnectionInfo newInfo) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(oldInfo);
                    mConnectionManagerMap.put(newInfo, manager);
                }
            }
        });
        synchronized (mConnectionManagerMap) {
            mConnectionManagerMap.put(info, manager);
        }
        return manager;
    }

    protected List<IConnectionManager> getList() {
        List<IConnectionManager> list = new ArrayList<>();
        Iterator<ConnectionInfo> it = mConnectionManagerMap.keySet().iterator();
        while (it.hasNext()) {
            ConnectionInfo info = it.next();
            IConnectionManager manager = mConnectionManagerMap.get(info);
            if (!manager.getOption().isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    it.remove();
                }
                continue;
            }
            list.add(manager);
        }
        return list;
    }


}
