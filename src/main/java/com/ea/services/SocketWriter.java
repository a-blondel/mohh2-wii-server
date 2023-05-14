package com.ea.services;

import com.ea.models.SocketData;
import com.ea.utils.HexDumpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketWriter {

    /**
     * Builds the full output message based on the data id and content
     * Then sends it through the socket
     * @param socket the socket to write into
     * @param socketData the object to use to write the message
     * @throws IOException
     */
    public static void write(Socket socket, SocketData socketData) {

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             DataOutputStream writer = new DataOutputStream(buffer)) {

            writer.write(socketData.getIdMessage().getBytes(StandardCharsets.UTF_8));
            writer.writeInt(0);
            int outputLength = 12;

            if (null != socketData.getOutputMessage()) {
                byte[] contentBytes = socketData.getOutputMessage().getBytes(StandardCharsets.UTF_8);
                outputLength += contentBytes.length;
                writer.writeInt(outputLength);
                writer.write(contentBytes);
            } else {
                writer.writeInt(outputLength);
            }

            byte[] bufferBytes = buffer.toByteArray();

            if (!HexDumpUtil.NO_DUMP_MSG.contains(socketData.getIdMessage())) {
                log.info("Send:\n{}", HexDumpUtil.formatHexDump(bufferBytes, 0, outputLength));
            }

            socket.getOutputStream().write(bufferBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
