package com.ea.services;

import com.ea.models.SocketData;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketParser {

    /**
     * Parses requests based on current content of the stream
     * Loops until all complete requests are parsed
     * Sends complete requests to the processor
     * Remaining data represents an incomplete request (requires more data from stream)
     * @param socket
     * @param data
     * @return String the remaining data (not parsed)
     */
    public static String parse(Socket socket, String data) {
        boolean loop = true;
        while (data.length() > 11 && loop) {
            String id = data.substring(0,4);
            int length = getlength(data);
            if (data.length() >= length) {
                log.info("Request: {}", data.substring(0, length).replaceAll("\n", " "));
                String content = data.substring(12, length);
                SocketData socketData = new SocketData(id, content, null);
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
     * @param data the request
     * @return int - the size of the content
     */
    private static int getlength(String data) {
        String size = "";
        for(byte b : data.substring(8, 12).getBytes(StandardCharsets.UTF_8)) {
            size += String.format("%02x", b);
        }
        return Integer.parseInt(size, 16);
    }

}
