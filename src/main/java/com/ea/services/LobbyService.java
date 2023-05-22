package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;

import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LobbyService {

    /**
     * Lobby count
     * @param socket
     * @param socketData
     */
    public static void sendGsea(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "COUNT", "3" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);

        sendLobbyList(socket);
    }

    /**
     * List lobbies
     * @param socket
     */
    public static void sendLobbyList(Socket socket) {
        Map<String, String> lobby1 = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "\"Modded lobby\"" },
                { "PARAMS", "2,191,,,,,,,,-1,1,1,1,1,1,1,1,1,20" },
                { "SYSFLAGS", "262656" },
                { "COUNT", "31" },
                { "MAXSIZE", "33" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        Map<String, String> lobby2 = Stream.of(new String[][] {
                { "IDENT", "2" },
                { "NAME", "\"Glitch\"" },
                { "PARAMS", "7,65,,,a,,32,,,-1,1,1,1,1,1,1,1,,5" },
                { "SYSFLAGS", "512" },
                { "COUNT", "2" },
                { "MAXSIZE", "6" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        Map<String, String> lobby3 = Stream.of(new String[][] {
                { "IDENT", "3" },
                { "NAME", "\"Bazooka only\"" },
                { "PARAMS", "8,1f5,,,5,,14,,,-1,1,1,1,1,1,1,1,1,10" },
                { "SYSFLAGS", "262656" },
                { "COUNT", "9" },
                { "MAXSIZE", "17" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        for (Map<String, String> lobby : Arrays.asList(lobby1, lobby2, lobby3)) {
            SocketData socketData = new SocketData("+gam", null, lobby);
            SocketWriter.write(socket, socketData);
        }
    }

    /**
     * Join lobby
     * @param socket
     * @param socketData
     */
    public static void sendGjoi(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "abcd" },
                { "HOST", "player" },
                { "GPSHOST", "player" },
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
                { "OPPO0", "player" }, // OPPO%d
                { "ADDR0", "127.0.0.1" }, // ADDR%d
                { "LADDR0", "127.0.0.1" }, // LADDR%d
                { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                // { "OPPART0", "0" }, // OPPART%d
                // { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                // { "OPFLAGS0", "0" }, // OPFLAGS%d
                // { "PRES0", "0" }, // PRES%d ???

                // another loop 0x8002225C only if NUMPART>=0
                { "PARTSIZE0", "17" }, // PARTSIZE%d
                { "PARTPARAMS0", "0" }, // PARTPARAMS%d

                // { "SESS", "0" }, %s-%s-%08x 0--498ea96f
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);

        sendSes(socket);

    }

    /**
     * Unused yet
     * @param socket
     */
    public static void sendAgm(Socket socket) {
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
                { "ADDR0", "127.0.0.1" }, // ADDR%d
                { "LADDR0", "127.0.0.1" }, // LADDR%d
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

        SocketWriter.write(socket, new SocketData("+agm", null, content));
    }

    /**
     * Create lobby
     * @param socket
     * @param socketData
     */
    public static void sendGspc(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);

        sendSes(socket);
    }

    /**
     * Unused yet
     * @param socket
     */
    public static void sendMgm(Socket socket) {
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
                { "ADDR0", "127.0.0.1" }, // ADDR%d
                { "LADDR0", "127.0.0.1" }, // LADDR%d
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

        SocketWriter.write(socket, new SocketData("+mgm", null, content));
    }

    /**
     * Lobby info based on IDENT
     * @param socket
     */
    public static void sendSes(Socket socket) {
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
                { "ADDR0", "127.0.0.1" }, // ADDR%d
                { "LADDR0", "127.0.0.1" }, // LADDR%d
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

        SocketWriter.write(socket, new SocketData("+ses", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public static void sendGget(Socket socket) {
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
                { "ADDR0", "127.0.0.1" }, // ADDR%d
                { "LADDR0", "127.0.0.1" }, // LADDR%d
                { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                { "OPPART0", "0" }, // OPPART%d
                { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                { "OPFLAGS0", "0" }, // OPFLAGS%d
                { "PRES0", "0" }, // PRES%d

                { "OPID1", "2" }, // OPID%d // must be > 0
                { "OPPO1", "player2" }, // OPPO%d
                { "ADDR1", "127.0.0.1" }, // ADDR%d
                { "LADDR1", "127.0.0.1" }, // LADDR%d
                { "MADDR1", "$0017ab8f4451" }, // MADDR%d
                { "OPPART1", "0" }, // OPPART%d
                { "OPPARAM1", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                { "OPFLAGS1", "0" }, // OPFLAGS%d
                { "PRES1", "0" }, // PRES%d
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        SocketWriter.write(socket, new SocketData("gget", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public static void sendUsr(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "I", "1" },
                { "N", "player" },
                { "M", "player" },
                { "F", "H" },
                { "A", "127.0.0.1" },
                { "P", "211" },
                { "S", "0" },
                { "X", "" },
                { "G", "0" },
                { "T", "2" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        SocketWriter.write(socket, new SocketData("+usr", null, content));
    }

    /**
     * Unused yet
     * @param socket
     */
    public static void sendMove(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "IDENT", "1" },
                { "NAME", "1" },
                { "COUNT", "1" },
                { "FLAGS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        SocketWriter.write(socket, new SocketData("move", null, content));
    }

    /**
     * Unused yet
     * @param socket
     * @param <T>
     */
    public static <T> void sendRom(Socket socket) {
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

        SocketWriter.write(socket, new SocketData("+rom", null, content));
    }

    /**
     * Unused yet
     * @param socket
     * @param <T>
     */
    public static <T> void sendPop(Socket socket) {
        Map<String, String> content = Stream.of(new String[][] {
                { "Z", "1/1" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        SocketWriter.write(socket, new SocketData("+pop", null, content));
    }

}
