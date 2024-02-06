package com.ea.nfsmw.client.services;

import com.ea.dto.SocketData;
import com.ea.nfsmw.client.config.NfsMwClientConfig;
import com.ea.nfsmw.client.steps.NfsMwSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ea.steps.DatagramSocketProcessor.*;
import static com.ea.utils.SocketUtils.*;

/**
 * Need For Speed Most Wanted Client Service
 */
@Slf4j
@Service
public class NfsMwClientService {

    @Autowired
    private NfsMwClientConfig nfsMwClientConfig;

    @Autowired
    private NfsMwSocketHandler nfsMwSocketHandler;

    private boolean nfsInitialized = false;


    public void mockClient() {

        if(!nfsInitialized) {
            try {
                init();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            nfsInitialized = true;
        }

        final byte[][] response = {nfsMwClientConfig.sendUdp(HexFormat.of().parseHex("0000000122cec37a4d17b1de34356fe2"))};


        try {
            Thread.sleep(250);

            response[0] = nfsMwClientConfig.sendUdp(HexFormat.of().parseHex("0000000222cec37a4d17b1de34356fe2"));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public void init() throws InterruptedException {
        addr();
        Thread.sleep(100);
        skey();
        Thread.sleep(100);
        news();
        Thread.sleep(100);
        sele1();
        Thread.sleep(100);
        auth();
        Thread.sleep(100);
        pers();
        Thread.sleep(100);
        gjoi();
    }

    public byte[] sendUdp(byte[] buf) {

        if(!nfsInitialized) {
            try {
                init();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            nfsInitialized = true;
        }

        // Forward UDP packet to NFSMW client
        byte[] nfsResult = nfsMwClientConfig.sendUdp(buf);

        return nfsResult;
    }

    public void addr() {
        Map<String, String> content = Stream.of(new String[][] {
                { "ADDR", "192.168.1.90" },
                { "PORT", String.valueOf(nfsMwClientConfig.getTcpSocket().getLocalPort()) },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("addr", null, content));
    }

    public void skey() {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKEY", "$5075626c6963204b6579" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("skey", null, content));
    }

    public void news() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "client.cfg" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("news", null, content));
    }

    public void news2() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "8" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("news", null, content));
    }

    public void cate() {
        Map<String, String> content = Stream.of(new String[][] {
                { "VIEW", "PS2" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("cate", null, content));
    }

    public void usld() {
        nfsMwClientConfig.write(new SocketData("usld", null, null));
    }


    public void gsea() {
        Map<String, String> content = Stream.of(new String[][] {
                { "START", "0" },
                { "COUNT", "10" },
                { "ASYNC", "1" },
                { "SYSMASK", "262144" },
                { "SYSFLAGS", "0" },
                { "PLAYERS", "1" },
                { "CUSTOM", ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,4,2,,,1,2,,1" },
                { "CUSTMASK", "0" },
                { "CUSTFLAGS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("gsea", null, content));
    }


    public void auth() {

        String tickStr ="5ea182808080808080f7e18080c094808480a8c489c3a68c9a8080808080808080808080808080808080808480c0e0cc99b3e680b8808082808082b4dcd7ef91af808e80c08080c0808df7ad94e09f8081809080808080808081fad982c0808090cfec9593c7eddbb0e48080808080808080808080808080808080808080808080808080808080828084c4c983808080828088d4f3868080808880e0a8858a8c98b0ecb491c4a9d5a9b3e0c0b193e69798b0808080808080808180908080808080808080808080808080e08880b08980848088c882b5c893808880fc81d3c7808ee2cab3aaeeedeabca095e4a5d19bb8abe9c188a3d4f3e9f3e5a28d8caa9391f682ba809088fb96f3efeead84ca8087cdeeb7e88ceab7dbb5bfe5b1c5bfaaeb90e98c8c86";
        byte[] tickBytes = HexFormat.of().parseHex(tickStr);
        String tickResult = new String(tickBytes, StandardCharsets.UTF_8);


        String maddrStr ="4f7665726c6f61642463306138303135612430303030303030305e808080808080808080805e80808080808080808080";
        byte[] maddrBytes = HexFormat.of().parseHex(maddrStr);
        String maddrResult = new String(maddrBytes, StandardCharsets.UTF_8);


        Map<String, String> content = Stream.of(new String[][] {
                { "VERS", "BURNOUT5/31" },
                { "SLUS", "07604772/US" },
                { "SKU", "PS3" },
                { "LOC", "enZZ" },
                { "MID", "$000000000000" },
                { "SDKVERS", "5.5.3.0" },
                { "BUILDDATE", "\"Sep 19 2007\"" },
                { "NAME", "" },
                { "TICK", tickResult },
                { "MADDR", maddrResult },
                { "MAC", "$000000000000" },
                { "PASS", "\"~2s,%22rYa2 K},1%3dV.0qa/4:c@hB [JX^\"" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("auth", null, content));
    }

    public void pers() {

        String maddrStr ="4f7665726c6f61642463306138303135612430303030303030305e808080808080808080805e80808080808080808080";
        byte[] maddrBytes = HexFormat.of().parseHex(maddrStr);
        String maddrResult = new String(maddrBytes, StandardCharsets.UTF_8);

        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "CLIENT" },
                { "MADDR", maddrResult },
                { "MAC", "$000000000000" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("pers", null, content));
    }

    public void sele1() {
        Map<String, String> content = Stream.of(new String[][] {
                { "MYGAME", "1" },
                { "GAMES", "0" },
                { "MESGTYPES", "100728964" },
                { "STATS", "500" },
                { "RANKS", "1" },
                { "MESGS", "1" },
                { "ROOMS", "0" },
                { "USERS", "1" },
                { "USERSETS", "1" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("sele", null, content));
    }

    public void gjoi() {
        Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "Bat-les-Steaks" },
                { "PASS", "" },
                { "PARAMS", ",,,b80,d003f6e0656e47423" },
                { "MINSIZE", "2" },
                { "MAXSIZE", "9" },
                { "CUSTFLAGS", "413082880" },
                { "SYSFLAGS", "64" },
                { "ROOM", "0" },
                { "IDENT", "19" },
                { "SESS", "@brobot78-Bat-les-Steaks-65c1684b" },
                { "PRIV", "0" },
                { "SEED", "13" },
                { "FORCE_LEAVE", "1" },
                { "USERPARAMS", "PUSMC01?????,,,ff-1,,d" },
                { "USERFLAGS", "0" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        nfsMwClientConfig.write(new SocketData("gjoi", null, content));
    }

}
