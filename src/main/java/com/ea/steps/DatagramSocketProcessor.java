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


        socketData.setOutputMessage(buf);

        int packetSeq = new BigInteger(1, buf, 0, 4).intValue();

        if (RAW_PACKET_DISC == packetSeq) {
            lobbyService.endLobbyReport();
        } else if (RAW_PACKET_UNREL <= packetSeq && RAW_PACKET_DATA > packetSeq) {

            byte[] nfsPacket = nfsMwClientService.sendUdp(buf);
            socketData.setOutputMessage(nfsPacket);

            /*int packetOperation = new BigInteger(1, buf, inputPacket.getLength() - 1, 1).intValue();
            if (GAME_PACKET_USER_UNRELIABLE == packetOperation) {
            } else if (GAME_PACKET_USER_UNRELIABLE_AND_GAME_PACKET_SYNC == packetOperation)  {
            } else if (GAME_PACKET_SYNC == packetOperation)  {
            }*/
        } else if (RAW_PACKET_DATA <= packetSeq) {
            // Nothing to do yet...
        }

        datagramSocketWriter.write(socket, socketData);

    }

}
