package com.ea.nfsmw.client.services;

import com.ea.dto.SocketData;
import com.ea.nfsmw.client.config.NfsMwClientConfig;
import com.ea.nfsmw.client.steps.NfsMwSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ea.steps.DatagramSocketProcessor.*;
import static com.ea.utils.SocketUtils.*;

/**
 * Need For Speed Most Wanted Client Service
 */
@Slf4j
@Service
public class NfsMwClientService {

    @Autowired
    private NfsMwClientConfig nfsMwClientConfig;

    @Autowired
    private NfsMwSocketHandler nfsMwSocketHandler;

    private boolean nfsInitialized = false;

    private byte[] mohSessionId = HexFormat.of().parseHex("ed5c78dd");
    private byte[] nfsSessionId = HexFormat.of().parseHex("26c48d86");


    public void mockClient() {

        if(!nfsInitialized) {
            try {
                init();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            nfsInitialized = true;
        }

        final byte[][] response = {nfsMwClientConfig.sendUdp(HexFormat.of().parseHex("0100000026c48d86"))};

        try {
            Thread.sleep(250);

            response[0] = nfsMwClientConfig.sendUdp(HexFormat.of().parseHex("00010000ff000000003a4be3003a4be30000000040"));

            Thread.sleep(250);

            response[0] = nfsMwClientConfig.sendUdp(HexFormat.of().parseHex("00010000ff000000003a4be3003a4be30000000040"));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final String[] packetSeq = {formatHexString(response[0]).substring(3, 4)};
        final int[] startSes = {0};

        while("1".equals(packetSeq[0]) && startSes[0] != 5) {
            response[0] = nfsMwClientConfig.sendUdp(response[0]);
            packetSeq[0] = formatHexString(response[0]).substring(3,4);
            startSes[0] = nfsMwClientConfig.getStartSes();
        }

        new Thread(() -> {
            while(true) {
                if(!"0".equals(packetSeq[0])) {
                    response[0] = nfsMwClientConfig.sendUdp(response[0]);
                }
                packetSeq[0] = formatHexString(response[0]).substring(3,4);
            }
        }).start();

    }


    public void init() throws InterruptedException {
        addr();
        Thread.sleep(100);
        skey();
        Thread.sleep(100);
        news();
        Thread.sleep(100);
        auth();
        Thread.sleep(100);
        pers();
        Thread.sleep(100);
        sele1();
        Thread.sleep(100);
        usea1();
        Thread.sleep(100);
        sele2();
        Thread.sleep(100);
        ujoi();
        Thread.sleep(100);
        gjoi();
        Thread.sleep(100);
        auxi1();
        Thread.sleep(100);
        usea2();
        Thread.sleep(100);
        auxi2();
        Thread.sleep(100);
        onln();
        Thread.sleep(100);
        mesg();
        Thread.sleep(100);
        auxi2();
        Thread.sleep(100);
        auxi2();
        Thread.sleep(100);
        auxi2();
    }

    public byte[] sendUdp(byte[] buf) {

        if(!nfsInitialized) {
            try {
                init();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            nfsInitialized = true;
        }

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

        int nfsGamePacket = new BigInteger(1, bigEndian, bigEndian.length - 1, 1).intValue();

        if (6 == nfsGamePacket) {
            System.arraycopy(parseHexString("07"), 0, bigEndian, bigEndian.length - 1, 1);
        } else if (70 == nfsGamePacket) {
            System.arraycopy(parseHexString("47"), 0, bigEndian, bigEndian.length - 1, 1);
        }

        return bigEndian;
    }

    public void addr() {
        Map<String, String> content = Stream.of(new String[][] {
                { "ADDR", "192.168.1.90" },
                { "PORT", String.valueOf(nfsMwClientConfig.getTcpSocket().getLocalPort()) },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("addr", null, content));
    }

    public void skey() {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKEY", "$5075626c6963204b6579" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("skey", null, content));
    }

    public void news() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "7" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("news", null, content));
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
                { "NAME", "CLIENT" },
                { "REGKEY", "" },
                { "MAC", "$309c23d1c342" },
                { "PASS", "~$7p9%25%22TQ-1>)[d$PXk<AAcxdP]qWzd" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("auth", null, content));
    }

    public void pers() {
        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "CLIENT" },
                { "MAC", "$309c23d1c342" },
                { "CDEV", "" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("pers", null, content));
    }

    public void sele1() {
        Map<String, String> content = Stream.of(new String[][] {
                { "MYGAME", "1" },
                { "STATS", "5000" },
                { "ASYNC", "1" },
                { "MESGS", "1" },
                { "ROOMS", "1" },
                { "USERSETS", "1" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("sele", null, content));
    }

    public void usea1() {
        Map<String, String> content = Stream.of(new String[][] {
                { "START", "0" },
                { "COUNT", "100" },
                { "CUSTFLAGS", "-2147474432" },
                { "CUSTMASK", "-2081397247" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("usea", null, content));
    }

    public void sele2() {
        /*String hexStr ="352e486f7374506c61796572";
        byte[] bytes = HexFormat.of().parseHex(hexStr);
        String str = new String(bytes, StandardCharsets.UTF_8);*/

        Map<String, String> content = Stream.of(new String[][] {
                { "USERSET0", nfsMwSocketHandler.getGameName() },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("sele", null, content));
    }

    public void ujoi() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", nfsMwSocketHandler.getGameName() }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("ujoi", null, content));
    }

    public void gjoi() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "NAME" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("gjoi", null, content));
    }

    public void auxi1() {
        String hexStr = "53434625336430253061562533643230253061";
        byte[] bytes = HexFormat.of().parseHex(hexStr);
        String str = new String(bytes, StandardCharsets.UTF_8);

        Map<String, String> content = Stream.of(new String[][] {
                { "TEXT", str },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("auxi", null, content));
    }

    public void usea2() {
       /* String hexStr ="352e486f7374506c61796572";
        byte[] bytes = HexFormat.of().parseHex(hexStr);
        String str = new String(bytes, StandardCharsets.UTF_8);*/

        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", nfsMwSocketHandler.getGameName() },
                { "START", "0" },
                { "COUNT", "1" },
                { "CUSTFLAGS", "0" },
                { "CUSTMASK", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("usea", null, content));
    }

    public void auxi2() {
        String hexStr = "53434625336430253061434e2533642d3837313835383537372530615054253364302e383532392530615048253364312e302530615041253364302e38323033253061562533643230253061";
        byte[] bytes = HexFormat.of().parseHex(hexStr);
        String str = new String(bytes, StandardCharsets.UTF_8);

        Map<String, String> content = Stream.of(new String[][] {
                { "TEXT", str },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("auxi", null, content));
    }

    public void onln() {
        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "NAME" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("onln", null, content));
    }

    public void mesg() {
        Map<String, String> content = Stream.of(new String[][] {
                { "TEXT", "42" },
                { "ATTR", "EGS" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("mesg", null, content));
    }

}
