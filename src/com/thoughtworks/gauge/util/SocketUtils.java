package com.thoughtworks.gauge.util;

import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.util.SocketUtils");
    public static int findFreePortForApi() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) {
            LOG.debug(e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.debug(e);
                }
            }
        }
        return -1;
    }

}