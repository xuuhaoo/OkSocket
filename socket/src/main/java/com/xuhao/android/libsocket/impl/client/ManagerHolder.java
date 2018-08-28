package com.xuhao.android.libsocket.impl.client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.xuhao.android.common.interfacies.server.IServerManager;
import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.common.utils.SpiUtils;
import com.xuhao.android.libsocket.impl.client.abilities.IConnectionSwitchListener;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

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

    private SparseArray<IServerManager> mServerManagerMap = new SparseArray<>();

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();
    }

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        mConnectionManagerMap.clear();
    }

    public IServerManager getServer(int localPort, Context context) {
        IServerManager manager = mServerManagerMap.get(localPort);
        if (manager == null) {
            manager = SpiUtils.load(IServerManager.class);
            if (manager == null) {
                SLog.e("Server load error. Server plug-in are required!" +
                        " For details link to https://github.com/xuuhaoo/OkSocket");
            } else {
                synchronized (mServerManagerMap) {
                    mServerManagerMap.append(localPort, manager);
                }
                manager.initLocalPortPrivate(localPort);
                return manager;
            }
        }
        return manager;
    }

    public IConnectionManager getConnection(ConnectionInfo info, Context context) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager == null) {
            return getConnection(info, context, OkSocketOptions.getDefault());
        } else {
            return getConnection(info, context, manager.getOption());
        }
    }

    public IConnectionManager getConnection(ConnectionInfo info, Context context, OkSocketOptions okOptions) {
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
    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, Context context, OkSocketOptions okOptions) {
        AbsConnectionManager manager = new BlockConnectionManager(context, info);
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
