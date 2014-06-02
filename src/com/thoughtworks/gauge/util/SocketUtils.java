package com.thoughtworks.gauge.util;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {

    private static int apiPort;

    public static int findFreePortForApi() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            apiPort = socket.getLocalPort();
            return apiPort;
        } catch (IOException e) {

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        return -1;
    }

    public static int getApiPort() {
        if (apiPort == 0) {
            apiPort = findFreePortForApi();
        }
        return apiPort;
    }
}