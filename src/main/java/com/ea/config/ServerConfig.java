package com.ea.config;

import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.pem.util.PemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

@Slf4j
@Configuration
@ComponentScan("com.ea")
public class ServerConfig {

    @Autowired
    private Props props;

    /**
     * Initiate the SSL server socket
     * @return SSLServerSocket
     * @throws IOException
     */
    public SSLServerSocket createSslServerSocket() throws IOException {
        SSLContext sslContext = createSslContext();
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();

        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(props.getSslPort());
        serverSocket.setEnabledProtocols(props.getSslProtocols().split(","));
        serverSocket.setEnabledCipherSuites(props.getSslCipherSuites().split(","));

        return serverSocket;
    }

    /**
     * Create the SSLContext based on PEM files which are not handled by default
     * @return SSLContext
     */
    private SSLContext createSslContext() {
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
    public ServerSocket createTcpServerSocket() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(props.getTcpPort());
        return serverSocket;
    }

    /**
     * Initiate the UDP server socket
     * @return ServerSocket
     * @throws IOException
     */
    public DatagramSocket createUdpServerSocket() throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(props.getUdpPort());
        return serverSocket;
    }

}
