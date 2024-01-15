package com.ea.services;

import com.ea.dto.SocketData;
import com.ea.steps.SocketWriter;
import com.ea.utils.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
        String cols = socketUtils.getValueFromSocket(socketData.getInputMessage(), "COLS"); // send column information or not
        String start = socketUtils.getValueFromSocket(socketData.getInputMessage(), "START"); // <start ranking> (index)
        /**
         * <category-index>
         * Values : 0 = My EA Leaderboard, 1 = EA Top 100, 2 = EA Weapon Leaders
         */
        String categoryIndex = socketUtils.getValueFromSocket(socketData.getInputMessage(), "CI");

        String columnNumber = "18";
        if ("2".equals(categoryIndex)) {
            columnNumber = "30";
        }

        Map<String, String> content = Stream.of(new String[][] {
                { "CHAN", chan }, // <matching request value>
                { "START", start }, // <actual start used>
                { "RANGE", "1" }, // <actual range used>
                { "SEQN", seqn }, // <value provided in request>
                { "CC", columnNumber }, // <number of columns>
                { "FC", "1" }, // <number of fixed columns>
                { "DESC", "" }, // <list-description>
                { "PARAMS", "1,1,1,1,1,1,1,1,1,1,1,1" }, // <comma-separated list of integer parameters>
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        if ("1".equals(cols) && Set.of("0","1").contains(categoryIndex)) {
            content.putAll(Stream.of(new String[][] {
                    { "CN0", "RNK" }, // <column-name>
                    { "CD0", "\"Leaderboard Ranking\"" }, // <column-name> (selected)
                    //{ "CP0", "1" }, // <column-parameter>
                    //{ "CW0", "50" }, // <column-width>
                    //{ "CT0", "1" }, // <column-type>
                    //{ "CS0", "1" }, // <column-style>
                    { "CN1", "Persona" },
                    { "CD1", "Persona" },
                    { "CN2", "Score" },
                    { "CD2", "Score" },
                    { "CN3", "Kills" },
                    { "CD3", "\"Total Kills\"" },
                    { "CN4", "Deaths" },
                    { "CD4", "\"Total Deaths\"" },
                    { "CN5", "Accuracy" }, // '%' is appended by the game
                    { "CD5", "\"Accuracy %\"" },
                    { "CN6", "Time" }, // Must be in seconds, 'H' is appended by the game, capped at 999H on game side
                    { "CD6", "\"Total Time Played Online\"" },
                    { "CN7", "KPM" },
                    { "CD7", "\"Kills Per Minute\"" },
                    { "CN8", "DPM" },
                    { "CD8", "\"Deaths Per Minute\"" },
                    { "CN9", "Headshots" },
                    { "CD9", "\"Total Headshots\"" },
                    /**
                     * Looks like the map is duplicated each time a game mode is playable on it
                     * As far as I know, it doesn't matter in that context
                     * Values : 0 = Port, 2 = City, 4 = Sewers, 7 = Village, 10 = Monastery, 12 = Base
                    */
                    { "CN10", "\"Fav. Map\"" },
                    { "CD10", "\"Most Played Map\"" },
                    { "CN11", "\"Fav. Mode\"" }, // 0 = DM, 1 = TDM, 2 = INF
                    { "CD11", "\"Most Played Mode\"" },
                    { "CN12", "\"Fav. Team\"" }, // 0 = Axis, 1 = Allied
                    { "CD12", "\"Most Played Team\"" },
                    { "CN13", "Wins" },
                    { "CD13", "\"Total Wins\"" },
                    { "CN14", "Losses" },
                    { "CD14", "\"Total Losses\"" },
                    { "CN15", "\"DM RND\"" },
                    { "CD15", "\"Deathmatch Rounds Played\"" },
                    { "CN16", "\"INF RND\"" },
                    { "CD16", "\"Infiltration Rounds Played\"" },
                    { "CN17", "\"TDM RND\"" },
                    { "CD17", "\"Team Deathmatch Rounds Played\"" },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
        } else if ("1".equals(cols) && "2".equals(categoryIndex)) {
            content.putAll(Stream.of(new String[][] {
                    { "CN0", "RNK" },
                    { "CD0", "\"Leaderboard Ranking\"" },
                    { "CN1", "Persona" },
                    { "CD1", "Persona" },
                    { "CN2", "Kills" },
                    { "CD2", "\"Total Kills\"" },
                    { "CN3", "Accuracy" },
                    { "CD3", "\"Accuracy %\"" },
                    { "CN4", "\".45 Kill\"" },
                    { "CD4", "\"M1911 Pistol Kills\"" },
                    { "CN5", "\".45 Acc\"" },
                    { "CD5", "\"M1911 Pistol Accuracy\"" },
                    { "CN6", "\"THMP Kill\"" },
                    { "CD6", "\"Thompson Kills\"" },
                    { "CN7", "\"THMP Acc\"" },
                    { "CD7", "\"Thompson Accuracy\"" },
                    { "CN8", "\"BAR Kill\"" },
                    { "CD8", "\"M1918 BAR Kills\"" },
                    { "CN9", "\"BAR Acc\"" },
                    { "CD9", "\"M1918 BAR Accuracy\"" },
                    { "CN10", "\"GAR Kill\"" },
                    { "CD10", "\"M1 Garand Kills\"" },
                    { "CN11", "\"GAR Acc\"" },
                    { "CD11", "\"M1 Garand Accuracy\"" },
                    { "CN12", "\"SPFD Kill\"" },
                    { "CD12", "\"Springfield Kills\"" },
                    { "CN13", "\"SPFD Acc\"" },
                    { "CD13", "\"Springfield Accuracy\"" },
                    { "CN14", "\"SHOT Kill\"" },
                    { "CD14", "\"M12 Shotgun Kills\"" },
                    { "CN15", "\"SHOT Acc\"" },
                    { "CD15", "\"M12 Shotgun Accuracy\"" },
                    { "CN16", "\"BAZ Kill\"" },
                    { "CD16", "\"M1A1 Bazooka Kills\"" },
                    { "CN17", "\"BAZ Acc\"" },
                    { "CD17", "\"M1A1 Bazooka Accuracy\"" },
                    { "CN18", "\"P08 Kill\"" },
                    { "CD18", "\"P08 Luger Kills\"" },
                    { "CN19", "\"P08 Acc\"" },
                    { "CD19", "\"P08 Luger Accuracy\"" },
                    { "CN20", "\"MP40 Kill\"" },
                    { "CD20", "\"MP40 Kills\"" },
                    { "CN21", "\"MP40 Acc\"" },
                    { "CD21", "\"MP40 Accuracy\"" },
                    { "CN22", "\"StG44 Kill\"" },
                    { "CD22", "\"StG44 AR Kills\"" },
                    { "CN23", "\"StG44 Acc\"" },
                    { "CD23", "\"StG44 AR Accuracy\"" },
                    { "CN24", "\"KAR Kill\"" },
                    { "CD24", "\"Karabiner 98K Kills\"" },
                    { "CN25", "\"KAR Acc\"" },
                    { "CD25", "\"Karabiner 98K Accuracy\"" },
                    { "CN26", "\"GEWR Kill\"" },
                    { "CD26", "\"Gewehr Kills\"" },
                    { "CN27", "\"GEWR Acc\"" },
                    { "CD27", "\"Gewehr Accuracy\"" },
                    { "CN28", "\"GRND Kill\"" },
                    { "CD28", "\"Grenade Kills\"" },
                    { "CN29", "\"Melee Kill\"" },
                    { "CD29", "\"Melee Kills\"" },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
        }

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        snp(socket, categoryIndex);
    }

    /**
     * Send ranking snapshot
     * @param socket
     */
    public void snp(Socket socket, String categoryIndex) {

        // Get stats based on category index in DB
        // 0 : My Leaderboard
        // 1 : Top 100
        // 2 : Weapon Leaderboard (most kills)

        Map<String, String> content = Stream.of(new String[][] {
                { "N", "toto" }, // <persona name>
                { "R", "0" }, // <rank>
                { "P", "0" }, // <points>
                { "O", "0" }, // <online>
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));


        if (Set.of("0","1").contains(categoryIndex)) {
            content.putAll(Stream.of(new String[][]{
                    // <stats>
                    {"S", "1,\"Player\",150000,200000,50000,25.12,10000000,1.225,0.250,175000,12,1,1,900,333,300,300,300"},
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
        } else if ("2".equals(categoryIndex)) {
            content.putAll(Stream.of(new String[][]{
                    // <stats>
                    {"S", "1,\"Player\",200000,25.12,1,1.0,2,2.0,3,3.0,4,4.0,5,5.0,6,6.0,7,7.0,8,8.0,9,9.0,10,10.0,11,11.0,12,12.0,13,14"},
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
        }

        SocketData socketData = new SocketData("+snp", null, content);
        socketWriter.write(socket, socketData);
    }

}
