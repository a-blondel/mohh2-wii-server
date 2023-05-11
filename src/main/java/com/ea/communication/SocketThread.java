package com.ea.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Thread to handle a unique socket
 */
@Slf4j
public class SocketThread implements Runnable {

    Socket clientSocket;

    public void setClientSocket(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("Client session started: {} | {}", clientSocket.hashCode(), clientSocket.getInetAddress().getHostName());
        try {
            SocketUtils.readSocket(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("Client session ended: " + clientSocket.hashCode());
        }
    }

}
