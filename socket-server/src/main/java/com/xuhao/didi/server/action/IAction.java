package com.xuhao.didi.server.action;

public interface IAction {

    interface Server {
        String SERVER_ACTION_DATA = "server_action_data";

        String ACTION_SERVER_LISTENING = "action_server_listening";

        String ACTION_CLIENT_CONNECTED = "action_client_connected";

        String ACTION_CLIENT_DISCONNECTED = "action_client_disconnected";

        String ACTION_SERVER_WILL_BE_SHUTDOWN = "action_server_will_be_shutdown";

        String ACTION_SERVER_ALLREADY_SHUTDOWN = "action_server_allready_shutdown";

    }

    interface Client {
        String ACTION_READ_THREAD_START = "action_read_thread_start";

        String ACTION_READ_THREAD_SHUTDOWN = "action_read_thread_shutdown";

        String ACTION_WRITE_THREAD_START = "action_write_thread_start";

        String ACTION_WRITE_THREAD_SHUTDOWN = "action_write_thread_shutdown";

        String ACTION_READ_COMPLETE = "action_read_complete";

        String ACTION_WRITE_COMPLETE = "action_write_complete";
    }

}
