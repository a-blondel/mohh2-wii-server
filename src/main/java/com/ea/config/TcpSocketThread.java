package com.ea.config;

import com.ea.models.SocketData;
import com.ea.steps.SocketReader;
import com.ea.steps.SocketWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Thread to handle a unique tcp socket
 */
@Slf4j
public class TcpSocketThread implements Runnable {

    Socket clientSocket;

    private static ScheduledExecutorService pingExecutor;

    public void setClientSocket(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("TCP client session started: {} | {}", clientSocket.hashCode(), clientSocket.getInetAddress().getHostName());
        try {
            pingExecutor = Executors.newSingleThreadScheduledExecutor();
            pingExecutor.scheduleAtFixedRate(() -> startPing(clientSocket), 30, 30, TimeUnit.SECONDS);

            SocketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pingExecutor.shutdown();
            log.info("TCP client session ended: " + clientSocket.hashCode());
        }
    }

    public static void startPing(Socket socket) {
        SocketData socketData = new SocketData("~png", null, null);
        SocketWriter.write(socket, socketData);
    }

}
