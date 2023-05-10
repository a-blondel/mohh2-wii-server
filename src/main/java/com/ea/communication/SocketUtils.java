package com.ea.communication;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLSocket;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketUtils {

    public static void writeToSocket(Socket socket, ByteArrayOutputStream buffer, DataOutputStream writer,
                                     String header, StringBuffer content) throws IOException {

        writer.write(header.getBytes(StandardCharsets.UTF_8)); // header
        writer.writeInt(0); // flags
        byte[] contentBytes = content.toString().getBytes(StandardCharsets.UTF_8);
        writer.writeInt(12 + contentBytes.length); // length (12 = header (4) + flags (4) + length (4))
        writer.write(contentBytes); // content

        byte[] bufferBytes = buffer.toByteArray();
        String bufferString = new String(bufferBytes, StandardCharsets.UTF_8);

        log.info("Writing data to socket:\n" + bufferString);
        socket.getOutputStream().write(bufferBytes);
    }

}
