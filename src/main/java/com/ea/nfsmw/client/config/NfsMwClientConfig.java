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

            String packet = formatHexString(input);
            String packetSeq = packet.substring(0, 4);
            String packetEndSeq = packet.substring(packet.length() - 2, packet.length());

            String lastPacketStr;
            String lastPacketTypeStr = "";
            if(lastPacket != null) {
                lastPacketStr = formatHexString(lastPacket);
                lastPacketTypeStr = lastPacketStr.substring(lastPacketStr.length() - 2, lastPacketStr.length());
            }

            if(startSes < 3 && lastPacket != null) {
                if(startSes == 1 && (lastPacketTypeStr.equals("24") || lastPacketTypeStr.equals("0d") || lastPacketTypeStr.equals("17") || lastPacketTypeStr.equals("18"))) {
                    byte[] newInput = HexFormat.of().parseHex("180100001a0100001001020770000000000006434c49454e5400048d5b7dd280cc3c8f3d0f3e0f3f0f400f410f420f430f440f450f460f470f480f490f4a0f4b0f4c0f4d0f4e0f4f0f500f510f520f540f560f568f570f578f580f588f590f598f5a0f5a8f5b0f5b8f5c0f5c8f5d0f5d8f5e0f5e8f5f0f5f8f1d92608f610f648f680f6b8f6f0f728f760f768f770f778f780f788f790f798f7a0f7a8f0984860fffffffffac91ffffffff85118d0f8d8f8e0f918f950f988fb0872a091c07f08771877ffff0876785ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff00027ac30040045d0006000045");
                    System.arraycopy(lastPacket, 0, newInput, 0, 8);
                    input = newInput;
                    startSes++;
                } else if (startSes == 2) {
                    byte[] newInput = HexFormat.of().parseHex("190100001a01000010020205f1ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff9e8aaa13b88f388f378f7fffffffffffe6e68100000001000000008000000100000000800000000000000180000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016e6e6e68005");
                    System.arraycopy(lastPacket, 0, newInput, 0, 8);
                    input = newInput;
                    startSes++;
                }
            } else if (startSes == 3) {
                if (lastPacketTypeStr.equals("40")) {
                    byte[] newInput = HexFormat.of().parseHex("1c0100001e010000b00000004005");
                    System.arraycopy(lastPacket, 0, newInput, 0, 8);
                    send(newInput);
                    startSes = 4;
                }
            } else if (startSes == 4) {
                if (lastPacketTypeStr.equals("40")) {
                    byte[] newInput = HexFormat.of().parseHex("2001000020010000b00000006a614e812000da140000bbbfa8000a000045");
                    System.arraycopy(lastPacket, 0, newInput, 0, 8);
                    send(newInput);
                }
                if (lastPacketTypeStr.equals("45")) {
                    byte[] newInput = HexFormat.of().parseHex("2501000027010000");
                    System.arraycopy(lastPacket, 0, newInput, 0, 8);
                    input = newInput;
                    startSes = 5;
                }
            } else if (startSes == 5 && !packetSeq.endsWith("1")) {
                byte[] newInput = input;
                System.arraycopy(lastPacket, 0, newInput, 4, 4);

                if(packetSeq.endsWith("00")) {
                    if (packetEndSeq.equals("06")) {
                        newInput = HexFormat.of().parseHex("8000000025010000f8b424e9c006");
                        System.arraycopy(input, 0, newInput, 0, 4);
                        System.arraycopy(lastPacket, 0, newInput, 4, 4);
                    } else if (packetEndSeq.equals("46")) {
                        newInput = HexFormat.of().parseHex("8300000025010000f8b7dd053000166a28000a1ef10008000046");
                        System.arraycopy(input, 0, newInput, 0, 4);
                        System.arraycopy(lastPacket, 0, newInput, 4, 4);
                    }
                }
                input = newInput;
            }

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
        InetAddress server = InetAddress.getByName(props.getNfsIp());
        DatagramPacket dataSent = new DatagramPacket(input, input.length, server, props.getNfsUdpPort());
        log.info("Send to NFSMW:\n{}", HexDumpUtils.formatHexDump(dataSent.getData(), 0, dataSent.getLength()));
        udpSocket.send(dataSent);

        byte[] out = new byte[256];
        DatagramPacket dataReceived = new DatagramPacket(out, out.length);
        udpSocket.receive(dataReceived);
        log.info("Received from NFSMW:\n{}", HexDumpUtils.formatHexDump(dataReceived.getData(), 0, dataReceived.getLength()));

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
