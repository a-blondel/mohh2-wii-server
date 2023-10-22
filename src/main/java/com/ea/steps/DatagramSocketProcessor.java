package com.ea.steps;

import com.ea.dto.DatagramSocketData;
import com.ea.nfsmw.client.services.NfsMwClientService;
import com.ea.services.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import static com.ea.utils.SocketUtils.*;

@Slf4j
@Component
public class DatagramSocketProcessor {

    public static final int RAW_PACKET_INIT = 1; // 0x02
    public static final int RAW_PACKET_CONN = 2; // 0x02
    public static final int RAW_PACKET_DISC = 3; // 0x03
    public static final int RAW_PACKET_POKE = 5; // 0x05
    public static final int GAME_PACKET_USER_UNRELIABLE = 7; // 0x07
    public static final int GAME_PACKET_USER_UNRELIABLE_AND_GAME_PACKET_SYNC = 71; // 0x07 + 0x40
    public static final int GAME_PACKET_SYNC = 64; // 0x40
    public static final int RAW_PACKET_DATA = 256; // 0x100
    public static final int RAW_PACKET_UNREL = 128; // 0x80

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private DatagramSocketWriter datagramSocketWriter;

    @Autowired
    private NfsMwClientService nfsMwClientService;

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

        if (RAW_PACKET_DISC == packetSeq) {
            lobbyService.endLobbyReport();
        } else if (RAW_PACKET_UNREL <= packetSeq && RAW_PACKET_DATA > packetSeq) {
            int packetOperation = new BigInteger(1, buf, inputPacket.getLength() - 1, 1).intValue();
            if (GAME_PACKET_USER_UNRELIABLE == packetOperation) {
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
            } else if (GAME_PACKET_USER_UNRELIABLE_AND_GAME_PACKET_SYNC == packetOperation)  {
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
            } else if (GAME_PACKET_SYNC == packetOperation)  {
                // 0000136b 0000136b 113a34cb 113a3563 0098 40

                // 0000136b = packetSeq
                // 0000136b = ack of previous RAW_PACKET_DATA
                // 113a34cb = last tick
                // 113a3563 = current tick
                // 0098 = game latency
                // 40 = packet type (GAME_PACKET_SYNC (40))
            }
        } else if (RAW_PACKET_DATA <= packetSeq) {
            // Nothing to do yet...
        }

        byte[] nfsPacket = nfsMwClientService.sendUdp(buf);

        // Send to the game
        socketData.setOutputMessage(nfsPacket);

        datagramSocketWriter.write(socket, socketData);

    }

}
