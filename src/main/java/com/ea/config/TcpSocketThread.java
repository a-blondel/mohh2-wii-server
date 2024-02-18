package com.ea.config;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.services.LobbyService;
import com.ea.services.PersonaService;
import com.ea.steps.SocketReader;
import com.ea.steps.SocketWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Thread to handle a unique tcp socket
 */
@Slf4j
public class TcpSocketThread implements Runnable {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private LobbyService lobbyService;

    @Setter
    private Socket clientSocket;

    @Getter
    private SessionData sessionData;

    private ScheduledExecutorService pingExecutor;

    public void run() {
        log.info("TCP client session started: {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
        try {
            sessionData = new SessionData();
            pingExecutor = Executors.newSingleThreadScheduledExecutor();
            pingExecutor.scheduleAtFixedRate(() -> png(clientSocket), 30, 30, TimeUnit.SECONDS);

            SocketReader.read(clientSocket, sessionData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pingExecutor.shutdown();
            lobbyService.endLobbyReport(sessionData); // If the player doesn't leave from the game
            personaService.endPersonaConnection(sessionData);
            log.info("TCP client session ended: {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
        }
    }

    public void png(Socket socket) {
        SocketData socketData = new SocketData("~png", null, null);
        SocketWriter.write(socket, socketData);
    }

}
