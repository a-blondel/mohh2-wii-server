package com.ea.communication;

import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;

/**
 * Thread to handle a unique SSL socket
 */
@Slf4j
public class SSLSocketThread implements Runnable {

    SSLSocket clientSocket;

    public void setClientSocket(SSLSocket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("Client session started: {} | {}", clientSocket.hashCode(), clientSocket.getRemoteSocketAddress());
        try {
            SocketUtils.readSocket(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("Client session ended: " + clientSocket.hashCode());
        }
    }

}
