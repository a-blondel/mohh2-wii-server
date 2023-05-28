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
public class DatagramSocketWriter {

    /**
     * Sends response
     * @param socket the socket to write into
     * @param socketData the object to use to write the message
     * @throws IOException
     */
    public static void write(DatagramSocket socket, DatagramSocketData socketData) {

        try {

            DatagramPacket inputPacket = socketData.getInputPacket();
            byte[] buf = socketData.getOutputMessage();

            InetAddress address = inputPacket.getAddress();
            int port = inputPacket.getPort();

            DatagramPacket outputPacket = new DatagramPacket(
                    buf,
                    buf.length,
                    address,
                    port);

            if (Props.isActive("udp.debug")) {
                log.info("Send to {}:{}:\n{}", address, port, HexDumpUtil.formatHexDump(buf, 0, buf.length));
            }

            socket.send(outputPacket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
