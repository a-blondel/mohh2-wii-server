package com.ea.services;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@Slf4j
public class SocketReader {

    /**
     * Waits for data to come from the client
     * Calls a parser to handle requests from the stream
     * @param socket the socket to read
     * @throws IOException
     */
    public static void read(Socket socket) throws IOException {
        String data = "";
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int read;
        while((read = is.read(buffer)) != -1) {
            data += new String(buffer, 0, read);
            data = SocketParser.parse(socket, buffer, data);
            if (data.length() > 0) {
                log.info("Data not parsed: " + data);
            }
        }
    }

}
