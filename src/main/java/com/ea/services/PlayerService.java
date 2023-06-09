package com.ea.services;

import com.ea.dto.SocketData;
import com.ea.steps.SocketWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PlayerService {

    @Autowired
    SocketWriter socketWriter;

    public void sendSele(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "MORE", "0" },
                { "SLOTS", "4" },
                { "STATS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

    public void sendAuth(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "player" },
                { "ADDR", socket.getInetAddress().getHostName() },
                { "PERSONAS", "player" },
                { "LOC", "frFR" },
                { "MAIL", "player@gmail.com" },
                { "SPAM", "NN" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

    public void sendPers(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "player" },
                { "LKEY", "" },
                { "EX-ticker", "" },
                { "LOC", "frFR" },
                { "A", socket.getInetAddress().getHostName() },
                { "LA", socket.getInetAddress().getHostName() },
                { "IDLE", "600000" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        sendWho(socket);
    }

    public void sendLlvl(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKILL_PTS", "211" },
                { "SKILL_LVL", "1049601" },
                { "SKILL", "" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        sendWho(socket);
    }

    public void sendWho(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "I", "71615" },
                { "N", "player" },
                { "F", "U" },
                { "P", "211" },
                { "S", "1,2,3,4,5,6,7,493E0,C350" }, // Stats
                { "X", "0" },
                { "G", "0" },
                { "AT", "" },
                { "CL", "511" },
                { "LV", "1049601" },
                { "MD", "0" },
                { "R", "1" }, // Rank
                { "US", "0" },
                { "HW", "0" },
                { "RP", "0" },
                { "LO", "frFR" }, // Country
                { "CI", "0" },
                { "CT", "0" },
                // 0x800225E0
                { "A", socket.getInetAddress().getHostName() },
                { "LA", socket.getInetAddress().getHostName() },
                // 0x80021384
                { "C", "4000,,7,1,1,,1,1,5553" },
                { "RI", "0" },
                { "RT", "0" },
                { "RG", "0" },
                { "RGC", "0" },
                // 0x80021468 if RI != ?? then read RM and RF
                { "RM", "0" },
                { "RF", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+who", null, content));
    }

}
