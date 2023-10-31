package com.ea.nfsmw.client.config;

import com.ea.nfsmw.client.steps.NfsMwSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

@Component
public class NfsMwSocketThread implements Runnable {
    @Autowired
    private NfsMwSocketHandler nfsMwSocketHandler;

    private Socket clientSocket;


    public void setClientSocket(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            nfsMwSocketHandler.read(clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
