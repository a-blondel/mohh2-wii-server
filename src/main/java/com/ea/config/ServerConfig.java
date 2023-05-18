package com.ea.config;

import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.ServerSocket;

import static com.ea.utils.PropertiesLoader.getIntegerProperty;
import static com.ea.utils.PropertiesLoader.getStringProperty;

public class ServerConfig {

    /**
     * Initiate the SSL server socket
     * @return SSLServerSocket
     * @throws IOException
     */
    public static SSLServerSocket createSslServerSocket() throws IOException {
        SSLContext sslContext = createSslContext();
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();

        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(getIntegerProperty("ssl.port"));
        serverSocket.setEnabledProtocols(getStringProperty("ssl.protocols").split(","));
        serverSocket.setEnabledCipherSuites(getStringProperty("ssl.cipher-suites").split(","));

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
     * Initiate the server socket
     * @return ServerSocket
     * @throws IOException
     */
    public static ServerSocket createServerSocket() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(getIntegerProperty("tcp.port"));
        return serverSocket;
    }

}
