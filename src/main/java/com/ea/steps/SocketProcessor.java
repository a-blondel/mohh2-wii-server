package com.ea.steps;

import com.ea.models.SocketData;
import com.ea.services.AuthService;
import com.ea.services.LobbyService;
import com.ea.services.PlayerService;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class SocketProcessor {

    /**
     * Prepares the output message based on request type,
     * then calls the writer
     * @param socket the socket to give to the writer
     * @param socketData the object to process
     */
    public static void process(Socket socket, SocketData socketData) {
        switch (socketData.getIdMessage()) {
            case ("@tic"), ("~png"):
                break;
            case ("@dir"):
                AuthService.sendDir(socket, socketData);
                break;
            case ("addr"):
                AuthService.sendAddr(socket, socketData);
                break;
            case ("skey"):
                AuthService.sendSkey(socket, socketData);
                break;
            case ("news"):
                AuthService.sendNews(socket, socketData);
                break;
            case ("sele"):
                PlayerService.sendSele(socket, socketData);
                break;
            case ("auth"):
                PlayerService.sendAuth(socket, socketData);
                break;
            case ("pers"):
                PlayerService.sendPers(socket, socketData);
                break;
            case ("llvl"):
                PlayerService.sendLlvl(socket, socketData);
                break;
            case ("gsea"):
                LobbyService.sendGsea(socket, socketData);
                break;
            case ("gjoi"):
                LobbyService.sendGjoi(socket, socketData);
                break;
            case ("gpsc"):
                LobbyService.sendGspc(socket, socketData);
                break;
            default:
                log.info("Unsupported operation: {}", socketData.getIdMessage());
                SocketWriter.write(socket, socketData);
                break;
        }
    }

}
