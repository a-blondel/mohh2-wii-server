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

            String packetSeq = formatHexString(input).substring(0, 4);

            if(startSes == 1 && lastPacket != null) {
                byte[] newInput = HexFormat.of().parseHex("1a0100001a0100001001010091000000030006434c49454e540004534b05790000da0c2f00bbb7d80000000045");
                System.arraycopy(lastPacket, 0, newInput, 0, 8);
                input = newInput;
                startSes = 2;
            } else if (startSes == 2  && lastPacket != null) {
                byte[] newInput = HexFormat.of().parseHex("1b0100101a010000b000000030051001010091000000030006434c49454e540004534b05790000da0c2f00bbb7d8000000004525");
                System.arraycopy(lastPacket, 0, newInput, 0, 8);
                input = newInput;
                startSes = 3;
            } else if (startSes == 3 && !packetSeq.endsWith("1")) {
                byte[] newInput = input;
                System.arraycopy(lastPacket, 0, newInput, 4, 4);
                input = newInput;
            }

            InetAddress server = InetAddress.getByName(props.getNfsIp());
            DatagramPacket dataSent = new DatagramPacket(input, input.length, server, props.getNfsUdpPort());
            log.info("Send to NFSMW:\n{}", HexDumpUtils.formatHexDump(dataSent.getData(), 0, dataSent.getLength()));
            udpSocket.send(dataSent);

            byte[] out = new byte[256];
            DatagramPacket dataReceived = new DatagramPacket(out, out.length);
            udpSocket.receive(dataReceived);
            log.info("Received from NFSMW:\n{}", HexDumpUtils.formatHexDump(dataReceived.getData(), 0, dataReceived.getLength()));

            lastPacket = dataReceived.getData();

            return Arrays.copyOf(dataReceived.getData(), dataReceived.getLength());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                log.info("Send to NFSMW:\n{}", HexDumpUtils.formatHexDump(bufferBytes, 0, outputLength));
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
