package com.xuhao.didi.socket.client.impl.client;

import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.socket.client.impl.client.abilities.IConnectionSwitchListener;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManagerPrivate;
import com.xuhao.didi.socket.common.interfaces.utils.SPIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhao on 2017/5/16.
 */
public class ManagerHolder {

    private volatile Map<ConnectionInfo, IConnectionManager> mConnectionManagerMap = new HashMap<>();

    private volatile Map<Integer, IServerManagerPrivate> mServerManagerMap = new HashMap<>();

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();
    }

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        mConnectionManagerMap.clear();
    }

    public IServerManager getServer(int localPort) {
        IServerManagerPrivate manager = mServerManagerMap.get(localPort);
        if (manager == null) {
            manager = (IServerManagerPrivate) SPIUtils.load(IServerManager.class);
            if (manager == null) {
                String err = "Oksocket.Server() load error. Server plug-in are required!" +
                        " For details link to https://github.com/xuuhaoo/OkSocket";
                SLog.e(err);
                throw new IllegalStateException(err);
            } else {
                synchronized (mServerManagerMap) {
                    mServerManagerMap.put(localPort, manager);
                }
                manager.initServerPrivate(localPort);
                return manager;
            }
        }
        return manager;
    }

    public IConnectionManager getConnection(ConnectionInfo info) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager == null) {
            return getConnection(info, OkSocketOptions.getDefault());
        } else {
            return getConnection(info, manager.getOption());
        }
    }

    public IConnectionManager getConnection(ConnectionInfo info, OkSocketOptions okOptions) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager != null) {
            if (!okOptions.isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(info);
                }
                return createNewManagerAndCache(info, okOptions);
            } else {
                manager.option(okOptions);
            }
            return manager;
        } else {
            return createNewManagerAndCache(info, okOptions);
        }
    }

    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, OkSocketOptions okOptions) {
        AbsConnectionManager manager = new ConnectionManagerImpl(info);
        manager.option(okOptions);
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

        Map<ConnectionInfo, IConnectionManager> map = new HashMap<>(mConnectionManagerMap);
        Iterator<ConnectionInfo> it = map.keySet().iterator();
        while (it.hasNext()) {
            ConnectionInfo info = it.next();
            IConnectionManager manager = map.get(info);
            if (!manager.getOption().isConnectionHolden()) {
                it.remove();
                continue;
            }
            list.add(manager);
        }
        return list;
    }


}
