package com.ea.nfsmw.client.config;

import com.ea.dto.SocketData;
import com.ea.utils.HexDumpUtils;
import com.ea.utils.Props;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@Slf4j
@Configuration
public class NfsMwClientConfig {


    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private PrintWriter out;
    private BufferedReader in;

    @Autowired
    private Props props;

    public void startTcpConnection() {
        try {
            tcpSocket = new Socket(props.getNfsIp(), props.getNfsTcpPort());
            //out = new PrintWriter(clientSocket.getOutputStream(), true);
            //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
            InetAddress server = InetAddress.getByName(props.getNfsIp());
            DatagramPacket dataSent = new DatagramPacket(input, input.length, server, props.getNfsUdpPort());
            log.info("Send to NFSMW:\n{}", HexDumpUtils.formatHexDump(dataSent.getData(), 0, dataSent.getLength()));
            udpSocket.send(dataSent);

            byte[] out = new byte[256];
            DatagramPacket dataReceived = new DatagramPacket(out, out.length);
            udpSocket.receive(dataReceived);
            log.info("Received from NFSMW:\n{}", HexDumpUtils.formatHexDump(dataReceived.getData(), 0, dataReceived.getLength()));

            return Arrays.copyOf(dataReceived.getData(), dataReceived.getLength());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendTcpMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopTcpConnection() throws IOException {
        in.close();
        out.close();
        tcpSocket.close();
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
}
