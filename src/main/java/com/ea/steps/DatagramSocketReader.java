package com.ea.steps;

import com.ea.models.DatagramSocketData;
import com.ea.utils.HexDumpUtil;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
public class DatagramSocketReader {

    /**
     * Waits for data to come from the client
     * Calls a processor to handle input messages from the stream
     * @param socket the socket to read
     * @throws IOException
     */
    public static void read(DatagramSocket socket) throws IOException {
        // TODO find the best way to exit
        while (true) {

            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            if (Props.isActive("udp.debug")) {
                log.info("Received from {}:{}:\n{}", address, port, HexDumpUtil.formatHexDump(packet.getData(), 0, packet.getLength()));
            }

            DatagramSocketProcessor.process(socket, new DatagramSocketData(packet, null));

        }
    }

}
