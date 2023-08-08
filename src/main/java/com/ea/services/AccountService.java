package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.AccountEntity;
import com.ea.repositories.AccountRepository;
import com.ea.steps.SocketWriter;
import com.ea.utils.PasswordUtils;
import com.ea.utils.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AccountService {

    @Autowired
    private SocketWriter socketWriter;

    @Autowired
    private SocketUtils socketUtils;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SessionData sessionData;

    /**
     * Account creation
     * @param socket
     * @param socketData
     */
    public void acct(Socket socket, SocketData socketData) {

        // Exists in DB ?

        // IF TRUE

        // DUPE NAME ERROR
        // DUPE MAIL ERROR ?

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
        String tos = socketUtils.getValueFromSocket(socketData.getInputMessage(), "TOS");
        String tick = socketUtils.getValueFromSocket(socketData.getInputMessage(), "TICK");
        String gamecode = socketUtils.getValueFromSocket(socketData.getInputMessage(), "GAMECODE");
        String vers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "VERS");
        String sku = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SKU");
        String slus = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SLUS");
        String sdkvers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SDKVERS");
        String builddate = socketUtils.getValueFromSocket(socketData.getInputMessage(), "BUILDDATE");

        // Number of alternate names to provide if persona duplicate is found.
        String alts = socketUtils.getValueFromSocket(socketData.getInputMessage(), "ALTS");

        // The game sends a tilde before the password
        if (pass.charAt(0) == '~') {
            pass = pass.substring(1);
        }

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setName(name);
        accountEntity.setPass(passwordUtils.encode(pass));
        accountEntity.setLoc(loc);
        accountEntity.setMail(mail);
        accountEntity.setBorn(born);
        accountEntity.setZip(zip);
        accountEntity.setGend(gend);
        accountEntity.setSpam(spam);
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
    public void edit(Socket socket, SocketData socketData) {

        String name = socketUtils.getValueFromSocket(socketData.getInputMessage(), "NAME");

        Optional<AccountEntity> accountEntityOpt = accountRepository.findByName(name);

        if (accountEntityOpt.isPresent()) {
            AccountEntity accountEntity = accountEntityOpt.get();

            String pass = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PASS");
            String mail = socketUtils.getValueFromSocket(socketData.getInputMessage(), "MAIL");
            String spam = socketUtils.getValueFromSocket(socketData.getInputMessage(), "SPAM");
            String chng = socketUtils.getValueFromSocket(socketData.getInputMessage(), "CHNG");

            if (!pass.equals(chng)) {
                if (passwordUtils.matches(pass, accountEntity.getPass())) {
                    accountEntity.setPass(passwordUtils.encode(chng));
                    if (mail != null) { // Can we send an 'empty mail' error instead ?
                        accountEntity.setMail(mail);
                    }
                    accountEntity.setSpam(spam);
                    accountEntity.setUpdatedOn(Timestamp.from(Instant.now()));
                    accountRepository.save(accountEntity);
                }
                // TODO : ELSE error WRONG PW
            }
        }
        // TODO : else NOT FOUND

        socketWriter.write(socket, socketData);
    }

    /**
     * Account login
     * @param socket
     * @param socketData
     */
    public void auth(Socket socket, SocketData socketData) {
        Map<String, String> content = null;

        String name = socketUtils.getValueFromSocket(socketData.getInputMessage(), "NAME");
        String pass = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PASS");

        Optional<AccountEntity> accountEntityOpt = accountRepository.findByName(name);

        if (accountEntityOpt.isPresent()) {
            AccountEntity accountEntity = accountEntityOpt.get();

            // The game sends a tilde before the password
            if (pass.charAt(0) == '~') {
                pass = pass.substring(1);
            }

            if (passwordUtils.matches(pass, accountEntity.getPass())) {

                sessionData.setCurrentAccount(accountEntity);

                // TODO : Find personas in DB, ...
                // String personas = ...
                content = Stream.of(new String[][]{
                        { "NAME", accountEntity.getName() },
                        { "ADDR", socket.getInetAddress().getHostName() },
                        { "PERSONAS", "player" }, // If personas is not null, comma separated list ??
                        { "LOC", accountEntity.getLoc() },
                        { "MAIL", accountEntity.getMail() },
                        { "SPAM", accountEntity.getSpam() }
                }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

            } else {
                // TODO: ERROR - INVALID LOGIN/PW
            }
        } else {
            // TODO: ERROR - INEXISTING
        }

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);
    }

}
