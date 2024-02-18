package com.ea.config;

import com.ea.dto.SessionData;
import com.ea.steps.DatagramSocketReader;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Thread to handle a unique udp socket
 */
@Slf4j
public class UdpSocketThread implements Runnable {

    @Setter
    private DatagramSocket clientSocket;

    @Setter
    private TcpSocketThread tcpSocketTread;

    public void run() {
        log.info("UDP client session started: {}", clientSocket.hashCode());
        try {
            DatagramSocketReader.read(clientSocket, tcpSocketTread.getSessionData());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("UDP client session ended: {}", clientSocket.hashCode());
        }
    }

}
