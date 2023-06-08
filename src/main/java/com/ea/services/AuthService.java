package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;
import com.ea.utils.Props;

import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthService {

    public static void sendDir(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                // { "DIRECT", "0" }, // 0x8001FC04
                // if DIRECT == 0 then read ADDR and PORT
                { "ADDR", socket.getLocalAddress().getHostName() }, // 0x8001FC18
                { "PORT", Props.getString("tcp.port") }, // 0x8001fc30
                // { "SESS", "0" }, // 0x8001fc48 %s-%s-%08x 0--498ea96f
                // { "MASK", "0" }, // 0x8001fc60
                // if ADDR == 0 then read DOWN
                // { "DOWN", "0" }, // 0x8001FC90
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendAddr(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);
    }

    public static void sendSkey(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKEY", "$51ba8aee64ddfacae5baefa6bf61e009" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendNews(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "BUDDY_SERVER", socket.getLocalAddress().getHostName() },
                { "BUDDY_PORT", Props.getString("tcp.port") },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);
    }

}
