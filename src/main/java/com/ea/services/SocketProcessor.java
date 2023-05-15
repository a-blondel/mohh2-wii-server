package com.ea.services;

import com.ea.models.SocketData;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SocketProcessor {

    public static final String NUL = "\0";
    public static final String LF = "\n";

    public static ScheduledExecutorService pingExecutor;

    /**
     * Prepares the output message based on request type,
     * then calls the writer
     * @param socket the socket to give to the writer
     * @param socketData the object to process
     */
    public static void process(Socket socket, SocketData socketData) {
        StringBuffer content = new StringBuffer();
        List<SocketData> sendAfter = new ArrayList<>();
        boolean send = true;
        switch (socketData.getIdMessage()) {
            case ("@tic"), ("~png"):
                send = false;
                break;
            case ("@dir"):
                content.append("ADDR=127.0.0.1" + LF);
                content.append("PORT=21172" + LF);
                //content.append("PORT=10901" + LF);
                content.append("SESS=1337420011" + LF);
                content.append("MASK=dbbcc81057aa718bbdafe887591112b4" + NUL);
                break;
            case ("addr"):
                content.append("ADDR=" + socket.getLocalAddress().getHostAddress() + NUL);
                pingExecutor = Executors.newSingleThreadScheduledExecutor();
                pingExecutor.scheduleAtFixedRate(() -> startPing(socket), 30, 30, TimeUnit.SECONDS);
                break;
            case ("skey"):
                content.append("SKEY=$37940faf2a8d1381a3b7d0d2f570e6a7" + NUL);
                break;
            case ("news"):
                content.append("Official servers are down" + NUL);
                break;
            case ("sele"):
                content.append("MORE=1" + LF);
                content.append("STATS=0" + LF);
                content.append("SLOTS=10" + NUL);
                break;
            case ("auth"):
                content.append("NAME=Name" + LF);
                content.append("ADDR=" + socket.getLocalAddress().getHostAddress() + LF);
                content.append("PERSONAS=User" + LF);
                content.append("LOC=frFR" + LF);
                content.append("MAIL=user@domain.com" + LF);
                content.append("SPAM=NN" + NUL);
                break;
            case ("pers"):
                content.append("PERS=User" + LF);
                content.append("LKEY=3fcf27540c92935b0a66fd3b0000283c" + LF);
                content.append("LOC=frFR" + NUL);

                sendAfter.add(new SocketData("+who", null, null));
                break;
            case ("llvl"):
                break;
            case ("gpsc"):
                content.append("IDENT=User" + LF);
                content.append("WHEN=2003.12.8 15:52:54" + LF);
                content.append("WHENC=2003.12.8 15:52:54" + LF);
                content.append("ROOM=User" + LF);
                content.append("HOST=User" + LF);
                content.append("GPSHOST=User" + LF);
                content.append("ADDR=127.0.0.1" + LF);
                content.append("GAMEPORT=21172" + LF);
                content.append("COUNT=1" + LF);
                content.append("PRIV=0" + LF);
                content.append("GPSREGION=2" + LF);
                content.append("SEED=12345" + LF);
                content.append("GAMEMODE=0" + LF);
                content.append("PARTPARAMS=0" + LF);
                content.append("OPGUEST=0" + LF);
                content.append("PARTSIZE0=17" + LF);
                content.append("VOIPPORT=9667" + LF);
                content.append("EVGID=0" + LF);
                content.append("EVID=0" + LF);
                content.append("PARTPARAMS0=" + LF);

                // Got from request
                content.append("PARAMS=8,65,,1,5,,,a,3,-1,1,1,1,1,1,1,1,1,10,,,15f90,122d0022" + LF);
                content.append("USERPARAMS=AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" + LF);
                content.append("NAME=User" + LF);
                content.append("MAXSIZE=17" + LF);
                content.append("CUSTFLAGS=0" + LF);
                content.append("NUMPART=1" + LF);
                content.append("USERPART=0" + LF);
                content.append("USERFLAGS=1" + LF);
                content.append("PASS=" + LF);
                content.append("SYSFLAGS=262656" + LF);
                content.append("MINSIZE=1" + NUL);

                pingExecutor.scheduleAtFixedRate(() -> startPing(socket), 30, 30, TimeUnit.SECONDS);

                sendAfter.add(new SocketData("+who", null, null));
                sendAfter.add(new SocketData("+mgm", null, content.toString()));
                break;
            default:
                log.info("Unsupported operation: {}", socketData.getIdMessage());
                break;
        }
        if (send) {
            socketData.setOutputMessage(content.toString());
            SocketWriter.write(socket, socketData);
        }

        for (SocketData otherSocketData : sendAfter) {
            switch (otherSocketData.getIdMessage()) {
                case ("+who"):
                    sendProfile(socket, otherSocketData);
                    break;
                case ("+mgm"):
                    createGame(socket, otherSocketData);
                    break;
                default:
                    log.info("Unsupported operation: {}", socketData.getIdMessage());
                    break;
            }
        }
    }

    public static void createGame(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);
    }

    public static void sendProfile(Socket socket, SocketData socketData) {
        StringBuffer content = new StringBuffer();
        content.append("M=User" + LF);
        content.append("N=User" + LF);
        content.append("MA=$7a790554222c" + LF);
        content.append("A=" + socket.getLocalAddress().getHostAddress() + LF);
        content.append("LA=User" + LF);
        content.append("P=1" + LF);
        content.append("AT=" + LF);
        content.append("C=4000,,7,1,1,,1,1,5553" + LF);
        content.append("CL=511" + LF);
        content.append("F=U" + LF);
        content.append("G=0" + LF);
        content.append("HW=0" + LF);
        content.append("I=71615" + LF);
        content.append("LO=frFR" + LF);
        content.append("LV=1049601" + LF);
        content.append("MD=0" + LF);
        content.append("PRES=1" + LF);
        content.append("RP=0" + LF);
        content.append("S=1,2,3,4,5,6,7,493E0,C350" + LF);
        content.append("US=0" + LF);
        content.append("VER=5" + LF);
        content.append("WT=0" + LF);
        content.append("WI=0" + LF);
        content.append("R=0" + LF);
        content.append("C=0" + LF);
        content.append("RI=0" + LF);
        content.append("I=1024" + LF);
        content.append("RT=0" + LF);
        content.append("LA=" + socket.getLocalAddress().getHostAddress() + LF);
        content.append("MA=" + LF);
        content.append("X=0" + NUL);

        socketData.setOutputMessage(content.toString());
        SocketWriter.write(socket, socketData);
    }

    public static void startPing(Socket socket) {
        SocketData socketData = new SocketData("~png", null, null);
        SocketWriter.write(socket, socketData);
    }

}
