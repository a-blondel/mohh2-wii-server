package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.AccountEntity;
import com.ea.repositories.AccountRepository;
import com.ea.steps.SocketWriter;
import com.ea.utils.AccountUtils;
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
        String name = socketUtils.getValueFromSocket(socketData.getInputMessage(), "NAME");

        Optional<AccountEntity> accountEntityOpt = accountRepository.findByName(name);
        if (accountEntityOpt.isPresent()) {
            socketData.setIdMessage("acctdupl"); // Duplicate account error (EC_DUPLICATE)
            int alts = Integer.parseInt(socketUtils.getValueFromSocket(socketData.getInputMessage(), "ALTS"));
            if (alts > 0) {
                String opts = AccountUtils.suggestNames(alts, name);
                Map<String, String> content = Stream.of(new String[][]{
                        { "OPTS", opts }
                }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
                socketData.setOutputData(content);
            }
        } else {
            /**
             * TODO : Use mapstruct interface
             */
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
        }
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
                    if (mail != null) {
                        accountEntity.setMail(mail);
                    }
                    accountEntity.setSpam(spam);
                    accountEntity.setUpdatedOn(Timestamp.from(Instant.now()));
                    accountRepository.save(accountEntity);
                } else {
                    socketData.setIdMessage("editpass"); // Invalid password error (EC_INV_PASS)
                }
            }
        } else {
            socketData.setIdMessage("editimst"); // Inexisting error (EC_INV_MASTER)
        }

        socketWriter.write(socket, socketData);
    }

    /**
     * Account login
     * @param socket
     * @param socketData
     */
    public void auth(Socket socket, SocketData socketData) {
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

                String personas = accountEntity.getPersonas().stream()
                        .filter(p -> p.getDeletedOn() == null)
                        .map(p -> p.getPers())
                        .collect(Collectors.joining(","));
                Map<String, String> content = Stream.of(new String[][]{
                        { "NAME", accountEntity.getName() },
                        { "ADDR", socket.getInetAddress().getHostName() },
                        { "PERSONAS", personas },
                        { "LOC", accountEntity.getLoc() },
                        { "MAIL", accountEntity.getMail() },
                        { "SPAM", accountEntity.getSpam() }
                }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
                socketData.setOutputData(content);
            } else {
                socketData.setIdMessage("authpass"); // Invalid password error (EC_INV_PASS)
            }
        } else {
            socketData.setIdMessage("authimst"); // Inexisting error (EC_INV_MASTER)
        }

        socketWriter.write(socket, socketData);
    }

}
