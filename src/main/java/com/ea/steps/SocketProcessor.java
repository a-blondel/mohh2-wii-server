package com.ea.steps;

import com.ea.dto.SocketData;
import com.ea.services.AccountService;
import com.ea.services.AuthService;
import com.ea.services.LobbyService;
import com.ea.services.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Slf4j
@Component
public class SocketProcessor {

    @Autowired
    AuthService authService;

    @Autowired
    AccountService accountService;

    @Autowired
    PlayerService playerService;

    @Autowired
    LobbyService lobbyService;

    @Autowired
    SocketWriter socketWriter;

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
                authService.sendDir(socket, socketData);
                break;
            case ("addr"):
                authService.sendAddr(socket, socketData);
                break;
            case ("skey"):
                authService.sendSkey(socket, socketData);
                break;
            case ("news"):
                authService.sendNews(socket, socketData);
                break;
            case ("sele"):
                authService.sendSele(socket, socketData);
                break;
            case ("acct"):
                accountService.sendAcct(socket, socketData);
                break;
            case ("auth"):
                accountService.sendAuth(socket, socketData);
                break;
            case ("pers"):
                playerService.sendPers(socket, socketData);
                break;
            case ("llvl"):
                playerService.sendLlvl(socket, socketData);
                break;
            case ("gsea"):
                lobbyService.sendGsea(socket, socketData);
                break;
            case ("gget"):
                lobbyService.sendGget(socket);
                break;
            case ("gjoi"):
                lobbyService.sendGjoi(socket, socketData);
                break;
            case ("gpsc"):
                lobbyService.sendGpsc(socket, socketData);
                break;
            default:
                log.info("Unsupported operation: {}", socketData.getIdMessage());
                socketWriter.write(socket, socketData);
                break;
        }
    }

}
