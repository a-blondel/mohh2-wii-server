package com.ea.nfsmw.client.steps;

import com.ea.dto.SocketData;
import com.ea.nfsmw.client.config.NfsMwClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class NfsMwSocketHandler {

    @Autowired
    private NfsMwClientConfig nfsMwClientConfig;

    private String gameName = "NAME";

    public void read(Socket tcpSocket) throws IOException {
        InputStream is = tcpSocket.getInputStream();
        byte[] buffer = new byte[1024];
        int readLength;
        while((readLength = is.read(buffer)) != -1) {
            parse(buffer, readLength);
        }
    }

    private void parse(byte[] buffer, int readLength) {
        boolean loop = true;
        int readRemaining = Integer.valueOf(readLength);
        int lastPos = 0;

        while (readRemaining > 11 && loop) {
            int currentMessageBegin = readLength - readRemaining;
            int currentMessageLength = getlength(buffer, lastPos);
            if (readRemaining >= currentMessageLength) {
                String id = new String(buffer, currentMessageBegin, 4);
                String content = new String(buffer, currentMessageBegin + 12, currentMessageLength);
                SocketData socketData = new SocketData(id, content, null);

                /*if (props.isTcpDebugEnabled() && !props.getTcpDebugExclusions().contains(socketData.getIdMessage())) {
                    log.info("Receive:\n{}", HexDumpUtils.formatHexDump(buffer, currentMessageBegin, currentMessageLength));
                }*/

                process(socketData);
                readRemaining -= currentMessageLength;
                lastPos += currentMessageLength;
            } else {
                //log.info("Cannot parse data");
                loop = false;
            }
        }
    }

    private void process(SocketData socketData) {
        switch (socketData.getIdMessage()) {
            case ("~png"):
                nfsMwClientConfig.write(socketData);
                break;
            case ("+uss"):
                uss(socketData);
                break;
            case ("+ses"), ("+mgm"):
                ses();
                break;
            default:
                break;
        }
    }

    private void uss(SocketData socketData) {
        gameName = getValueFromSocket(socketData.getInputMessage(), "N");
    }

    private void ses() {
        sele3();

        nfsMwClientConfig.setStartSes(1);
    }

    private void sele3() {
        Map<String, String> content = Stream.of(new String[][] {
                { "GAMES", "0" },
                { "MYGAME", "0" },
                { "ROOMS", "0" },
                { "USERS", "0" },
                { "USERSETS", "0" },
                { "MESGS", "1" },
                { "MESGTYPES", "GPY" },
                { "ASYNC", "0" },
                { "STATS", "0" },
                { "SLOTS", "0" },
                { "INGAME", "1" },
                { "USERSET0", "" },
                { "USERSET1", "" },
                { "USERSET2", "" },
                { "USERSET3", "" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("sele", null, content));
    }

    private int getlength(byte[] buffer, int lastPos) {
        String size = "";
        for (int i = lastPos + 8; i < lastPos + 12; i++) {
            size += String.format("%02x", buffer[i]);
        }
        return Integer.parseInt(size, 16);
    }

    public String getValueFromSocket(String data, String key) {
        String result = null;
        String[] entries = data.split("\\t");
        for (String entry : entries) {
            String[] parts = entry.trim().split("=");
            if(key.equals(parts[0])) {
                if (parts.length > 1) {
                    result = parts[1];
                }
                break;
            }
        }
        return result;
    }

    public String getGameName() {
        return this.gameName;
    }

}
