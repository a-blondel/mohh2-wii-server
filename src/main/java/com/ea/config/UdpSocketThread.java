package com.ea.config;

import com.ea.steps.DatagramSocketReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Thread to handle a unique udp socket
 */
@Slf4j
public class UdpSocketThread implements Runnable {

    DatagramSocket clientSocket;

    public void setClientSocket(DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("UDP client session started: {} | {}", clientSocket.hashCode(), clientSocket.getRemoteSocketAddress());
        try {
            DatagramSocketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("UDP client session ended: " + clientSocket.hashCode());
        }
    }

}
