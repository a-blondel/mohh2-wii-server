package com.ea;

import com.ea.config.ServerConfig;
import com.ea.config.SslSocketThread;
import com.ea.config.TcpSocketThread;
import com.ea.config.UdpSocketThread;
import com.ea.dto.SessionData;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
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

    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }

    @Override
    public void run(String... args) {
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
            if(!props.isConnectModeEnabled()) {
                DatagramSocket udpServerSocket = serverConfig.createUdpServerSocket();
                new Thread(new UdpSocketThread(udpServerSocket)).start();
            }
            log.info("Servers started. Waiting for client connections...");

            while(true) {
                final SessionData sessionData = new SessionData();
                new Thread(new SslSocketThread((SSLSocket) sslServerSocket.accept())).start();
                new Thread(new TcpSocketThread(tcpServerSocket.accept(), sessionData)).start();
            }
        } catch (Exception e) {
            log.error("Error starting servers", e);
        }
    }
}
