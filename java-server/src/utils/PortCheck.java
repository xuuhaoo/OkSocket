package utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortCheck {

    private static void bindPort(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    public static boolean isPortAvailable(int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort("127.0.0.1", port);
            return true;
        } catch (Exception e) {
            return false;

        }
    }
}
