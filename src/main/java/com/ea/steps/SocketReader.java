package com.ea.steps;

import com.ea.dto.SessionData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Optional;

@Slf4j
public class SocketReader {

    /**
     * Waits for data to come from the client
     * Calls a parser to handle input messages from the stream
     * @param socket the socket to read
     * @throws IOException
     */
    public static void read(Socket socket, SessionData sessionData) throws IOException {
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int readLength;
        while((readLength = is.read(buffer)) != -1) {
            SocketParser.parse(socket, sessionData, buffer, readLength);
        }
    }

}
