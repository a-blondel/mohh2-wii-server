package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;

import java.net.Socket;

import static com.ea.utils.HexDumpUtil.LF;
import static com.ea.utils.HexDumpUtil.NUL;

public class PlayerService {

    public static void sendSele(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("MORE=1" + LF)
                .append("STATS=0" + LF)
                .append("SLOTS=10" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendAuth(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("NAME=Name" + LF)
                .append("ADDR=" + socket.getLocalAddress().getHostAddress() + LF)
                .append("PERSONAS=Player" + LF)
                .append("LOC=frFR" + LF)
                .append("MAIL=user@domain.com" + LF)
                .append("SPAM=NN" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendPers(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("PERS=User" + LF)
                .append("LKEY=3fcf27540c92935b0a66fd3b0000283c" + LF)
                .append("LOC=frFR" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);

        sendWho(socket, new SocketData("+who", null, null));
    }

    public static void sendWho(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("M=User" + LF)
                .append("N=User" + LF)
                .append("MA=$7a790554222c" + LF)
                .append("A=" + socket.getLocalAddress().getHostAddress() + LF)
                .append("LA=User" + LF)
                .append("P=1" + LF)
                .append("AT=" + LF)
                .append("C=4000,,7,1,1,,1,1,5553" + LF)
                .append("CL=511" + LF)
                .append("F=U" + LF)
                .append("G=0" + LF)
                .append("HW=0" + LF)
                .append("I=71615" + LF)
                .append("LO=frFR" + LF)
                .append("LV=1049601" + LF)
                .append("MD=0" + LF)
                .append("PRES=1" + LF)
                .append("RP=0" + LF)
                .append("S=1,2,3,4,5,6,7,493E0,C350" + LF)
                .append("US=0" + LF)
                .append("VER=5" + LF)
                .append("WT=0" + LF)
                .append("WI=0" + LF)
                .append("R=0" + LF)
                .append("C=0" + LF)
                .append("RI=0" + LF)
                .append("I=1024" + LF)
                .append("RT=0" + LF)
                .append("LA=" + socket.getLocalAddress().getHostAddress() + LF)
                .append("MA=" + LF)
                .append("X=0" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);
    }

}
