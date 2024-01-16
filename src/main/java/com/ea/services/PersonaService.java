package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.AccountEntity;
import com.ea.entities.PersonaConnectionEntity;
import com.ea.entities.PersonaEntity;
import com.ea.entities.PersonaStatsEntity;
import com.ea.repositories.PersonaConnectionRepository;
import com.ea.repositories.PersonaRepository;
import com.ea.repositories.PersonaStatsRepository;
import com.ea.steps.SocketWriter;
import com.ea.utils.AccountUtils;
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
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaConnectionRepository personaConnectionRepository;

    @Autowired
    private PersonaStatsRepository personaStatsRepository;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private SocketWriter socketWriter;

    @Autowired
    private SocketUtils socketUtils;

    /**
     * Persona creation
     * @param socket
     * @param socketData
     */
    public void cper(Socket socket, SocketData socketData) {
        String pers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PERS");

        Optional<PersonaEntity> personaEntityOpt = personaRepository.findByPers(pers);
        if (personaEntityOpt.isPresent()) {
            socketData.setIdMessage("cperdupl"); // Duplicate persona error (EC_DUPLICATE)
            int alts = Integer.parseInt(socketUtils.getValueFromSocket(socketData.getInputMessage(), "ALTS"));
            if (alts > 0) {
                String opts = AccountUtils.suggestNames(alts, pers);
                Map<String, String> content = Stream.of(new String[][]{
                        { "OPTS", opts }
                }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
                socketData.setOutputData(content);
            }
        } else {
            PersonaEntity personaEntity = new PersonaEntity();
            personaEntity.setAccount(sessionData.getCurrentAccount());
            personaEntity.setPers(pers);
            personaEntity.setCreatedOn(Timestamp.from(Instant.now()));

            PersonaStatsEntity personaStatsEntity = new PersonaStatsEntity();
            personaStatsEntity.setPersona(personaEntity);
            personaEntity.setPersonaStats(personaStatsEntity);

            personaRepository.save(personaEntity);
        }

        socketWriter.write(socket, socketData);
    }

    /**
     * Get persona
     * @param socket
     * @param socketData
     */
    public void pers(Socket socket, SocketData socketData) {
        String pers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PERS");

        Optional<PersonaEntity> personaEntityOpt = personaRepository.findByPers(pers);
        if (personaEntityOpt.isPresent()) {
            PersonaEntity personaEntity = personaEntityOpt.get();
            sessionData.setCurrentPersonna(personaEntity);
            Map<String, String> content = Stream.of(new String[][] {
                    { "PERS", personaEntity.getPers() },
                    { "LKEY", "" },
                    { "EX-ticker", "" },
                    { "LOC", personaEntity.getAccount().getLoc() },
                    { "A", socket.getInetAddress().getHostName() },
                    { "LA", socket.getInetAddress().getHostName() },
                    { "IDLE", "35000" },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

            socketData.setOutputData(content);
            socketWriter.write(socket, socketData);

            who(socket);

            startPersonaConnection(socket, personaEntity);
        }
    }

    /**
     * Registers a connection of the persona
     * @param socket
     * @param personaEntity
     */
    private void startPersonaConnection(Socket socket, PersonaEntity personaEntity) {
        PersonaConnectionEntity personaConnectionEntity = new PersonaConnectionEntity();
        personaConnectionEntity.setIp(socket.getInetAddress().getHostAddress());
        personaConnectionEntity.setPersona(personaEntity);
        personaConnectionEntity.setStartTime(Timestamp.from(Instant.now()));
        personaConnectionRepository.save(personaConnectionEntity);
        sessionData.setCurrentPersonaConnection(personaConnectionEntity);
    }

    /**
     * Ends the current connection of the persona
     */
    public void endPersonaConnection() {
        PersonaConnectionEntity personaConnectionEntity = sessionData.getCurrentPersonaConnection();
        if(null != personaConnectionEntity) {
            personaConnectionEntity.setEndTime(Timestamp.from(Instant.now()));
            personaConnectionRepository.save(personaConnectionEntity);
        }
    }

    /**
     * Delete persona
     * @param socket
     * @param socketData
     */
    public void dper(Socket socket, SocketData socketData) {
        String pers = socketUtils.getValueFromSocket(socketData.getInputMessage(), "PERS");
        Optional<PersonaEntity> personaEntityOpt = personaRepository.findByPers(pers);
        if (personaEntityOpt.isPresent()) {
            PersonaEntity personaEntity = personaEntityOpt.get();
            personaEntity.setDeletedOn(Timestamp.from(Instant.now()));
            personaRepository.save(personaEntity);
        }
        socketWriter.write(socket, socketData);
    }

    public void llvl(Socket socket, SocketData socketData) {
        Map<String, String> content = Stream.of(new String[][] {
                { "SKILL_PTS", "211" },
                { "SKILL_LVL", "1049601" },
                { "SKILL", "" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        who(socket);
    }

    public void who(Socket socket) {
        PersonaEntity personaEntity = sessionData.getCurrentPersonna();
        AccountEntity accountEntity = sessionData.getCurrentAccount();

        Optional<PersonaStatsEntity> personaStatsEntityOpt = personaStatsRepository.findByPersonaId(personaEntity.getId());
        if (personaStatsEntityOpt.isPresent()) {
            PersonaStatsEntity personaStatsEntity = personaStatsEntityOpt.get();

            Map<String, String> content = Stream.of(new String[][] {
                    { "I", String.valueOf(accountEntity.getId())},
                    { "M", accountEntity.getName() },
                    { "N", personaEntity.getPers() },
                    { "F", "U" },
                    { "P", "40" },
                    // Stats : kills (in hex) at 8th position, deaths (in hex) at 9th
                    { "S", ",,,,,,," + Long.toHexString(personaStatsEntity.getTotalKills()) + "," + Long.toHexString(personaStatsEntity.getTotalDeaths()) },
                    { "X", "0" },
                    { "G", "0" },
                    { "AT", "" },
                    { "CL", "511" },
                    { "LV", "1049601" },
                    { "MD", "0" },
                    { "R", String.valueOf(personaStatsRepository.getRankByPersonaId(personaStatsEntity.getPersona().getId())) }, // Rank (in decimal)
                    { "US", "0" },
                    { "HW", "0" },
                    { "RP", String.valueOf(personaEntity.getRp()) }, // Reputation (0 to 5 stars)
                    { "LO", sessionData.getCurrentAccount().getLoc() }, // Locale (used to display country flag)
                    { "CI", "0" },
                    { "CT", "0" },
                    // 0x800225E0
                    { "A", socket.getInetAddress().getHostName() },
                    { "LA", socket.getInetAddress().getHostName() },
                    // 0x80021384
                    { "C", "4000,,7,1,1,,1,1,5553" },
                    { "RI", "0" },
                    { "RT", "0" },
                    { "RG", "0" },
                    { "RGC", "0" },
                    // 0x80021468 if RI != ?? then read RM and RF
                    { "RM", "0" },
                    { "RF", "0" },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
            socketWriter.write(socket, new SocketData("+who", null, content));
        }
    }

}
