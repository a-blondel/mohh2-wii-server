package com.ea.steps;

import com.ea.dto.SocketData;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.util.stream.Collectors.joining;

@Slf4j
@Component
public class SocketWriter {

    @Autowired
    private Props props;

    /**
     * Builds the full output message based on the data id and content
     * Then sends it through the socket
     * @param socket the socket to write into
     * @param socketData the object to use to write the message
     * @throws IOException
     */
    public void write(Socket socket, SocketData socketData) {

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             DataOutputStream writer = new DataOutputStream(buffer)) {

            writer.write(socketData.getIdMessage().getBytes(StandardCharsets.UTF_8));
            if(socketData.getIdMessage().length() == 4) {
                writer.writeInt(0);
            }
            int outputLength = 12;

            if (null != socketData.getOutputData()) {
                byte[] contentBytes = (socketData.getOutputData().entrySet()
                        .stream()
                        .map(param -> param.getKey() + "=" + param.getValue())
                        .collect(joining("\n")) + "\0").getBytes(StandardCharsets.UTF_8);

                outputLength += contentBytes.length;
                writer.writeInt(outputLength);
                writer.write(contentBytes);
            } else {
                writer.writeInt(outputLength);
            }

            byte[] bufferBytes = buffer.toByteArray();

            if (props.isTcpDebugEnabled() && !props.getTcpDebugExclusions().contains(socketData.getIdMessage())) {
                log.info("Send:\n{}", HexDumpUtils.formatHexDump(bufferBytes, 0, outputLength));
            }

            socket.getOutputStream().write(bufferBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
