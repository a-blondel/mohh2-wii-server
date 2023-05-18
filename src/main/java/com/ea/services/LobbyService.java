package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;

import java.net.Socket;
import java.util.Arrays;

import static com.ea.utils.HexDumpUtil.LF;
import static com.ea.utils.HexDumpUtil.NUL;

public class LobbyService {

    public static void sendGsea(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("COUNT=3" + NUL).toString();

        socketData.setOutputMessage(content);
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
        String lobby1 = new StringBuffer()
                .append("IDENT=1" + LF)
                .append("NAME=\"Modded lobby\"" + LF)
                .append("PARAMS=2,191,,,,,,,,-1,1,1,1,1,1,1,1,1,20" + LF)
                .append("SYSFLAGS=262656" + LF)
                .append("COUNT=31" + LF)
                .append("MAXSIZE=33" + NUL).toString();
                //.append("PASS=" + LF);

        String lobby2 = new StringBuffer()
                .append("IDENT=2" + LF)
                .append("NAME=\"Glitch\"" + LF)
                .append("PARAMS=7,65,,,a,,32,,,-1,1,1,1,1,1,1,1,,5" + LF)
                .append("SYSFLAGS=512" + LF)
                .append("COUNT=2" + LF)
                .append("MAXSIZE=6" + NUL).toString();

        String lobby3 = new StringBuffer()
                .append("IDENT=3" + LF)
                .append("NAME=\"Join :)\"" + LF)
                .append("PARAMS=8,1f5,,,5,,14,,,-1,1,1,1,1,1,1,1,1,10" + LF)
                .append("SYSFLAGS=262656" + LF)
                .append("COUNT=9" + LF)
                .append("MAXSIZE=17" + NUL).toString();

        for (String lobby : Arrays.asList(lobby1, lobby2, lobby3)) {
            SocketData socketData = new SocketData("+gam", null, lobby);
            SocketWriter.write(socket, socketData);
        }

    }

    public static void sendGjoi(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);

        sendAgm(socket);

        sendSes(socket);
    }

    public static void sendAgm(Socket socket) {
        String content = new StringBuffer()
                .append("IDENT=1" + LF)
                .append("NAME=abcd" + LF)
                .append("HOST=player" + LF)
                .append("GPSHOST=player" + LF)
                .append("PARAMS=8,12d,,,-1,,,1e,,-1,1,1,1,1,1,1,1,1,20,,,15f90,122d0022" + LF)
                .append("ROOM=efgh" + LF)
                .append("CUSTFLAGS=0" + LF)
                .append("SYSFLAGS=262656" + LF)
                .append("COUNT=1" + LF)
                .append("PRIV=0" + LF)
                .append("MINSIZE=0" + LF)
                .append("MAXSIZE=33" + LF)
                .append("NUMPART=1" + LF)
                .append("SEED=randomseed" + LF)
                .append("WHEN=2009.2.8-9:44:15" + LF)
                .append("GAMEPORT=21173" + LF)
                .append("VOIPPORT=21173" + LF)
                .append("OPID0=0" + LF)
                .append("OPPO0=player" + LF)
                .append("ADDR0=127.0.0.1" + LF)
                .append("PARTSIZE0=17" + LF)
                .append("PARTPARAMS0=0" + NUL).toString();

        SocketWriter.write(socket, new SocketData("+agm", null, content));
    }

    public static void sendGspc(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);

        sendMgm(socket);

        sendSes(socket);
    }

    public static void sendMgm(Socket socket) {
        String content = new StringBuffer()
                .append("IDENT=1" + LF)
                .append("NAME=abcd" + LF)
                .append("HOST=player" + LF)
                .append("GPSHOST=player" + LF)
                .append("PARAMS=8,12d,,,-1,,,1e,,-1,1,1,1,1,1,1,1,1,20,,,15f90,122d0022" + LF)
                .append("ROOM=efgh" + LF)
                .append("CUSTFLAGS=0" + LF)
                .append("SYSFLAGS=262656" + LF)
                .append("COUNT=1" + LF)
                .append("PRIV=0" + LF)
                .append("MINSIZE=0" + LF)
                .append("MAXSIZE=33" + LF)
                .append("NUMPART=1" + LF)
                .append("SEED=randomseed" + LF)
                .append("WHEN=2009.2.8-9:44:15" + LF)
                .append("GAMEPORT=21173" + LF)
                .append("VOIPPORT=21173" + LF)
                .append("OPID0=0" + LF)
                .append("OPPO0=player" + LF)
                .append("ADDR0=127.0.0.1" + LF)
                .append("PARTSIZE0=17" + LF)
                .append("PARTPARAMS0=0" + NUL).toString();

        SocketWriter.write(socket, new SocketData("+mgm", null, content));
    }

    public static void sendSes(Socket socket) {
        String content = new StringBuffer()
                .append("IDENT=1" + LF)
                .append("NAME=abcd" + LF)
                .append("HOST=player" + LF)
                .append("GPSHOST=player" + LF)
                .append("PARAMS=8,12d,,,-1,,,1e,,-1,1,1,1,1,1,1,1,1,20,,,15f90,122d0022" + LF)
                .append("ROOM=efgh" + LF)
                .append("CUSTFLAGS=0" + LF)
                .append("SYSFLAGS=262656" + LF)
                .append("COUNT=1" + LF)
                .append("PRIV=0" + LF)
                .append("MINSIZE=0" + LF)
                .append("MAXSIZE=33" + LF)
                .append("NUMPART=1" + LF)
                .append("SEED=randomseed" + LF)
                .append("WHEN=2009.2.8-9:44:15" + LF)
                .append("GAMEPORT=21173" + LF)
                .append("VOIPPORT=21173" + LF)
                .append("OPID0=0" + LF)
                .append("OPPO0=player" + LF)
                .append("ADDR0=127.0.0.1" + LF)
                .append("PARTSIZE0=17" + LF)
                .append("PARTPARAMS0=0" + NUL).toString();

        SocketWriter.write(socket, new SocketData("+ses", null, content));
    }

}
