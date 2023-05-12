package com.ea.config;

import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.PemUtils;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
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

        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(21171);
        serverSocket.setEnabledProtocols(new String[]{"SSLv3"});
        serverSocket.setEnabledCipherSuites(new String[]{"SSL_RSA_WITH_RC4_128_MD5","SSL_RSA_WITH_RC4_128_SHA"});

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
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(21172);
        return serverSocket;
    }

}
