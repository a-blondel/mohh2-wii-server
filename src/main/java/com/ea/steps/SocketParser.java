package com.ea.steps;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.utils.BeanUtil;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.Arrays;

@Slf4j
public class SocketParser {
    private static Props props = BeanUtil.getBean(Props.class);

    /**
     * Parses input messages based on current content of the stream
     * Loops until all complete messages are parsed
     * Sends complete messages to the processor
     * @param socket the socket to exchange with
     * @param buffer the buffer to read from
     * @param readLength the size of written content in buffer
     */
    public static void parse(Socket socket, SessionData sessionData, byte[] buffer, int readLength) {
        boolean loop = true;
        int readRemaining = Integer.valueOf(readLength);
        int lastPos = 0;

        while (readRemaining > 11 && loop) {
            int currentMessageBegin = readLength - readRemaining;
            int currentMessageLength = getlength(buffer, lastPos);
            if (readRemaining >= currentMessageLength) {
                String id = new String(buffer, currentMessageBegin, 4);
                String content = new String(Arrays.copyOfRange(buffer, currentMessageBegin + 12, currentMessageBegin + currentMessageLength));
                SocketData socketData = new SocketData(id, content, null);

                if (props.isTcpDebugEnabled() && !props.getTcpDebugExclusions().contains(socketData.getIdMessage())) {
                    byte[] dump = Arrays.copyOfRange(buffer, currentMessageBegin, currentMessageBegin + currentMessageLength);
                    log.info("Received from {}:{} :\n{}", socket.getInetAddress().getHostAddress(), socket.getPort(), HexDumpUtils.formatHexDump(dump));
                }

                SocketProcessor.process(socket, sessionData, socketData);
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
    private static int getlength(byte[] buffer, int lastPos) {
        String size = "";
        for (int i = lastPos + 8; i < lastPos + 12; i++) {
            size += String.format("%02x", buffer[i]);
        }
        return Integer.parseInt(size, 16);
    }

}
