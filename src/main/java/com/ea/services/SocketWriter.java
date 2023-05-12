package com.ea.services;

import com.ea.models.SocketData;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketWriter {


    /**
     * Builds the full response based on the data id and content
     * Then sends it through the socket
     * @param socket the socket to write into
     * @param socketData the object to use to write the response
     * @throws IOException
     */
    public static void write(Socket socket, SocketData socketData) {

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             DataOutputStream writer = new DataOutputStream(buffer)) {

            writer.write(socketData.getId().getBytes(StandardCharsets.UTF_8));
            writer.writeInt(0);
            byte[] contentBytes = socketData.getResponse().getBytes(StandardCharsets.UTF_8);
            writer.writeInt(12 + contentBytes.length);
            writer.write(contentBytes);

            byte[] bufferBytes = buffer.toByteArray();
            String bufferString = new String(bufferBytes, StandardCharsets.UTF_8);

            log.info("Reply: {}", bufferString.replaceAll("\n", " "));
            socket.getOutputStream().write(bufferBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
