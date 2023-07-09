package com.ea.config;

import com.ea.steps.DatagramSocketReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Thread to handle a unique udp socket
 */
@Slf4j
@Component
public class UdpSocketThread implements Runnable {

    DatagramSocket clientSocket;

    @Autowired
    DatagramSocketReader datagramSocketReader;

    public void setClientSocket(DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        log.info("UDP client session started: {}", clientSocket.hashCode());
        try {
            datagramSocketReader.read(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("UDP client session ended: " + clientSocket.hashCode());
        }
    }

}
