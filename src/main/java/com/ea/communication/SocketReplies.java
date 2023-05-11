package com.ea.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class SocketReplies {

    public static void reply(Socket socket, SocketRequest socketRequest) {
        StringBuffer content = new StringBuffer();
        String header = "";
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             DataOutputStream writer = new DataOutputStream(buffer)) {
            switch (socketRequest.getId()) {
                case ("@tic"):
                    break;
                case ("@dir"):
                    header = "@dir";
                    content.append("ADDR=127.0.0.1" + Const.LF);
                    content.append("PORT=21172" + Const.LF);
                    content.append("SESS=1337420011" + Const.LF);
                    content.append("MASK=dbbcc81057aa718bbdafe887591112b4" + Const.NUL);
                    break;
                case ("addr"):
                    header = "addr";
                    content.append("ADDR=" + socket.getLocalAddress().getHostAddress() + Const.NUL);
                    break;
                case ("skey"):
                    header = "skey";
                    content.append("SKEY=$37940faf2a8d1381a3b7d0d2f570e6a7" + Const.NUL);
                    break;
                case ("news"):
                    header = "news";
                    content.append("Official servers are down" + Const.NUL);
                    break;
                default:
                    log.info("Unsupported operation: {}", socketRequest.getId());
                    break;
            }
            if (header.length() > 0) {
                SocketUtils.writeToSocket(socket, buffer, writer, header, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
