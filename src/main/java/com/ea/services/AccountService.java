package com.ea.services;

import com.ea.dto.SocketData;
import com.ea.entities.AccountEntity;
import com.ea.repositories.AccountRepository;
import com.ea.steps.SocketWriter;
import com.ea.utils.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AccountService {

    @Autowired
    private SocketWriter socketWriter;

    @Autowired
    private SocketUtils socketUtils;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Account creation
     * @param socket
     * @param socketData
     */
    public void sendAcct(Socket socket, SocketData socketData) {

        // Exists in DB ?

        // IF TRUE

        // DUPE ERROR

        // ELSE

        /**
         * TODO : Use mapstruct interface
         */
        String name = socketUtils.getValueFromSocket(socketData.getInputMessage(), "NAME");
        String pass = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PASS");
        String loc = socketUtils.getValueFromSocket(socketData.getInputMessage(), "LOC");
        String mail = socketUtils.getValueFromSocket(socketData.getInputMessage(), "MAIL");
        String born = socketUtils.getValueFromSocket(socketData.getInputMessage(), "BORN");
        String zip = socketUtils.getValueFromSocket(socketData.getInputMessage(), "ZIP");
        String gend = socketUtils.getValueFromSocket(socketData.getInputMessage(), "GEND");
        String spam = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SPAM");
        String alts = socketUtils.getValueFromSocket(socketData.getInputMessage(), "ALTS");
        String tos = socketUtils.getValueFromSocket(socketData.getInputMessage(), "TOS");
        String tick = socketUtils.getValueFromSocket(socketData.getInputMessage(), "TICK");
        String gamecode = socketUtils.getValueFromSocket(socketData.getInputMessage(), "GAMECODE");
        String vers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "VERS");
        String sku = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SKU");
        String slus = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SLUS");
        String sdkvers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SDKVERS");
        String builddate = socketUtils.getValueFromSocket(socketData.getInputMessage(), "BUILDDATE");

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setName(name);
        accountEntity.setPass(pass);
        accountEntity.setLoc(loc);
        accountEntity.setMail(mail);
        accountEntity.setBorn(born);
        accountEntity.setZip(zip);
        accountEntity.setGend(gend);
        accountEntity.setSpam(spam);
        accountEntity.setAlts(Integer.parseInt(alts));
        accountEntity.setTos(Integer.parseInt(tos));
        accountEntity.setTick(tick);
        accountEntity.setGamecode(gamecode);
        accountEntity.setVers(vers);
        accountEntity.setSku(sku);
        accountEntity.setSlus(slus);
        accountEntity.setSdkvers(sdkvers);
        accountEntity.setBuilddate(builddate);
        accountEntity.setCreatedOn(Timestamp.from(Instant.now()));

        accountRepository.save(accountEntity);

        // END IF

        socketWriter.write(socket, socketData);
    }

    /**
     * Account update
     * @param socket
     * @param socketData
     */
    public void sendEdit(Socket socket, SocketData socketData) {

        String name = socketUtils.getValueFromSocket(socketData.getInputMessage(), "NAME");
        String pass = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PASS");
        String mail = socketUtils.getValueFromSocket(socketData.getInputMessage(), "MAIL");
        String spam = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SPAM");
        String chng = socketUtils.getValueFromSocket(socketData.getInputMessage(), "CHNG");

        AccountEntity accountEntity = new AccountEntity(); // replace by find by name
        accountEntity.setPass(chng);
        accountEntity.setMail(mail);
        accountEntity.setSpam(spam);
        accountEntity.setUpdatedOn(Timestamp.from(Instant.now()));

        accountRepository.save(accountEntity);

        socketWriter.write(socket, socketData);
    }

    /**
     * Account login
     * @param socket
     * @param socketData
     */
    public void sendAuth(Socket socket, SocketData socketData) {

        String name = socketUtils.getValueFromSocket(socketData.getInputMessage(), "NAME");
        String pass = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PASS");

        // Exists in DB ?

        // IF login and pw matches

        /*Map<String, String> content = Stream.of(new String[][] {
                { "NAME", "player" },
                { "ADDR", socket.getInetAddress().getHostName() },
                { "PERSONAS", "player" }, // If personas is not null
                { "LOC", "frFR" },
                { "MAIL", "player@gmail.com" },
                { "SPAM", "NN" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));*/

        // ELSE

        // END IF

        //socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

}
