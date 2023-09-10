package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.LobbyEntity;
import com.ea.repositories.LobbyRepository;
import com.ea.steps.SocketWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LobbyService {

    @Autowired
    private SocketWriter socketWriter;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private LobbyRepository lobbyRepository;

    /**
     * Lobby count
     * @param socket
     * @param socketData
     */
    public void gsea(Socket socket, SocketData socketData) {

        List<LobbyEntity> lobbyEntities = lobbyRepository.findByEndTime(null);

        Map<String, String> content = Stream.of(new String[][] {
                { "COUNT", String.valueOf(lobbyEntities.size()) },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        gam(socket, lobbyEntities);
    }

    /**
     * List lobbies
     * @param socket
     */
    public void gam(Socket socket, List<LobbyEntity> lobbyEntities) {
        List<Map<String, String>> lobbies = new ArrayList<>();

        for(LobbyEntity lobbyEntity : lobbyEntities) {
            lobbies.add(Stream.of(new String[][] {
                    { "IDENT", String.valueOf(lobbyEntity.getId()) },
                    { "NAME", lobbyEntity.getName() },
                    { "PARAMS", lobbyEntity.getParams() },
                    { "SYSFLAGS", lobbyEntity.getSysflags() },
                    { "COUNT", String.valueOf(lobbyEntity.getLobbyPersonas().stream().filter(lp -> lp.isInLobby()).count() + 1) },
                    { "MAXSIZE", String.valueOf(new BigInteger(lobbyEntity.getParams().substring(lobbyEntity.getParams().lastIndexOf(',') + 1), 16).add(BigInteger.ONE)) },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
        }

        for (Map<String, String> lobby : lobbies) {
            SocketData socketData = new SocketData("+gam", null, lobby);
            socketWriter.write(socket, socketData);
        }
    }

    /**
     * Join lobby
     * @param socket
     * @param socketData
     */
    public void gjoi(Socket socket, SocketData socketData) {
        socketWriter.write(socket, socketData);

        ses(socket);

    }

    /**
     * Unused yet
     * @param socket
     */
    public void agm(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "abcd" },
                { "HOST", "player" },
                { "GPSHOST", "player" },
                { "PARAMS", "8,12d,,,-1,,,1e,,-1,1,1,1,1,1,1,1,1,20,,,15f90,122d0022" },
                // { "PLATPARAMS", "0" },  // ???
                { "ROOM", "0" },
                { "CUSTFLAGS", "0" },
                { "SYSFLAGS", "262656" },
                { "COUNT", "1" },
                { "PRIV", "0" },
                { "MINSIZE", "0" },
                { "MAXSIZE", "33" },
                { "NUMPART", "1" },
                { "SEED", "012345" }, // random seed
                { "WHEN", "2009.2.8-9:44:15" },
                { "GAMEPORT", "21173" },
                { "VOIPPORT", "21173" },
                // { "GAMEMODE", "0" }, // ???
                // { "AUTH", "0" }, // ???

                // loop 0x80022058 only if COUNT>=0
                { "OPID0", "1" }, // OPID%d
                { "OPPO0", "player" }, // OPPO%d
                { "ADDR0", socket.getInetAddress().getHostName() }, // ADDR%d
                { "LADDR0", socket.getInetAddress().getHostName() }, // LADDR%d
                { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                // { "OPPART0", "0" }, // OPPART%d
                // { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                // { "OPFLAGS0", "0" }, // OPFLAGS%d
                // { "PRES0", "0" }, // PRES%d ???

                // another loop 0x8002225C only if NUMPART>=0
                { "PARTSIZE0", "17" }, // PARTSIZE%d
                { "PARTPARAMS0", "0" }, // PARTPARAMS%d
                { "SELF", "player" }, // PARTPARAMS%d

                // { "SESS", "0" }, %s-%s-%08x 0--498ea96f
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+agm", null, content));
    }

    /**
     * Create lobby
     * @param socket
     * @param socketData
     */
    public void gpsc(Socket socket, SocketData socketData) {
        socketWriter.write(socket, socketData);

        ses(socket);
    }

    /**
     * Unused yet
     * @param socket
     */
    public void mgm(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "abcd" },
                { "HOST", "player" },
                { "GPSHOST", "player" },
                { "PARAMS", "8,12d,,,-1,,,1e,,-1,1,1,1,1,1,1,1,1,20,,,15f90,122d0022" },
                // { "PLATPARAMS", "0" },  // ???
                { "ROOM", "0" },
                { "CUSTFLAGS", "0" },
                { "SYSFLAGS", "262656" },
                { "COUNT", "1" },
                { "PRIV", "0" },
                { "MINSIZE", "0" },
                { "MAXSIZE", "33" },
                { "NUMPART", "1" },
                { "SEED", "012345" }, // random seed
                { "WHEN", "2009.2.8-9:44:15" },
                { "GAMEPORT", "21173" },
                { "VOIPPORT", "21173" },
                // { "GAMEMODE", "0" }, // ???
                // { "AUTH", "0" }, // ???

                // loop 0x80022058 only if COUNT>=0
                { "OPID0", "1" }, // OPID%d
                { "OPPO0", "player" }, // OPPO%d
                { "ADDR0", socket.getInetAddress().getHostName() }, // ADDR%d
                { "LADDR0", socket.getInetAddress().getHostName() }, // LADDR%d
                { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                // { "OPPART0", "0" }, // OPPART%d
                // { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                // { "OPFLAGS0", "0" }, // OPFLAGS%d
                // { "PRES0", "0" }, // PRES%d ???

                // another loop 0x8002225C only if NUMPART>=0
                { "PARTSIZE0", "17" }, // PARTSIZE%d
                { "PARTPARAMS0", "0" }, // PARTPARAMS%d
                { "SELF", "player" }, // PARTPARAMS%d

                // { "SESS", "0" }, %s-%s-%08x 0--498ea96f
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+mgm", null, content));
    }

    /**
     * Lobby info based on IDENT
     * @param socket
     */
    public void ses(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "abcd" },
                { "HOST", sessionData.getCurrentPersonna().getPers() },
                { "GPSHOST", sessionData.getCurrentPersonna().getPers() },
                { "PARAMS", "2,191,,,-1,,,1e,,-1,1,1,1,1,1,1,1,1,20,,,15f90,122d0022" },
                // { "PLATPARAMS", "0" },  // ???
                { "ROOM", "0" },
                { "CUSTFLAGS", "0" },
                { "SYSFLAGS", "262656" },
                { "COUNT", "1" },
                { "PRIV", "0" },
                { "MINSIZE", "0" },
                { "MAXSIZE", "33" },
                { "NUMPART", "1" },
                { "SEED", "012345" }, // random seed
                { "WHEN", "2009.2.8-9:44:15" },
                { "GAMEPORT", "21173" },
                { "VOIPPORT", "21173" },
                // { "GAMEMODE", "0" }, // ???
                // { "AUTH", "0" }, // ???

                // loop 0x80022058 only if COUNT>=0
                { "OPID0", "1" }, // OPID%d
                { "OPPO0", sessionData.getCurrentPersonna().getPers() }, // OPPO%d
                { "ADDR0", socket.getInetAddress().getHostName() }, // ADDR%d
                { "LADDR0", socket.getInetAddress().getHostName() }, // LADDR%d
                { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                // { "OPPART0", "0" }, // OPPART%d
                // { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                // { "OPFLAGS0", "0" }, // OPFLAGS%d
                // { "PRES0", "0" }, // PRES%d ???

                // another loop 0x8002225C only if NUMPART>=0
                { "PARTSIZE0", "17" }, // PARTSIZE%d
                { "PARTPARAMS0", "0" }, // PARTPARAMS%d
                { "SELF", sessionData.getCurrentPersonna().getPers() }, // PARTPARAMS%d

                // { "SESS", "0" }, %s-%s-%08x 0--498ea96f
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+ses", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public void gget(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "WHEN", "2003.12.8 15:52:54" },
                { "NAME", "GameName" },
                { "HOST", "IDK" },
                { "PARAMS", "2,191,,,,,,,,-1,1,1,1,1,1,1,1,1,20" },
                { "ROOM", "1" },
                { "MAXSIZE", "33" },
                { "MINSIZE", "2" },
                { "COUNT", "3" },
                { "USERFLAGS", "0" },
                { "SYSFLAGS", "262656" },

                { "OPID0", "1" }, // OPID%d // must be > 0
                { "OPPO0", "player1" }, // OPPO%d
                { "ADDR0", socket.getInetAddress().getHostName() }, // ADDR%d
                { "LADDR0", socket.getInetAddress().getHostName() }, // LADDR%d
                { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                { "OPPART0", "0" }, // OPPART%d
                { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                { "OPFLAGS0", "0" }, // OPFLAGS%d
                { "PRES0", "0" }, // PRES%d

                { "OPID1", "2" }, // OPID%d // must be > 0
                { "OPPO1", "player2" }, // OPPO%d
                { "ADDR1", socket.getInetAddress().getHostName() }, // ADDR%d
                { "LADDR1", socket.getInetAddress().getHostName() }, // LADDR%d
                { "MADDR1", "$0017ab8f4451" }, // MADDR%d
                { "OPPART1", "0" }, // OPPART%d
                { "OPPARAM1", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                { "OPFLAGS1", "0" }, // OPFLAGS%d
                { "PRES1", "0" }, // PRES%d
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("gget", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public void usr(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "I", "1" },
                { "N", "player" },
                { "M", "player" },
                { "F", "H" },
                { "A", socket.getInetAddress().getHostName() },
                { "P", "211" },
                { "S", "0" },
                { "X", "" },
                { "G", "0" },
                { "T", "2" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+usr", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public void move(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "1" },
                { "COUNT", "1" },
                { "FLAGS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("move", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public <T> void rom(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "I", "1" },
                { "N", "player" },
                { "H", "player" },
                { "F", "CK" },
                { "T", "1" },
                { "L", "50" },
                { "P", "0" },
                { "IDENT", "1" }, // new
                { "NAME", "player" }, //
                { "COUNT", "1" }, //
                { "LIDENT", "0" }, //
                { "LCOUNT", "0" }, //
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+rom", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public <T> void pop(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "Z", "1/1" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketWriter.write(socket, new SocketData("+pop", null, content));
    }

}
