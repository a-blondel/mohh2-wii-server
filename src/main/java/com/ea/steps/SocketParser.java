package com.ea.steps;

import com.ea.dto.SocketData;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Slf4j
@Component
public class SocketParser {

    @Autowired
    private Props props;

    @Autowired
    private SocketProcessor socketProcessor;

    /**
     * Parses input messages based on current content of the stream
     * Loops until all complete messages are parsed
     * Sends complete messages to the processor
     * @param socket the socket to exchange with
     * @param buffer the buffer to read from
     * @param readLength the size of written content in buffer
     */
    public void parse(Socket socket, byte[] buffer, int readLength) {
        boolean loop = true;
        int readRemaining = Integer.valueOf(readLength);
        int lastPos = 0;

        while (readRemaining > 11 && loop) {
            int currentMessageBegin = readLength - readRemaining;
            int currentMessageLength = getlength(buffer, lastPos);
            if (readRemaining >= currentMessageLength) {
                String id = new String(buffer, currentMessageBegin, 4);
                String content = new String(buffer, currentMessageBegin + 12, currentMessageLength);
                SocketData socketData = new SocketData(id, content, null);

                if (props.isTcpDebugEnabled() && !props.getTcpDebugExclusions().contains(socketData.getIdMessage())) {
                    log.info("Receive:\n{}", HexDumpUtils.formatHexDump(buffer, currentMessageBegin, currentMessageLength));
                }

                socketProcessor.process(socket, socketData);
                readRemaining -= currentMessageLength;
                lastPos += currentMessageLength;
            } else {
                log.info("Cannot parse data");
                loop = false;
            }
        }
    }

    /**
     * Calculate length of the content to parse
     * @param buffer the request buffer (only efficient way to get the length)
     * @param lastPos the position to begin in the buffer (there can be multiple messages in a buffer)
     * @return int - the size of the content
     */
    private int getlength(byte[] buffer, int lastPos) {
        String size = "";
        for (int i = lastPos + 8; i < lastPos + 12; i++) {
            size += String.format("%02x", buffer[i]);
        }
        return Integer.parseInt(size, 16);
    }

}
