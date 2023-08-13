package com.ea.config;

import com.ea.dto.SocketData;
import com.ea.steps.SocketReader;
import com.ea.steps.SocketWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class TcpSocketThread implements Runnable {

    @Autowired
    SocketReader socketReader;

    @Autowired
    SocketWriter socketWriter;

    Socket clientSocket;

    private ScheduledExecutorService pingExecutor;

    public void setClientSocket(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("TCP client session started: {} | {}", clientSocket.hashCode(), clientSocket.getInetAddress().getHostName());
        try {
            pingExecutor = Executors.newSingleThreadScheduledExecutor();
            pingExecutor.scheduleAtFixedRate(() -> png(clientSocket), 30, 30, TimeUnit.SECONDS);

            socketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pingExecutor.shutdown();
            log.info("TCP client session ended: " + clientSocket.hashCode());
        }
    }

    public void png(Socket socket) {
        SocketData socketData = new SocketData("~png", null, null);
        socketWriter.write(socket, socketData);
    }

}
