package com.ea.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Thread to handle a unique socket
 */
@Slf4j
public class SocketThread implements Runnable {

    Socket clientSocket;

    public void setClientSocket(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
        // Set timeout for reading from client socket
        // this.clientSocket.setSoTimeout(30000);
    }

    public void run() {

        log.info("Client session started: {} | {} | {}", clientSocket.hashCode(),
                clientSocket.getLocalSocketAddress().toString(),
                clientSocket.getRemoteSocketAddress().toString());

        try {
            exchangeWithSocket(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.info("Client session ended: " + clientSocket.hashCode());
        }
    }

    /**
     * Read and write on the socket
     * @param socket
     * @throws IOException
     */
    private static void exchangeWithSocket(Socket socket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("Received: " + line);
                StringBuffer content = new StringBuffer();
                String header = "";
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                     DataOutputStream writer = new DataOutputStream(buffer)) {
                    if (line.contains("addr")) {
                        header = "addr";
                        content.append("ADDR=" + socket.getLocalAddress().getHostAddress() + Const.NUL);
                    } else if (line.contains("skey")) {
                        header = "skey";
                        content.append("SKEY=$37940faf2a8d1381a3b7d0d2f570e6a7" + Const.LF);
                        content.append("PLATFORM=wii" + Const.NUL);
                    } else if (line.contains("news")) {
                        header = "news";
                        content.append("BUDDY_SERVER=" + socket.getLocalAddress().getHostAddress() + Const.LF);
                        content.append("BUDDY_PORT=21173" + Const.LF);
                        content.append("BUDDY_URL=http://wiimoh08.ea.com/" + Const.LF);
                        content.append("TOSAC_URL=http://wiimoh08.ea.com/TOSAC.txt" + Const.LF);
                        content.append("TOSA_URL=http://wiimoh08.ea.com/TOSA.txt" + Const.LF);
                        content.append("TOS_URL=http://wiimoh08.ea.com/TOS.txt" + Const.LF);
                        content.append("NEWS_TEXT=DEMO" + Const.LF);
                        content.append("TOS_TEXT=DEMO" + Const.LF);
                        content.append("NEWS_DATE=2008.6.11 21:00:00" + Const.LF);
                        content.append("NEWS_URL=http://wiimoh08.ea.com/news.txt" + Const.NUL);
                    }
                    if (header.length() > 0) {
                        SocketUtils.writeToSocket(socket, buffer, writer, header, content);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
