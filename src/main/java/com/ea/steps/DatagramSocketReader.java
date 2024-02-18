package com.ea.steps;

import com.ea.dto.DatagramSocketData;
import com.ea.dto.SessionData;
import com.ea.utils.BeanUtil;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

@Slf4j
public class DatagramSocketReader {

    private static Props props = BeanUtil.getBean(Props.class);

    /**
     * Waits for data to come from the client
     * Calls a processor to handle input messages from the stream
     * @param socket the socket to read
     * @throws IOException
     */
    public static void read(DatagramSocket socket, SessionData sessionData) throws IOException {
        // TODO find the best way to exit
        while (true) {

            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            if (props.isUdpDebugEnabled()) {
                byte[] dump = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                log.info("Received from {}:{}:\n{}", address, port, HexDumpUtils.formatHexDump(dump));
            }

            DatagramSocketProcessor.process(socket, sessionData, new DatagramSocketData(packet, null));

        }
    }

}
