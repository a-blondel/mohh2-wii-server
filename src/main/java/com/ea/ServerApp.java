package com.ea;

import com.ea.config.ServerConfig;
import com.ea.config.SslSocketThread;
import com.ea.config.TcpSocketThread;
import com.ea.config.UdpSocketThread;
import com.ea.nfsmw.client.config.NfsMwClientConfig;
import com.ea.nfsmw.client.config.NfsMwSocketThread;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.Security;

/**
 * Entry point
 */
@Slf4j
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ServerApp implements CommandLineRunner {
    @Autowired
    Props props;

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    NfsMwClientConfig nfsMwClientConfig;

    @Autowired
    SslSocketThread sslSocketThread;

    @Autowired
    TcpSocketThread tcpSocketThread;

    @Autowired
    UdpSocketThread udpSocketThread;

    @Autowired
    NfsMwSocketThread nfsMwSocketThread;

    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Security.setProperty("jdk.tls.disabledAlgorithms", "");
        System.setProperty("https.protocols", props.getSslProtocols());
        System.setProperty("https.cipherSuites", props.getSslCipherSuites());
        System.setProperty("jdk.tls.client.cipherSuites", props.getSslCipherSuites());
        System.setProperty("jdk.tls.server.cipherSuites", props.getSslCipherSuites());

        if (props.isSslDebugEnabled()) {
            System.setProperty("javax.net.debug", "all");
        }

        try {
            log.info("Starting servers...");
            SSLServerSocket sslServerSocket = serverConfig.createSslServerSocket();
            ServerSocket tcpServerSocket = serverConfig.createTcpServerSocket();
            DatagramSocket udpServerSocket = serverConfig.createUdpServerSocket();
            nfsMwClientConfig.startTcpConnection();
            nfsMwClientConfig.startUdpConnection();
            log.info("Servers started. Waiting for client connections...");

            while(true) {
                sslSocketThread.setClientSocket((SSLSocket) sslServerSocket.accept());
                Thread threadSSL = new Thread(sslSocketThread);
                threadSSL.start();

                tcpSocketThread.setClientSocket(tcpServerSocket.accept());
                Thread threadTCP = new Thread(tcpSocketThread);
                threadTCP.start();

                udpSocketThread.setClientSocket(udpServerSocket);
                Thread threadUDP = new Thread(udpSocketThread);
                threadUDP.start();

                nfsMwSocketThread.setClientSocket(nfsMwClientConfig.getTcpSocket());
                Thread nfsThreadTCP = new Thread(nfsMwSocketThread);
                nfsThreadTCP.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
