package com.ea.steps;

import com.ea.dto.DatagramSocketData;
import com.ea.services.LobbyService;
import com.ea.utils.SocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

@Slf4j
@Component
public class DatagramSocketProcessor {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private DatagramSocketWriter datagramSocketWriter;

    @Autowired
    private SocketUtils socketUtils;

    /**
     * Prepares the output message based on request type,
     * then calls the writer
     * @param socket the socket to give to the writer
     * @param socketData the object to process
     */
    public void process(DatagramSocket socket, DatagramSocketData socketData) {

        DatagramPacket inputPacket = socketData.getInputPacket();
        byte[] buf = Arrays.copyOf(inputPacket.getData(), inputPacket.getLength());

        int packetSeq = new BigInteger(1, buf, 0, 4).intValue();

        if (5 == packetSeq) { // RAW_PACKET_POKE
            int returnSeq = 2; // RAW_PACKET_CONN (not sure, but it works)
            System.arraycopy(socketUtils.intToByteArray(returnSeq), 0, buf, 0, 4);
        } else if (3 == packetSeq) { // RAW_PACKET_DISC
            lobbyService.endLobbyReport();
        } else if (128 <= packetSeq && 256 > packetSeq) { // RAW_PACKET_UNREL
            int packetOperation = new BigInteger(1, buf, inputPacket.getLength() - 1, 1).intValue();
            if (7 == packetOperation) { // GAME_PACKET_USER_UNRELIABLE
                // 0000009c 00000109 00000000 21421344 1d000000 1c000000 fcff1f00 e0ff1f00 e0ff1f00 e0ff1f04 00 07

                // 0000009c = packetSeq
                // 00000109 = ack of RAW_PACKET_DATA
                // 00000000
                // 21421344 = checksum of 1d000000 1c000000 fcff1f00 e0ff1f00 e0ff1f00 e0ff1f04 00
                // 1d000000 = increment from 1 in little endian ?
                // 1c000000 = ack of increment -1 in little endian ?
                // fcff1f 00 = ?
                // e0ff1f 00 = almost identical to the previous one ?
                // e0ff1f 00 = identical to the previous one ?
                // e0ff1f 0400 = identical to the previous one + ?
                // 07 = packet type (GAME_PACKET_USER_UNRELIABLE)
            } else if (71 == packetOperation)  { // GAME_PACKET_USER_UNRELIABLE (7) + GAME_PACKET_SYNC (64)
                // 0000009d 00000109 00000000 8b5fc3e7 1e000000 1d000000 fcff3f00 c0ff3f00 c0ff3f00 c0ff3f04 00
                // 41c4b77f 41c4b7bc 0054 47

                // 0000009d = packetSeq
                // 00000109 = ack of previous RAW_PACKET_DATA
                // 00000000
                // 8b5fc3e7 = checksum of 1e000000 1d000000 fcff3f 00 c0ff3f00 c0ff3f00 c0ff3f04 00
                // 1e000000 = increment from 1 in little endian ?
                // 1d000000 = ack of increment -1 in little endian ?
                // fcff3f 00 = ?
                // c0ff3f 00 = almost identical to the previous one ?
                // c0ff3f 00 = identical to the previous one
                // c0ff3f 0400 = identical to the previous one  + ?

                // 41c4b77f = last tick
                // 41c4b7bc = current tick
                // 0054 = game latency
                // 47 = packet type (GAME_PACKET_USER_UNRELIABLE (7) + GAME_PACKET_SYNC (40))
            } else if (64 == packetOperation)  { // GAME_PACKET_SYNC (64)
                // 0000136b 0000136b 113a34cb 113a3563 0098 40

                // 0000136b = packetSeq
                // 0000136b = ack of previous RAW_PACKET_DATA
                // 113a34cb = last tick
                // 113a3563 = current tick
                // 0098 = game latency
                // 40 = packet type (GAME_PACKET_SYNC (40))
            }
        } else if (256 <= packetSeq) { // RAW_PACKET_DATA
            // Nothing to do yet...
        }

        socketData.setOutputMessage(buf);

        datagramSocketWriter.write(socket, socketData);

    }

}
