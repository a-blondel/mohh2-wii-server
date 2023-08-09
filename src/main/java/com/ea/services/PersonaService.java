package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.PersonaEntity;
import com.ea.repositories.PersonaRepository;
import com.ea.steps.SocketWriter;
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
            /**
             * Number of alternate names to provide if persona duplicate is found.
             * We can then return "OPTS" attribute with comma(,) separated list of alternate account names (if param ALTS > 0).
             */
            String alts = socketUtils.getValueFromSocket(socketData.getInputMessage(), "ALTS");
            if (Integer.parseInt(alts) > 0) {
                // We must provide 4 alt options to avoid an empty list
                // Create a better also ?
                // TODO : mutualize method between account and persona
                String opt1 = pers + "1";
                String opt2 = pers + "Kid";
                String opt3 = pers + "Rule";
                String opt4 = pers + "9";
                Map<String, String> content = Stream.of(new String[][]{
                        { "OPTS", opt1.substring(0, opt1.length() > 32 ? 31 : opt1.length()) + ","
                                + opt2.substring(0, opt2.length() > 32 ? 31 : opt2.length()) + ","
                                + opt3.substring(0, opt3.length() > 32 ? 31 : opt3.length()) + ","
                                + opt4.substring(0, opt4.length() > 32 ? 31 : opt4.length())
                        }
                }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
                socketData.setOutputData(content);
            }
        } else {
            PersonaEntity personaEntity = new PersonaEntity();
            personaEntity.setAccount(sessionData.getCurrentAccount());
            personaEntity.setPers(pers);
            personaEntity.setCreatedOn(Timestamp.from(Instant.now()));

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
        Map<String, String> content = Stream.of(new String[][] {
                { "PERS", "player" },
                { "LKEY", "" },
                { "EX-ticker", "" },
                { "LOC", "frFR" },
                { "A", socket.getInetAddress().getHostName() },
                { "LA", socket.getInetAddress().getHostName() },
                { "IDLE", "35000" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        who(socket);
    }

    /**
     * Delete persona
     * @param socket
     * @param socketData
     */
    public void dper(Socket socket, SocketData socketData) {
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
        Map<String, String> content = Stream.of(new String[][] {
                { "I", "71615" },
                { "N", "player" },
                { "F", "U" },
                { "P", "211" },
                { "S", "1,2,3,4,5,6,7,493E0,C350" }, // Stats
                { "X", "0" },
                { "G", "0" },
                { "AT", "" },
                { "CL", "511" },
                { "LV", "1049601" },
                { "MD", "0" },
                { "R", "1" }, // Rank
                { "US", "0" },
                { "HW", "0" },
                { "RP", "0" },
                { "LO", "frFR" }, // Country
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
