package com.ea;

import com.ea.config.ServerConfig;
import com.ea.config.SslSocketThread;
import com.ea.config.TcpSocketThread;
import com.ea.config.UdpSocketThread;
import com.ea.dto.SessionData;
import com.ea.enums.CertificateKind;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.env.Environment;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.function.BiFunction;

/**
 * Entry point
 */
@Slf4j
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ServerApp implements CommandLineRunner {

    private static final String WII = "wii";

    @Autowired
    private Props props;

    @Autowired
    private Environment env;

    @Autowired
    private ServerConfig serverConfig;

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

            CertificateKind certificateKind = env.getActiveProfiles().length > 0
                   && env.getActiveProfiles()[0].contains(WII) ? CertificateKind.MOH_WII : CertificateKind.MOH_PSP;

            SSLServerSocket sslServerSocket = serverConfig.createSslServerSocket(props.getSslPort(), certificateKind);
            SSLServerSocket sslTosServerSocket = serverConfig.createSslServerSocket(443, CertificateKind.TOS);
            ServerSocket tcpServerSocket = serverConfig.createTcpServerSocket(props.getTcpPort());
            ServerSocket tcpTosServerSocket = serverConfig.createTcpServerSocket(80);

            if(!props.isConnectModeEnabled()) {
                DatagramSocket udpServerSocket = serverConfig.createUdpServerSocket();
                new Thread(new UdpSocketThread(udpServerSocket)).start();
            }

            startServerThread(sslServerSocket, (socket, sessionData) -> new SslSocketThread((SSLSocket) socket));
            startServerThread(sslTosServerSocket, (socket, sessionData) -> new SslSocketThread((SSLSocket) socket));
            startServerThread(tcpServerSocket, TcpSocketThread::new);
            startServerThread(tcpTosServerSocket, TcpSocketThread::new);

            log.info("Servers started. Waiting for client connections...");
        } catch (Exception e) {
            log.error("Error starting servers", e);
        }
    }

    private void startServerThread(ServerSocket serverSocket, BiFunction<Socket, SessionData, Runnable> runnableFactory) {
        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(runnableFactory.apply(socket, new SessionData())).start();
                }
            } catch (IOException e) {
                log.error("Error accepting connections", e);
            }
        }).start();
    }

}
