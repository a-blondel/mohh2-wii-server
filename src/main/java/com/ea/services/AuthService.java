package com.ea.services;

import com.ea.models.SocketData;
import com.ea.steps.SocketWriter;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ea.utils.HexDumpUtil.LF;
import static com.ea.utils.HexDumpUtil.NUL;
import static com.ea.utils.PropertiesLoader.getIntegerProperty;

public class AuthService {

    public static ScheduledExecutorService pingExecutor;

    public static void sendDir(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("ADDR=127.0.0.1" + LF)
                .append("PORT=" + getIntegerProperty("tcp.port") + LF)
                .append("SESS=1337420011" + LF)
                .append("MASK=dbbcc81057aa718bbdafe887591112b4" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendAddr(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("ADDR=" + socket.getLocalAddress().getHostAddress() + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);

        pingExecutor = Executors.newSingleThreadScheduledExecutor();
        pingExecutor.scheduleAtFixedRate(() -> startPing(socket), 30, 30, TimeUnit.SECONDS);
    }

    public static void startPing(Socket socket) {
        SocketData socketData = new SocketData("~png", null, null);
        SocketWriter.write(socket, socketData);
    }

    public static void sendSkey(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("SKEY=$37940faf2a8d1381a3b7d0d2f570e6a7" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);
    }

    public static void sendNews(Socket socket, SocketData socketData) {
        String content = new StringBuffer()
                .append("Official servers are down" + NUL).toString();

        socketData.setOutputMessage(content);
        SocketWriter.write(socket, socketData);
    }

}
