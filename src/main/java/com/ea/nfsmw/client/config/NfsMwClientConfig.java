package com.ea.nfsmw.client.config;

import com.ea.dto.SocketData;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import com.ea.utils.SocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

import static com.ea.utils.SocketUtils.formatHexString;
import static java.util.stream.Collectors.joining;

@Slf4j
@Configuration
public class NfsMwClientConfig {

    private Socket tcpSocket;
    private DatagramSocket udpSocket;

    private int startSes = 0;

    private byte[] lastPacket;

    @Autowired
    private Props props;

    @Autowired
    private SocketUtils socketUtils;

    public void startTcpConnection() {
        try {
            tcpSocket = new Socket(props.getNfsIp(), props.getNfsTcpPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startUdpConnection() {
        try {
            udpSocket = new DatagramSocket(props.getNfsUdpPort());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] sendUdp(byte[] input) {
        try {
            return send(input);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] send(byte[] input) throws IOException {
        InetAddress server = InetAddress.getByName("192.168.1.11");
        DatagramPacket dataSent = new DatagramPacket(input, input.length, server, props.getNfsUdpPort());
        log.info("Send to NFSMW:\n{}", HexDumpUtils.formatHexDump(dataSent.getData()));
        udpSocket.send(dataSent);

        byte[] out = new byte[256];
        DatagramPacket dataReceived = new DatagramPacket(out, out.length);
        udpSocket.receive(dataReceived);

        byte[] dump = Arrays.copyOfRange(dataReceived.getData(), 0, dataReceived.getLength());
        log.info("Received from NFSMW:\n{}", HexDumpUtils.formatHexDump(dump));

        lastPacket = Arrays.copyOf(dataReceived.getData(), dataReceived.getLength());

        return lastPacket;
    }

    public void write(SocketData socketData) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             DataOutputStream writer = new DataOutputStream(buffer)) {

            writer.write(socketData.getIdMessage().getBytes(StandardCharsets.UTF_8));
            if(socketData.getIdMessage().length() == 4) {
                writer.writeInt(0);
            }
            int outputLength = 12;

            if (null != socketData.getOutputData()) {
                byte[] contentBytes = (socketData.getOutputData().entrySet()
                        .stream()
                        .map(param -> param.getKey() + "=" + param.getValue())
                        .collect(joining("\n")) + "\0").getBytes(StandardCharsets.UTF_8);

                outputLength += contentBytes.length;
                writer.writeInt(outputLength);
                writer.write(contentBytes);
            } else {
                writer.writeInt(outputLength);
            }

            byte[] bufferBytes = buffer.toByteArray();
            if (props.isTcpDebugEnabled()) {
                log.info("Send to NFSMW:\n{}", HexDumpUtils.formatHexDump(bufferBytes));
            }
            tcpSocket.getOutputStream().write(bufferBytes);

            //log.info("Response:\n{}", HexDumpUtils.formatHexDump(in.readLine().getBytes(), 0, in.readLine().length()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getTcpSocket() {
        return this.tcpSocket;
    }

    public void setStartSes(int startSes) {
        this.startSes = startSes;
    }

    public int getStartSes() {
        return startSes;
    }
}
