package com.ea.steps;

import com.ea.dto.SocketData;
import com.ea.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Slf4j
@Component
public class SocketProcessor {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private SocketWriter socketWriter;

    /**
     * Dispatch to appropriate service based on request type
     * @param socket the socket to handle
     * @param socketData the object to process
     */
    public void process(Socket socket, SocketData socketData) {
        switch (socketData.getIdMessage()) {
            case ("@tic"), ("~png"):
                break;
            case ("@dir"):
                authService.dir(socket, socketData);
                break;
            case ("addr"):
                authService.addr(socket, socketData);
                break;
            case ("skey"):
                authService.skey(socket, socketData);
                break;
            case ("news"):
                authService.news(socket, socketData);
                break;
            case ("sele"):
                authService.sele(socket, socketData);
                break;
            case ("acct"):
                accountService.acct(socket, socketData);
                break;
            case ("edit"):
                accountService.edit(socket, socketData);
                break;
            case ("auth"):
                accountService.auth(socket, socketData);
                break;
            case ("cper"):
                personaService.cper(socket, socketData);
                break;
            case ("pers"):
                personaService.pers(socket, socketData);
                break;
            case ("dper"):
                personaService.dper(socket, socketData);
                break;
            case ("llvl"):
                personaService.llvl(socket, socketData);
                break;
            case ("cate"):
                statsService.cate(socket, socketData);
                break;
            case ("gsea"):
                lobbyService.gsea(socket, socketData);
                break;
            case ("gget"):
                lobbyService.gget(socket);
                break;
            case ("gjoi"):
                lobbyService.gjoi(socket, socketData);
                break;
            case ("gpsc"):
                lobbyService.gpsc(socket, socketData);
                break;
            default:
                log.info("Unsupported operation: {}", socketData.getIdMessage());
                socketWriter.write(socket, socketData);
                break;
        }
    }

}
