package com.ea.config;

import com.ea.steps.SocketReader;
import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;

/**
 * Thread to handle a unique SSL socket
 */
@Slf4j
public class SslSocketThread implements Runnable {

    SSLSocket clientSocket;

    public void setClientSocket(SSLSocket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("SSL client session started: {} | {}", clientSocket.hashCode(), clientSocket.getRemoteSocketAddress());
        try {
            SocketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("SSL client session ended: " + clientSocket.hashCode());
        }
    }

}