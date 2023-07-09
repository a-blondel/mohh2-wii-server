package com.ea.steps;

import com.ea.dto.DatagramSocketData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

@Slf4j
@Component
public class DatagramSocketProcessor {

    @Autowired
    DatagramSocketWriter datagramSocketWriter;

    /**
     * Prepares the output message based on request type,
     * then calls the writer
     * @param socket the socket to give to the writer
     * @param socketData the object to process
     */
    public void process(DatagramSocket socket, DatagramSocketData socketData) {

        DatagramPacket inputPacket = socketData.getInputPacket();
        byte[] buf = Arrays.copyOf(inputPacket.getData(), inputPacket.getLength());
        int requestId = calcRequestId(inputPacket.getData());

        boolean updateOutput = true;
        byte[] responseId = new byte[4];

        switch (requestId) {
            case (5):
                responseId = intToByteArray(1);
                break;
            case (2):
                responseId = intToByteArray(6);
                break;
            case (1):
                responseId = intToByteArray(2);
                break;
            default:
                log.info("Unsupported operation: {}", requestId);
                updateOutput = false;
                break;
        }

        if(updateOutput) {
            buf[0] = responseId[0];
            buf[1] = responseId[1];
            buf[2] = responseId[2];
            buf[3] = responseId[3];
        }

        socketData.setOutputMessage(buf);

        datagramSocketWriter.write(socket, socketData);

    }

    /**
     * Calculate the message's id
     * @param data the message data
     * @return int - the id of the message
     */
    private int calcRequestId(byte[] data) {
        String size = "";
        for (int i = 0; i < 4; i++) {
            size += String.format("%02x", data[i]);
        }
        return Integer.parseInt(size, 16);
    }

    public final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

}
