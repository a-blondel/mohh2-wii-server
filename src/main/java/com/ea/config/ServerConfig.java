package com.ea.config;

import com.ea.utils.Props;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class ServerConfig {

    /**
     * Initiate the SSL server socket
     * @return SSLServerSocket
     * @throws IOException
     */
    public static SSLServerSocket createSslServerSocket() throws IOException {
        SSLContext sslContext = createSslContext();
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();

        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(Props.getInt("ssl.port"));
        serverSocket.setEnabledProtocols(Props.getString("ssl.protocols").split(","));
        serverSocket.setEnabledCipherSuites(Props.getString("ssl.cipher-suites").split(","));

        return serverSocket;
    }

    /**
     * Create the SSLContext based on PEM files which are not handled by default
     * @return SSLContext
     */
    private static SSLContext createSslContext() {
        X509ExtendedKeyManager keyManager = PemUtils.loadIdentityMaterial("keystore/cert.pem", "keystore/key.pem", "dummy".toCharArray());
        X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial("keystore/cert.pem");

        SSLFactory sslFactory = SSLFactory.builder()
                .withIdentityMaterial(keyManager)
                .withTrustMaterial(trustManager)
                .build();

        return sslFactory.getSslContext();
    }

    /**
     * Initiate the TCP server socket
     * @return ServerSocket
     * @throws IOException
     */
    public static ServerSocket createTcpServerSocket() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(Props.getInt("tcp.port"));
        return serverSocket;
    }

    /**
     * Initiate the UDP server socket
     * @return ServerSocket
     * @throws IOException
     */
    public static DatagramSocket createUdpServerSocket() throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(Props.getInt("udp.port"));
        return serverSocket;
    }

}
