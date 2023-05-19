package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;

import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerService {

    public static void sendSele(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "MORE", "0" },
                { "SLOTS", "4" },
                { "STATS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendAuth(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "player" },
                { "ADDR", "127.0.0.1" },
                { "PERSONAS", "player" },
                { "LOC", "frFR" },
                { "MAIL", "player@gmail.com" },
                { "SPAM", "NN" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendPers(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "player" },
                { "LKEY", "" },
                { "EX-ticker", "" },
                { "LOC", "frFR" },
                { "A", "127.0.0.1" },
                { "LA", "127.0.0.1" },
                { "IDLE", "600000" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);

        sendWho(socket);
    }

    public static void sendLlvl(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKILL_PTS", "211" },
                { "SKILL_LVL", "1049601" },
                { "SKILL", "" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);

        sendWho(socket);
    }

    public static void sendWho(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "I", "71615" },
                { "N", "player" },
                { "F", "U" },
                { "P", "211" },
                { "S", "1,2,3,4,5,6,7,493E0,C350" },
                { "X", "0" },
                { "G", "0" },
                { "AT", "" },
                { "CL", "511" },
                { "LV", "1049601" },
                { "MD", "0" },
                { "R", "0" },
                { "US", "0" },
                { "HW", "0" },
                { "RP", "0" },
                { "LO", "frFR" },
                { "CI", "0" },
                { "CT", "0" },
                // 0x800225E0
                { "A", "127.0.0.1" },
                { "LA", "127.0.0.1" },
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

        SocketWriter.write(socket, new SocketData("+who", null, content));
    }

}
