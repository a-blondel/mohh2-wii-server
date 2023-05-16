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
            case ("gsea"):
                content.append("COUNT=3" + NUL);
                sendAfter.add(new SocketData("+gam", null, null));
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
                    createLobby(socket, otherSocketData);
                    break;
                case ("+gam"):
                    showLobbies(socket, otherSocketData.getIdMessage());
                    break;
                default:
                    log.info("Unsupported operation: {}", socketData.getIdMessage());
                    break;
            }
        }
    }

    public static void createLobby(Socket socket, SocketData socketData) {
        SocketWriter.write(socket, socketData);
    }

    /** PARAMS
     * 1 = Mode (2 = CTF, 7 = TDM, 8 = DM)
     * 2 = Map (191 = Village, 65 = Port, 1f5 = monastery, c9 = City, 12d = sewers, 259 = base)
     * 3 = Friendly fire (1 = true, 2 = reverse fire, empty = false)
     * 4 = Equilibrate (1 = true, empty = false)
     * 5 = number of rounds
     * 6 = points limit
     * 7 = score limit
     * 8 = round time limit
     * 9 = max team kills
     * 10 = controls (empty = Elite, ? = Zapper, -1 = all)
     * 11 = SMG (1 = true, empty = false)
     * 12 = HMG
     * 13 = Rifle
     * 14 = Scoped Rifle
     * 15 = Shotgun
     * 16 = Bazooka
     * 17 = Grenades
     * 18 = Ranked - Must come with SYSFLAGS (ranked = 262656, unranked = 512) !
     * 19 = max players
     */
    public static void showLobbies(Socket socket, String messageId) {
        StringBuffer content = new StringBuffer();
        content.append("IDENT=1" + LF);
        content.append("NAME=\"Modded lobby\"" + LF);
        content.append("PARAMS=2,191,,,,,,,,-1,1,1,1,1,1,1,1,1,20" + LF);
        content.append("SYSFLAGS=262656" + LF);
        content.append("COUNT=31" + LF);
        content.append("MAXSIZE=33" + NUL);
        //content.append("PASS=" + LF);

        SocketData socketData = new SocketData(messageId, null, content.toString());
        SocketWriter.write(socket, socketData);

        content = new StringBuffer();
        content.append("IDENT=2" + LF);
        content.append("NAME=\"Glitch\"" + LF);
        content.append("PARAMS=7,65,,,a,,32,,,-1,1,1,1,1,1,1,1,,5" + LF);
        content.append("SYSFLAGS=512" + LF);
        content.append("COUNT=2" + LF);
        content.append("MAXSIZE=6" + NUL);

        SocketData socketData2 = new SocketData(messageId, null, content.toString());
        SocketWriter.write(socket, socketData2);

        content = new StringBuffer();
        content.append("IDENT=3" + LF);
        content.append("NAME=\"Join :)\"" + LF);
        content.append("PARAMS=8,1f5,,,5,,14,,,-1,1,1,1,1,1,1,1,1,10" + LF);
        content.append("SYSFLAGS=262656" + LF);
        content.append("COUNT=9" + LF);
        content.append("MAXSIZE=17" + NUL);

        SocketData socketData3 = new SocketData(messageId, null, content.toString());
        SocketWriter.write(socket, socketData3);
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
