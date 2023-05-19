package com.ea.config;

import com.ea.utils.HexDumpUtil;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
            // TODO find the best way to exit
            while (true) {
                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                clientSocket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                if (Props.isActive("udp.debug")) {
                    log.info("Received from {}:{}:\n{}", address, port, HexDumpUtil.formatHexDump(packet.getData(), 0, packet.getLength()));
                }

                packet = new DatagramPacket(packet.getData(), packet.getLength(), address, port);

                clientSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("UDP client session ended: " + clientSocket.hashCode());
        }
    }

}
