package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.PersonaStatsEntity;
import com.ea.enums.MapEnum;
import com.ea.enums.RankingCategory;
import com.ea.repositories.PersonaStatsRepository;
import com.ea.steps.SocketWriter;
import com.ea.utils.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class StatsService {

    @Autowired
    private SocketWriter socketWriter;

    @Autowired
    private SocketUtils socketUtils;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private PersonaStatsRepository personaStatsRepository;

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
        String categoryIndex = socketUtils.getValueFromSocket(socketData.getInputMessage(), "CI"); // <category-index>

        String columnNumber = "18";
        if (RankingCategory.EA_WEAPON_LEADERS.value.equals(categoryIndex)) {
            columnNumber = "30";
        }

        // Must be fetched here to know the actual size
        List<PersonaStatsEntity> personaStatsEntityList = new ArrayList<>();
        long offset = 0;

        if(RankingCategory.MY_EA_LEADERBOARD.value.equals(categoryIndex)) {
            personaStatsEntityList = personaStatsRepository.getLeaderboard(100, offset);
        } else if (RankingCategory.EA_TOP_100.value.equals(categoryIndex)) {
            offset = personaStatsRepository.getRankByPersonaId(sessionData.getCurrentPersonna().getId());
            offset = Math.max(offset - 50, 0);
            personaStatsEntityList = personaStatsRepository.getLeaderboard(100, offset);
        } else if (RankingCategory.EA_WEAPON_LEADERS.value.equals(categoryIndex)) {
            personaStatsEntityList = personaStatsRepository.getWeaponLeaderboard(100, offset);
        }

        Map<String, String> content = Stream.of(new String[][] {
                { "CHAN", chan }, // <matching request value>
                { "START", start }, // <actual start used>
                { "RANGE", String.valueOf(personaStatsEntityList.size()) }, // <actual range used>
                { "SEQN", seqn }, // <value provided in request>
                { "CC", columnNumber }, // <number of columns>
                { "FC", "1" }, // <number of fixed columns>
                { "DESC", "" }, // <list-description>
                { "PARAMS", "1,1,1,1,1,1,1,1,1,1,1,1" }, // <comma-separated list of integer parameters>
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        if ("1".equals(cols) && Set.of(RankingCategory.MY_EA_LEADERBOARD.value, RankingCategory.EA_TOP_100.value)
                .contains(categoryIndex)) {
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
        } else if ("1".equals(cols) && RankingCategory.EA_WEAPON_LEADERS.value.equals(categoryIndex)) {
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

        snp(socket, categoryIndex, personaStatsEntityList, offset);
    }

    /**
     * Send ranking snapshot
     * @param socket
     */
    public void snp(Socket socket, String categoryIndex, List<PersonaStatsEntity> personaStatsEntityList, long offset) {
        List<Map<String, String>> rankingList = new ArrayList<>();
        for(PersonaStatsEntity personaStatsEntity : personaStatsEntityList) {
            String name = personaStatsEntity.getPersona().getPers();
            String rank = String.valueOf(++offset);
            String points = String.valueOf(personaStatsEntity.getTotalKills() - personaStatsEntity.getTotalDeaths());
            if (Set.of(RankingCategory.MY_EA_LEADERBOARD.value, RankingCategory.EA_TOP_100.value).contains(categoryIndex)) {
                long totalTime = personaStatsEntity.getTimeAsAllied() + personaStatsEntity.getTimeAsAxis();
                String mostPlayedTeam = personaStatsEntity.getTimeAsAxis() > personaStatsEntity.getTimeAsAllied() ? "0" : "1";
                rankingList.add(Stream.of(new String[][] {
                        { "N", name }, // <persona name>
                        { "R", rank }, // <rank>
                        { "P", points }, // <points>
                        { "O", "0" }, // <online> ?
                        { "S", String.join(",", // <stats>
                                String.valueOf(offset),
                                personaStatsEntity.getPersona().getPers(),
                                String.valueOf(personaStatsEntity.getTotalKills() - personaStatsEntity.getTotalDeaths()),
                                String.valueOf(personaStatsEntity.getTotalKills()),
                                String.valueOf(personaStatsEntity.getTotalDeaths()),
                                getPrecision(personaStatsEntity.getTotalMissedShots(), personaStatsEntity.getTotalShots()),
                                String.valueOf(totalTime),
                                totalTime == 0 ? "0" : String.valueOf(new Formatter(Locale.US).format("%.3f", personaStatsEntity.getTotalKills() / (totalTime / 60f))),
                                totalTime == 0 ? "0" : String.valueOf(new Formatter(Locale.US).format("%.3f", personaStatsEntity.getTotalDeaths() / (totalTime / 60f))),
                                String.valueOf(personaStatsEntity.getTotalHeadshots()),
                                getMostPlayedMap(personaStatsEntity),
                                getMostPlayedMode(personaStatsEntity),
                                mostPlayedTeam,
                                String.valueOf(personaStatsEntity.getDmWins() + personaStatsEntity.getInfWins() + personaStatsEntity.getTdmWins()),
                                String.valueOf(personaStatsEntity.getDmLosses() + personaStatsEntity.getInfLosses() + personaStatsEntity.getTdmLosses()),
                                String.valueOf(personaStatsEntity.getDmWins() + personaStatsEntity.getDmLosses()),
                                String.valueOf(personaStatsEntity.getInfWins() + personaStatsEntity.getInfLosses()),
                                String.valueOf(personaStatsEntity.getTdmWins() + personaStatsEntity.getTdmLosses())
                              )
                        },
                }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
            } else if (RankingCategory.EA_WEAPON_LEADERS.value.equals(categoryIndex)) {
                rankingList.add(Stream.of(new String[][] {
                        { "N", name },
                        { "R", rank },
                        { "P", points },
                        { "O", "0" },
                        { "S", String.join(",",
                                String.valueOf(offset),
                                personaStatsEntity.getPersona().getPers(),
                                String.valueOf(personaStatsEntity.getTotalKills()),
                                getPrecision(personaStatsEntity.getTotalMissedShots(), personaStatsEntity.getTotalShots()),
                                String.valueOf(personaStatsEntity.getColtKills()),
                                getPrecision(personaStatsEntity.getColtMissedShots(), personaStatsEntity.getColtShots()),
                                String.valueOf(personaStatsEntity.getThompsonKills()),
                                getPrecision(personaStatsEntity.getThompsonMissedShots(), personaStatsEntity.getThompsonShots()),
                                String.valueOf(personaStatsEntity.getBarKills()),
                                getPrecision(personaStatsEntity.getBarMissedShots(), personaStatsEntity.getBarShots()),
                                String.valueOf(personaStatsEntity.getGarandKills()),
                                getPrecision(personaStatsEntity.getGarandMissedShots(), personaStatsEntity.getGarandShots()),
                                String.valueOf(personaStatsEntity.getSpringfieldKills()),
                                getPrecision(personaStatsEntity.getSpringfieldMissedShots(), personaStatsEntity.getSpringfieldShots()),
                                String.valueOf(personaStatsEntity.getShotgunKills()),
                                getPrecision(personaStatsEntity.getShotgunMissedShots(), personaStatsEntity.getShotgunShots()),
                                String.valueOf(personaStatsEntity.getBazookaKills()),
                                getPrecision(personaStatsEntity.getBazookaMissedShots(), personaStatsEntity.getBazookaShots()),
                                String.valueOf(personaStatsEntity.getLugerKills()),
                                getPrecision(personaStatsEntity.getLugerMissedShots(), personaStatsEntity.getLugerShots()),
                                String.valueOf(personaStatsEntity.getMpKills()),
                                getPrecision(personaStatsEntity.getMpMissedShots(), personaStatsEntity.getMpShots()),
                                String.valueOf(personaStatsEntity.getStgKills()),
                                getPrecision(personaStatsEntity.getStgMissedShots(), personaStatsEntity.getStgShots()),
                                String.valueOf(personaStatsEntity.getKarabinerKills()),
                                getPrecision(personaStatsEntity.getKarabinerMissedShots(), personaStatsEntity.getKarabinerShots()),
                                String.valueOf(personaStatsEntity.getGewehrKills()),
                                getPrecision(personaStatsEntity.getGewehrMissedShots(), personaStatsEntity.getGewehrShots()),
                                String.valueOf(personaStatsEntity.getGrenadeKills()),
                                String.valueOf(personaStatsEntity.getMeleeKills())
                              )
                        },
                }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
            }
        }

        for (Map<String, String> ranking : rankingList) {
            SocketData socketData = new SocketData("+snp", null, ranking);
            socketWriter.write(socket, socketData);
        }
    }

    private String getPrecision(long missedShots, long totalShots) {
        String precision = "100";
        if(0 != totalShots) {
            precision = String.valueOf(new Formatter(Locale.US).format("%.2f", (1 - (missedShots / (float) totalShots)) * 100));
        }
        return precision;
    }

    private String getMostPlayedMode(PersonaStatsEntity personaStatsEntity) {
        String mostPlayedMode;
        if(personaStatsEntity.getTimeInDm() > personaStatsEntity.getTimeInTdm()) {
            mostPlayedMode = personaStatsEntity.getTimeInDm() > personaStatsEntity.getTimeInInf() ? "0" : "2";
        } else {
            mostPlayedMode = personaStatsEntity.getTimeInTdm() > personaStatsEntity.getTimeInInf() ? "1" : "2";
        }
        return mostPlayedMode;
    }

    private String getMostPlayedMap(PersonaStatsEntity personaStatsEntity) {
        String mostPlayedMap = MapEnum.PORT.value;
        long maxTimeInMap = personaStatsEntity.getTimeInPort();
        if(maxTimeInMap < personaStatsEntity.getTimeInCity()) {
            maxTimeInMap = personaStatsEntity.getTimeInCity();
            mostPlayedMap = MapEnum.CITY.value;
        }
        if(maxTimeInMap < personaStatsEntity.getTimeInSewers()) {
            maxTimeInMap = personaStatsEntity.getTimeInSewers();
            mostPlayedMap = MapEnum.SEWERS.value;
        }
        if(maxTimeInMap < personaStatsEntity.getTimeInVillage()) {
            maxTimeInMap = personaStatsEntity.getTimeInVillage();
            mostPlayedMap = MapEnum.VILLAGE.value;
        }
        if(maxTimeInMap < personaStatsEntity.getTimeInMonastery()) {
            maxTimeInMap = personaStatsEntity.getTimeInMonastery();
            mostPlayedMap = MapEnum.MONASTERY.value;
        }
        if(maxTimeInMap < personaStatsEntity.getTimeInBase()) {
            mostPlayedMap = MapEnum.BASE.value;
        }
        return mostPlayedMap;
    }

}
