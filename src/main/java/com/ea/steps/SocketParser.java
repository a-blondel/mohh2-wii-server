package com.ea.steps;

import com.ea.models.SocketData;
import com.ea.utils.HexDumpUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class SocketParser {

    /**
     * Parses input messages based on current content of the stream
     * Loops until all complete messages are parsed
     * Sends complete messages to the processor
     * @param socket the socket to exchange with
     * @param buffer the buffer to read from
     * @param readLength the size of written content in buffer
     */
    public static void parse(Socket socket, byte[] buffer, int readLength) {
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

                if (!HexDumpUtil.NO_DUMP_MSG.contains(socketData.getIdMessage())) {
                    log.info("Receive:\n{}", HexDumpUtil.formatHexDump(buffer, currentMessageBegin, currentMessageLength));
                }

                SocketProcessor.process(socket, socketData);
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
