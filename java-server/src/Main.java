
import utils.Log;
import utils.PortCheck;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {

    public static final int DEFAULT_PORT = 8080;

    private static int port;

    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        Log.i("server is running...");
        preparePort(args);
        prepareSocketServer(args);
    }

    private static void prepareSocketServer(String[] args) {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            Log.i("server is waiting for client...");
            while (true) {
                Socket socket = serverSocket.accept();
                Log.i("client accept:" + socket.getInetAddress().getHostAddress());
                boolean isHex = false;
                if (args != null) {
                    for (String str : args) {
                        if ("-hex".equalsIgnoreCase(str)) {
                            isHex = true;
                        }
                    }
                }
                new MsgDispatcher(socket, isHex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void preparePort(String[] args) {
        Log.i("preparing port...");
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            try {
                for (int i = 0; i < args.length; i++) {
                    if ("-port".equalsIgnoreCase(args[i])) {
                        if (i < args.length - 1) {
                            port = Integer.parseInt(args[i + 1]);
                        }
                    }
                }
            } catch (Exception e) {
                port = DEFAULT_PORT;
            }
        }
        while (!PortCheck.isPortAvailable(port) && port < 65535) {
            port++;
        }
        if (port == 65535 && !PortCheck.isPortAvailable(port)) {
            throw new IllegalArgumentException(port + " port is not available");
        }
        try {
            InetAddress ia = InetAddress.getLocalHost();
            Log.i("IP: " + ia.getHostAddress() + " /Port is " + port);
        } catch (UnknownHostException e) {
            Log.i("IP: 127.0.0.1 /PORT: " + port);
        }
    }
}
