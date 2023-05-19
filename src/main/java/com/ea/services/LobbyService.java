package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;

import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LobbyService {

    public static void sendGsea(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "COUNT", "3" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        SocketWriter.write(socket, socketData);

        sendLobbyList(socket);
    }

    /** PARAMS
     * 1 = Mode (2 = CTF, 7 = TDM, 8 = DM)
     * 2 = Map (191 = Village, 65 = Port, 1f5 = monastery, c9 = City, 12d = sewers, 259 = base)
     * 3 = Friendly fire (1 = true, 2 = reverse fire, empty = false)
     * 4 = Equilibrate (1 = true, empty = false)
     * 5 = number of rounds
     * 6 = points limit
     * 7 = score limit
     * 8 = round time limit
     * 9 = max team kills
     * 10 = controls (empty = Elite, ? = Zapper, -1 = all)
     * 11 = SMG (1 = true, empty = false)
     * 12 = HMG
     * 13 = Rifle
     * 14 = Scoped Rifle
     * 15 = Shotgun
     * 16 = Bazooka
     * 17 = Grenades
     * 18 = Ranked - Must come with SYSFLAGS (ranked = 262656, unranked = 512) !
     * 19 = max players
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

    public static void sendGjoi(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);

        sendSes(socket);

        sendAgm(socket);
    }

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
                { "OPID0", "0" }, // OPID%d
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

        SocketWriter.write(socket, new SocketData("+agm", null, content));
    }

    public static void sendGspc(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);

        sendSes(socket);

        sendMgm(socket);
    }

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
                { "OPID0", "0" }, // OPID%d
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

        SocketWriter.write(socket, new SocketData("+mgm", null, content));
    }

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
                { "OPID0", "0" }, // OPID%d
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

        SocketWriter.write(socket, new SocketData("+ses", null, content));
    }

}
