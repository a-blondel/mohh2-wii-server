package com.ea.config;

import com.ea.enums.CertificateKind;
import com.ea.utils.Props;
import com.ea.dirtysdk.ProtoSSL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.Certificate;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ComponentScan("com.ea")
public class ServerConfig {

    private final Props props;
    private final ProtoSSL protoSSL;


    /**
     * Initiate the SSL server socket
     * @return SSLServerSocket
     * @throws IOException
     */
    public SSLServerSocket createSslServerSocket(int port, CertificateKind certificateKind) throws Exception {
        Pair<KeyPair, Certificate> eaCert = protoSSL.getEaCert(certificateKind);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setKeyEntry(certificateKind.getName(), eaCert.getLeft().getPrivate(), "password".toCharArray(), new Certificate[]{eaCert.getRight()});

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "password".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("SSLv3");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

        sslServerSocket.setEnabledProtocols(props.getSslProtocols().split(","));
        sslServerSocket.setEnabledCipherSuites(props.getSslCipherSuites().split(","));

        return sslServerSocket;
    }

    /**
     * Initiate the TCP server socket
     * @return ServerSocket
     * @throws IOException
     */
    public ServerSocket createTcpServerSocket(int port) throws IOException {
        return ServerSocketFactory.getDefault().createServerSocket(port);
    }

    /**
     * Initiate the UDP server socket
     * @return ServerSocket
     * @throws IOException
     */
    public DatagramSocket createUdpServerSocket() throws IOException {
        return new DatagramSocket(props.getUdpPort());
    }

}
