package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.AccountEntity;
import com.ea.mappers.SocketMapper;
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
    private SocketMapper socketMapper;

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
            AccountEntity accountEntity = socketMapper.toAccountEntityForCreation(socketData.getInputMessage());
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

            boolean update = false;
            boolean error = false;

            if (mail != null && !mail.equals(accountEntity.getMail())) {
                accountEntity.setMail(mail);
                update = true;
            }

            if (!pass.equals(chng)) {
                if (passwordUtils.matches(pass, accountEntity.getPass())) {
                    accountEntity.setPass(passwordUtils.encode(chng));
                    update = true;
                } else {
                    socketData.setIdMessage("editpass"); // Invalid password error (EC_INV_PASS)
                    error = true;
                }
            }

            if (!error && (update || !spam.equals(accountEntity.getSpam()))) {
                accountEntity.setSpam(spam);
                accountEntity.setUpdatedOn(Timestamp.from(Instant.now()));
                accountRepository.save(accountEntity);
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
