package com.ea.steps;

import com.ea.dto.DatagramSocketData;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
@Component
public class DatagramSocketReader {

    @Autowired
    Props props;

    @Autowired
    DatagramSocketProcessor datagramSocketProcessor;

    /**
     * Waits for data to come from the client
     * Calls a processor to handle input messages from the stream
     * @param socket the socket to read
     * @throws IOException
     */
    public void read(DatagramSocket socket) throws IOException {
        // TODO find the best way to exit
        while (true) {

            byte[] buf = new byte[256];

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            if (props.isUdpDebugEnabled()) {
                log.info("Received from {}:{}:\n{}", address, port, HexDumpUtils.formatHexDump(packet.getData(), 0, packet.getLength()));
            }

            datagramSocketProcessor.process(socket, new DatagramSocketData(packet, null));

        }
    }

}
