package com.ea.services;

import com.ea.dto.SocketData;
import com.ea.steps.SocketWriter;
import com.ea.utils.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AuthService {

    @Autowired
    SocketWriter socketWriter;

    @Autowired
    Props props;

    public void dir(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                // { "DIRECT", "0" }, // 0x8001FC04
                // if DIRECT == 0 then read ADDR and PORT
                { "ADDR", socket.getLocalAddress().getHostName() }, // 0x8001FC18
                { "PORT", String.valueOf(props.getTcpPort()) }, // 0x8001fc30
                // { "SESS", "0" }, // 0x8001fc48 %s-%s-%08x 0--498ea96f
                // { "MASK", "0" }, // 0x8001fc60
                // if ADDR == 0 then read DOWN
                // { "DOWN", "0" }, // 0x8001FC90
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

    public void addr(Socket socket, SocketData socketData) {
        socketWriter.write(socket, socketData);
    }

    public void skey(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKEY", "$51ba8aee64ddfacae5baefa6bf61e009" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

    public void news(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "BUDDY_SERVER", socket.getLocalAddress().getHostName() },
                { "BUDDY_PORT", String.valueOf(props.getTcpPort()) },
                { "TOSAC_URL", "https://tos.ea.com/legalapp/webterms/us/fr/pc/" },
                { "TOSA_URL", "https://tos.ea.com/legalapp/webterms/us/fr/pc/" },
                { "TOS_URL", "https://tos.ea.com/legalapp/webterms/us/fr/pc/" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

    public void sele(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "MORE", "0" },
                { "SLOTS", "4" },
                { "STATS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

}
