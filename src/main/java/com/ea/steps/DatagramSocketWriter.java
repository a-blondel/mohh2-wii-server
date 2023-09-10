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
public class DatagramSocketWriter {
    @Autowired
    private Props props;

    /**
     * Sends packet
     * @param socket the socket to write into
     * @param socketData the object to use to write the message
     * @throws IOException
     */
    public void write(DatagramSocket socket, DatagramSocketData socketData) {

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

            if (props.isUdpDebugEnabled()) {
                log.info("Send to {}:{}:\n{}", address, port, HexDumpUtils.formatHexDump(buf, 0, buf.length));
            }

            socket.send(outputPacket);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
