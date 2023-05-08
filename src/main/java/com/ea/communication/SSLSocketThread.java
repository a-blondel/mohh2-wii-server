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
            StringBuffer response = new StringBuffer();
            // TODO : parse requests (between '@' and last '\0'), create objects (id, content)
            while ((line = reader.readLine()) != null) {
                log.info("Received: " + line);
                if (line.contains("@tic")) {;
                    writer.println("@tic\0\0\0\0\0\0\0");
                } else if (line.contains("BUILDDATE")) {
                    response.append("@dir\0\0\0\0\0\0\0.");
                    response.append("ADDR=127.0.0.1\n");
                    response.append("PORT=21172\n");
                    response.append("SESS=" + sslSocket.hashCode() + "\n");
                    response.append("MASK=dbbcc81057aa718bbdafe887591112b4\0");
                    log.info("Send: \n" + response);
                    writer.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sslSocket.close();
        }
    }

}
