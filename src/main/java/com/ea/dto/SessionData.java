package com.ea.dto;

import com.ea.entities.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


/**
 * Holds session data instead of checking into DB each time
 */
@Getter
@Setter
@NoArgsConstructor
public class SessionData {

    private AccountEntity currentAccount;
    private PersonaEntity currentPersonna;
    private PersonaConnectionEntity currentPersonaConnection;
    private LobbyEntity currentLobby;
    private LobbyReportEntity currentLobbyReport;

}
