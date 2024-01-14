package com.ea.services;

import com.ea.dto.SocketData;
import com.ea.steps.SocketWriter;
import com.ea.utils.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class StatsService {

    @Autowired
    private SocketWriter socketWriter;

    @Autowired
    private SocketUtils socketUtils;

    /**
     * Retrieve ranking categories
     * @param socket
     * @param socketData
     */
    public void cate(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "CC", "3" }, // <total # of categories in this view>
                { "IC", "3" }, // <total # of indices in this view>
                { "VC", "3" }, // <total # of variations in this view>
                { "U", "3" },
                { "SYMS", "3" },
                { "SS", "3" },
                { "R", String.join(",", Collections.nCopies(33, "1")) }, // <comma-separated-list of category,index,view data>
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

    /**
     * Request ranking snapshot
     * @param socket
     */
    public void snap(Socket socket, SocketData socketData) {

        String chan = socketUtils.getValueFromSocket(socketData.getInputMessage(), "CHAN");
        String seqn = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SEQN");
        String cols = socketUtils.getValueFromSocket(socketData.getInputMessage(), "COLS"); // send column information (0/1)
        String start = socketUtils.getValueFromSocket(socketData.getInputMessage(), "START"); // index
        String CI = socketUtils.getValueFromSocket(socketData.getInputMessage(), "CI");

        // CI : 0 = My EA Leaderboard, 1 = EA Top 100, 2 = EA Weapon Leaders

        Map<String, String> content = Stream.of(new String[][] {
                { "CHAN", chan }, // <matching request value>
                { "START", start }, // <actual start used>
                { "RANGE", "1" }, // <actual range used>
                { "SEQN", seqn }, // <value provided in request>
                { "CC", "1" }, // <number of columns>
                { "FC", "1" }, // <number of fixed columns>
                { "DESC", "Leader" }, // <list-description>
                { "PARAMS", "1,1,1,1,1,1,1,1,1,1,1,1" }, // <comma-separated list of integer parameters>
                { "CN0", "NAME" }, // <column-name>
                { "CD0", "NAME" }, // <column-name>
                { "CP0", "1" }, // <column-parameter>
                { "CW0", "50" }, // <column-width>
                { "CT0", "1" }, // <column-type>
                { "CS0", "1" }, // <column-style>
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        snp(socket);
    }

    /**
     * Send ranking snapshot
     * @param socket
     */
    public void snp(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "N", "1" }, // <persona name>
                { "R", "1" }, // <rank>
                { "P", "1" }, // <points>
                { "O", "1" }, // <online>
                { "S", "1,2,1,2,1,2,1,2,1" }, // <stats>
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        SocketData socketData = new SocketData("+snp", null, content);
        socketWriter.write(socket, socketData);
    }

}
