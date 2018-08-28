package com.xuhao.android.server.action;

public interface IAction {

    String SERVER_ACTION_DATA = "server_action_data";

    String ACTION_SERVER_LISTEN_SUCCESS = "action_server_listen_success";

    String ACTION_SERVER_LISTEN_FAILED = "action_server_listen_failed";

    String ACTION_CLIENT_CONNECTED = "action_client_connected";

    String ACTION_CLIENT_DISCONNECTED = "action_client_disconnected";

    String ACTION_SERVER_WILL_BE_SHUTDOWN = "action_server_will_be_shutdown";

    String ACTION_SERVER_ALLREADY_SHUTDOWN = "action_server_allready_shutdown";

}
