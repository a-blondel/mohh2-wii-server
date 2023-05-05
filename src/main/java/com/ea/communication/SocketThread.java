package com.ea.communication;

import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;

/**
 * Thread to handle a unique socket
 */
@Slf4j
public class SocketThread implements Runnable {

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
                log.info("Received: " + line);
                switch (line) {
                    case ("BUILDDATE=\"Sep  6 2007\""):
                        log.info("Answering OK to client");
                        writer.println("OK");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sslSocket.close();
        }
    }

}
