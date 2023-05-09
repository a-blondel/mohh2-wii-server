package com.ea.communication;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String header = "";
                String flags = "";
                StringBuffer content = new StringBuffer();
                log.info("Received: " + line);
                if (line.contains("addr")) {
                    header = "addr";
                    flags = "\0\0\0\0";
                    content.append("ADDR=" + socket.getLocalAddress().getHostAddress());
                } else if (line.contains("skey")) {
                    header = "skey";
                    flags = "\0\0\0\0";
                    content.append("SKEY=$51ba8aee64ddfacae5baefa6bf61e009" + Const.LF);
                    content.append("PLATFORM=wii");
                } else if (line.contains("news")) {
                    header = "news";
                    flags = "\0\0\0\0";
                    content.append("BUDDY_SERVER=" + socket.getLocalAddress().getHostAddress() + Const.LF);
                    content.append("BUDDY_PORT=21172" + Const.LF);
                    content.append("BUDDY_URL=http://wiimoh08.ea.com/" + Const.LF);
                    content.append("TOSAC_URL=http://wiimoh08.ea.com/TOSAC.txt" + Const.LF);
                    content.append("TOSA_URL=http://wiimoh08.ea.com/TOSA.txt" + Const.LF);
                    content.append("TOS_URL=http://wiimoh08.ea.com/TOS.txt" + Const.LF);
                    content.append("NEWS_TEXT=DEMO" + Const.LF);
                    content.append("TOS_TEXT=DEMO" + Const.LF);
                    content.append("NEWS_DATE=2008.6.11 21:00:00" + Const.LF);
                    content.append("NEWS_URL=http://wiimoh08.ea.com/news.txt");
                }
                content.append(Const.NUL);
                int size = 12 + content.toString().getBytes().length; // 12 = header (4) + flags (4) + length (4)
                if (size > 13) {
                    String formattedSize = String.format("%4s", Character.toString(size))
                            .replace(' ', '\0');
                    String reply = header + flags + formattedSize + content;
                    log.info("Send: " + Const.LF + reply);
                    writer.println(reply);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

}
