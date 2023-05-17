package com.ea.steps;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@Slf4j
public class SocketReader {

    /**
     * Waits for data to come from the client
     * Calls a parser to handle input messages from the stream
     * @param socket the socket to read
     * @throws IOException
     */
    public static void read(Socket socket) throws IOException {
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int readLength;
        while((readLength = is.read(buffer)) != -1) {
            SocketParser.parse(socket, buffer, readLength);
        }
    }

}
