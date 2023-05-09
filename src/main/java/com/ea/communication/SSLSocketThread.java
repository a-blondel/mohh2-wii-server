package com.ea.communication;

import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;

/**
 * Thread to handle a unique SSL socket
 */
@Slf4j
public class SSLSocketThread implements Runnable {

    SSLSocket clientSocket;

    public void setClientSocket(SSLSocket clientSocket) throws SocketException {
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
     * @param sslSocket
     * @throws IOException
     */
    private static void exchangeWithSocket(SSLSocket sslSocket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String header = "";
                String flags = "";
                StringBuffer content = new StringBuffer();
                log.info("Received: " + line);
                // @tic request is optional, skipping it
                if (line.contains("@dir")) {
                    header = "@dir";
                    flags = "\0\0\0\0";
                    content.append("ADDR=127.0.0.1" + Const.LF);
                    content.append("PORT=21172" + Const.LF);
                    content.append("SESS=" + sslSocket.hashCode() + Const.LF);
                    content.append("MASK=dbbcc81057aa718bbdafe887591112b4");
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
            sslSocket.close();
        }
    }

}
