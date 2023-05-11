package com.ea.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketUtils {

    public static void readSocket(Socket socket) throws IOException {
        String data = "";
        InputStream is = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int read;
        while((read = is.read(buffer)) != -1) {
            data += new String(buffer, 0, read);
            data = parseData(socket, data);
        }
    }

    private static String parseData(Socket socket, String data) {
        boolean loop = true;
        while (data.length() > 11 && loop) {
            String id = data.substring(0,4);
            int length = data.getBytes(StandardCharsets.UTF_8)[11];
            if (data.length() >= length) {
                log.info("Request: {}", data.substring(0, length).replaceAll("\n", " "));
                String content = data.substring(12, length);
                SocketRequest socketRequest = new SocketRequest(id, length, content);
                // Send reply to parsed request !
                SocketReplies.reply(socket, socketRequest);
                data = data.substring(length);
            } else {
                loop = false;
            }
        }
        return data;
    }

    public static void writeToSocket(Socket socket, ByteArrayOutputStream buffer, DataOutputStream writer,
                                     String header, StringBuffer content) throws IOException {

        writer.write(header.getBytes(StandardCharsets.UTF_8)); // header
        writer.writeInt(0); // flags
        byte[] contentBytes = content.toString().getBytes(StandardCharsets.UTF_8);
        writer.writeInt(12 + contentBytes.length); // length (12 = header (4) + flags (4) + length (4))
        writer.write(contentBytes); // content

        byte[] bufferBytes = buffer.toByteArray();
        String bufferString = new String(bufferBytes, StandardCharsets.UTF_8);

        log.info("Reply: {}", bufferString.replaceAll("\n", " "));
        socket.getOutputStream().write(bufferBytes);
    }

}
