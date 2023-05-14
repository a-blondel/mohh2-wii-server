package com.ea.services;

import com.ea.models.SocketData;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class SocketParser {

    /**
     * Parses requests based on current content of the stream
     * Loops until all complete requests are parsed
     * Sends complete requests to the processor
     * Remaining data represents an incomplete request (requires more data from stream)
     * @param socket the socket to exchange with
     * @param buffer the buffer to read from
     * @param data the data to parse
     * @return String the remaining data (not parsed)
     */
    public static String parse(Socket socket, byte[] buffer, String data) {
        boolean loop = true;
        int length = 0;
        while (data.length() > 11 && loop) {
            String id = data.substring(0, 4);
            length = getlength(buffer, length);
            if (data.length() >= length) {
                log.info("Receive: {}", data.substring(0, length).replaceAll("\n", " "));
                String content = data.substring(12, length);
                SocketData socketData = new SocketData(id, content, null, 0);
                SocketProcessor.process(socket, socketData);
                data = data.substring(length);
            } else {
                loop = false;
            }
        }
        return data;
    }

    /**
     * Calculate length of the content to parse
     * @param buffer the request buffer (only efficient way to get the length)
     * @param lastPos the position to begin in the buffer (there can be multiple requests in a buffer)
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
