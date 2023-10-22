package com.ea.nfsmw.client.services;

import com.ea.dto.SocketData;
import com.ea.nfsmw.client.config.NfsMwClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ea.utils.SocketUtils.parseHexString;
import static com.ea.utils.SocketUtils.reverseByteArray;

/**
 * Need For Speed Most Wanted Client Service
 */
@Slf4j
@Service
public class NfsMwClientService {

    @Autowired
    private NfsMwClientConfig nfsMwClientConfig;

    private boolean nfsInitialized = false;


    public void init() {
        auth();
        pers();
        ujoi();
        gjoi();
    }

    public byte[] sendUdp(byte[] buf) {

        if(!nfsInitialized) {
            init();
            nfsInitialized = true;
        }

        /*int packetSeq = new BigInteger(1, buf, 0, 4).intValue();

        // if nfs needs an init and not a conn (connect mode)
        if(RAW_PACKET_CONN == packetSeq) {
            System.arraycopy(parseHexString(formatIntToWord(RAW_PACKET_INIT)), 0, buf, 0, 4);
        }*/

        int gamePacket = new BigInteger(1, buf, buf.length - 1, 1).intValue();

        if (7 == gamePacket) {
            System.arraycopy(parseHexString("06"), 0, buf, buf.length - 1, 1);
        } else if (71 == gamePacket) {
            System.arraycopy(parseHexString("46"), 0, buf, buf.length - 1, 1);
        }

        // Convert UDP Packet to little endian
        byte[] littleEndian = reverseByteArray(buf);

        // Forward UDP packet to NFSMW client
        byte[] nfsResult = nfsMwClientConfig.sendUdp(littleEndian);

        // Convert NFSMW UDP Packet to big endian
        byte[] bigEndian = reverseByteArray(nfsResult);

        return bigEndian;
    }

    public void auth() {
        Map<String, String> content = Stream.of(new String[][] {
                { "REGN", "NA" },
                { "CLST", "194010" },
                { "NETV", "20" },
                { "FROM", "US" },
                { "LANG", "EN" },
                { "MID", "$309c23d1c342" },
                { "PROD", "nfs-pc-2006" },
                { "VERS", "\"pc/1.3-Nov 21 2005\"" },
                { "SLUS", "SLUS_21351" },
                { "SKU", "14705" },
                { "SDKVERS", "3.9.3.0" },
                { "BUILDDATE", "\"Oct 19 2005\"" },
                { "NAME", "ClientPlayer" },
                { "REGKEY", "" },
                { "MAC", "$309c23d1c342" },
                { "PASS", "~$7p9%25%22TQ-1>)[d$PXk<AAcxdP]qWzd" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("auth", null, content));
    }

    public void pers() {
        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "ClientPlayer" },
                { "MAC", "$309c23d1c342" },
                { "CDEV", "" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("pers", null, content));
    }

    public void ujoi() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "HostPlayer" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("ujoi", null, content));
    }

    public void gjoi() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "HostPlayer" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("gjoi", null, content));
    }

}
