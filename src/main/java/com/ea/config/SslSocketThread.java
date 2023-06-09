package com.ea.config;

import com.ea.steps.SocketReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;

/**
 * Thread to handle a unique SSL socket
 */
@Slf4j
@Component
public class SslSocketThread implements Runnable {

    SSLSocket clientSocket;

    @Autowired
    SocketReader socketReader;

    public void setClientSocket(SSLSocket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("SSL client session started: {} | {}", clientSocket.hashCode(), clientSocket.getRemoteSocketAddress());
        try {
            socketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("SSL client session ended: " + clientSocket.hashCode());
        }
    }

}
