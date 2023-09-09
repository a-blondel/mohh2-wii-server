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
public class StatsService {

    @Autowired
    private SocketWriter socketWriter;

    /**
     * Retrieve ranking categories
     * @param socket
     * @param socketData
     */
    public void cate(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "CC", "3" },
                { "IC", "3" },
                { "VC", "3" },
                { "R", "MYRANKING,EATOP100,EATOP100WEAPONS" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

}
