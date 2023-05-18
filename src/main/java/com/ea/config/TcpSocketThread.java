package com.ea.config;

import com.ea.services.AuthService;
import com.ea.steps.SocketReader;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Thread to handle a unique tcp socket
 */
@Slf4j
public class TcpSocketThread implements Runnable {

    Socket clientSocket;

    public void setClientSocket(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("TCP client session started: {} | {}", clientSocket.hashCode(), clientSocket.getInetAddress().getHostName());
        try {
            SocketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            AuthService.pingExecutor.shutdown();
            log.info("TCP client session ended: " + clientSocket.hashCode());
        }
    }

}
