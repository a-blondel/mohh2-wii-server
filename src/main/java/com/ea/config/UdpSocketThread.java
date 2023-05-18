package com.ea.config;

import com.ea.steps.SocketParser;
import com.ea.utils.HexDumpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
            while (true) {
                byte[] buf = new byte[256];

                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(receivePacket);

                log.info("Received:\n{}", HexDumpUtil.formatHexDump(receivePacket.getData(), 0, receivePacket.getLength()));

                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, port);

                clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("UDP client session ended: " + clientSocket.hashCode());
        }
    }

}
