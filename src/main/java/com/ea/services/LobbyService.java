package com.ea.services;

import com.ea.dto.SessionData;
import com.ea.dto.SocketData;
import com.ea.entities.LobbyEntity;
import com.ea.entities.PersonaEntity;
import com.ea.mappers.SocketMapper;
import com.ea.repositories.LobbyRepository;
import com.ea.steps.SocketWriter;
import com.ea.utils.SocketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LobbyService {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private SocketUtils socketUtils;

    @Autowired
    private SocketMapper socketMapper;

    @Autowired
    private SocketWriter socketWriter;

    /**
     * Lobby count
     * @param socket
     * @param socketData
     */
    public void gsea(Socket socket, SocketData socketData) {
        List<LobbyEntity> lobbyEntities = lobbyRepository.findByEndTime(null);

        Map<String, String> content = Stream.of(new String[][] {
                { "COUNT", String.valueOf(lobbyEntities.size()) },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        socketData.setOutputData(content);
        socketWriter.write(socket, socketData);

        gam(socket, lobbyEntities);
    }

    /**
     * List lobbies
     * @param socket
     */
    public void gam(Socket socket, List<LobbyEntity> lobbyEntities) {
        List<Map<String, String>> lobbies = new ArrayList<>();

        for(LobbyEntity lobbyEntity : lobbyEntities) {
            lobbies.add(Stream.of(new String[][] {
                    { "IDENT", String.valueOf(lobbyEntity.getId()) },
                    { "NAME", lobbyEntity.getName() },
                    { "PARAMS", lobbyEntity.getParams() },
                    { "SYSFLAGS", lobbyEntity.getSysflags() },
                    { "COUNT", String.valueOf(lobbyEntity.getLobbyPersonas().stream().filter(lp -> lp.isInLobby()).count() + 1) },
                    { "MAXSIZE", String.valueOf(lobbyEntity.getMaxsize()) },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1])));
        }

        for (Map<String, String> lobby : lobbies) {
            SocketData socketData = new SocketData("+gam", null, lobby);
            socketWriter.write(socket, socketData);
        }
    }

    /**
     * Join lobby
     * @param socket
     * @param socketData
     */
    public void gjoi(Socket socket, SocketData socketData) {
        socketWriter.write(socket, socketData);
        String ident = socketUtils.getValueFromSocket(socketData.getInputMessage(), "IDENT");
        ses(socket, Long.valueOf(ident));
    }

    /**
     * Create lobby
     * @param socket
     * @param socketData
     */
    public void gpsc(Socket socket, SocketData socketData) {
        socketWriter.write(socket, socketData);
        LobbyEntity lobbyEntity = socketMapper.toLobbyEntityForCreation(socketData.getInputMessage());
        lobbyEntity.setStartTime(Timestamp.from(Instant.now()));
        lobbyRepository.save(lobbyEntity);
        ses(socket, lobbyEntity.getId());
    }

    /**
     * Lobby info based on IDENT
     * @param socket
     */
    public void ses(Socket socket, Long lobbyId) {
        Optional<LobbyEntity> lobbyEntityOpt = lobbyRepository.findById(lobbyId);
        if(lobbyEntityOpt.isPresent()) {
            LobbyEntity lobbyEntity = lobbyEntityOpt.get();
            Map<String, String> content = Stream.of(new String[][] {
                    { "IDENT", String.valueOf(lobbyEntity.getId()) },
                    { "NAME", lobbyEntity.getName() },
                    { "HOST", sessionData.getCurrentPersonna().getPers() },
                    { "GPSHOST", sessionData.getCurrentPersonna().getPers() },
                    { "PARAMS", lobbyEntity.getParams() },
                    // { "PLATPARAMS", "0" },  // ???
                    { "ROOM", "0" },
                    { "CUSTFLAGS", "0" },
                    { "SYSFLAGS", lobbyEntity.getSysflags() },
                    { "COUNT", String.valueOf(lobbyEntity.getLobbyPersonas().stream().filter(lp -> lp.isInLobby()).count() + 1) },
                    { "PRIV", "0" },
                    { "MINSIZE", String.valueOf(lobbyEntity.getMinsize()) },
                    { "MAXSIZE", String.valueOf(lobbyEntity.getMaxsize()) },
                    { "NUMPART", "1" },
                    { "SEED", "012345" }, // random seed
                    { "WHEN", "2009.2.8-9:44:15" },
                    { "GAMEPORT", "21173" },
                    { "VOIPPORT", "21173" },
                    // { "GAMEMODE", "0" }, // ???
                    // { "AUTH", "0" }, // ???

                    // loop 0x80022058 only if COUNT>=0
                    { "OPID0", "1" }, // OPID%d
                    { "OPPO0", sessionData.getCurrentPersonna().getPers() }, // OPPO%d
                    { "ADDR0", socket.getInetAddress().getHostName() }, // ADDR%d
                    { "LADDR0", socket.getInetAddress().getHostName() }, // LADDR%d
                    { "MADDR0", "$0017ab8f4451" }, // MADDR%d
                    // { "OPPART0", "0" }, // OPPART%d
                    // { "OPPARAM0", "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC" }, // OPPARAM%d
                    // { "OPFLAGS0", "0" }, // OPFLAGS%d
                    // { "PRES0", "0" }, // PRES%d ???

                    // another loop 0x8002225C only if NUMPART>=0
                    { "PARTSIZE0", "17" }, // PARTSIZE%d
                    { "PARTPARAMS0", "0" }, // PARTPARAMS%d
                    { "SELF", sessionData.getCurrentPersonna().getPers() }, // PARTPARAMS%d

                    // { "SESS", "0" }, %s-%s-%08x 0--498ea96f
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

            socketWriter.write(socket, new SocketData("+ses", null, content));

        }
    }

    /**
     * Lobby details (current opponents, ...)
     * @param socket
     */
    public void gget(Socket socket, SocketData socketData) {
        String ident = socketUtils.getValueFromSocket(socketData.getInputMessage(), "IDENT");
        Optional<LobbyEntity> lobbyEntityOpt = lobbyRepository.findById(Long.valueOf(ident));
        if(lobbyEntityOpt.isPresent()) {
            LobbyEntity lobbyEntity = lobbyEntityOpt.get();
            Map<String, String> content = Stream.of(new String[][] {
                    { "IDENT", String.valueOf(lobbyEntity.getId()) },
                    { "WHEN", "2003.12.8 15:52:54" },
                    { "NAME", lobbyEntity.getName() },
                    { "HOST", "IDK" },
                    { "PARAMS", lobbyEntity.getParams() },
                    { "ROOM", "1" },
                    { "MAXSIZE", String.valueOf(lobbyEntity.getMaxsize()) },
                    { "MINSIZE", String.valueOf(lobbyEntity.getMinsize()) },
                    { "COUNT", String.valueOf(lobbyEntity.getLobbyPersonas().stream().filter(lp -> lp.isInLobby()).count() + 1) },
                    { "USERFLAGS", "0" },
                    { "SYSFLAGS", lobbyEntity.getSysflags() },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

            int[] idx = { 0 };
            lobbyEntity.getLobbyPersonas().stream().filter(lp -> lp.isInLobby()).forEach(lobbyPersonaEntity -> {
                        PersonaEntity personaEntity = lobbyPersonaEntity.getPersona();
                        content.put("OPID" + idx[0], String.valueOf(personaEntity.getId()));
                        content.put("OPPO" + idx[0], personaEntity.getPers());
                        content.put("ADDR" + idx[0], socket.getInetAddress().getHostName());
                        content.put("LADDR" + idx[0], socket.getInetAddress().getHostName());
                        content.put("MADDR" + idx[0], "$0017ab8f4451");
                        content.put("OPPART" + idx[0], "0");
                        content.put("OPPARAM" + idx[0], "AAAAAAAAAAAAAAAAAAAAAQBuDCgAAAAC");
                        content.put("OPFLAGS" + idx[0], "0");
                        content.put("PRES" + idx[0], "0");
                        idx[0]++;
                    }
            );

            socketWriter.write(socket, new SocketData("gget", null, content));
        }
    }

}
