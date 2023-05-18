package com.ea;

import com.ea.config.SslSocketThread;
import com.ea.config.TcpSocketThread;
import com.ea.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.security.*;

/**
 * Entry point
 */
@Slf4j
public class StartServer {

    public static void main(String[] args) {
        // JVM config
        Security.setProperty("jdk.tls.disabledAlgorithms", "");
        System.setProperty("https.protocols", "SSLv3");
        System.setProperty("https.cipherSuites", "SSL_RSA_WITH_RC4_128_MD5,SSL_RSA_WITH_RC4_128_SHA");
        System.setProperty("jdk.tls.client.cipherSuites", "SSL_RSA_WITH_RC4_128_MD5,SSL_RSA_WITH_RC4_128_SHA");
        System.setProperty("jdk.tls.server.cipherSuites", "SSL_RSA_WITH_RC4_128_MD5,SSL_RSA_WITH_RC4_128_SHA");

        // Debug
        // System.setProperty("javax.net.debug", "all");

        // Launch server
        run();
    }

    /**
     * Initiate the server
     */
    private static void run() {
        try {
            log.info("Starting servers...");
            SSLServerSocket sslServerSocket = ServerConfig.createSslServerSocket();
            ServerSocket serverSocket = ServerConfig.createServerSocket();
            log.info("Servers started. Waiting for client connections...");
            while(true){
                // SSL sockets
                SslSocketThread sslSocketThread = new SslSocketThread();
                sslSocketThread.setClientSocket((SSLSocket) sslServerSocket.accept());
                Thread threadSSL = new Thread(sslSocketThread);
                threadSSL.start();

                // Plain TCP sockets
                TcpSocketThread tcpSocketThread = new TcpSocketThread();
                tcpSocketThread.setClientSocket(serverSocket.accept());
                Thread thread = new Thread(tcpSocketThread);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
