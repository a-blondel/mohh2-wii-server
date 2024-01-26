package com.ea.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class Props {

    @Value("${ssl.port}")
    private int sslPort;

    @Value("${ssl.protocols}")
    private String sslProtocols;

    @Value("${ssl.cipher-suites}")
    private String sslCipherSuites;

    @Value("${ssl.debug.enabled}")
    private boolean sslDebugEnabled;

    @Value("${tcp.port}")
    private int tcpPort;

    @Value("${tcp.debug.enabled}")
    private boolean tcpDebugEnabled;

    @Value("${tcp.debug.exclusions}")
    private List<String> tcpDebugExclusions;

    @Value("${udp.port}")
    private int udpPort;

    @Value("${udp.connect-mode.enabled}")
    private boolean connectModeEnabled;

    @Value("${udp.debug.enabled}")
    private boolean udpDebugEnabled;

}
