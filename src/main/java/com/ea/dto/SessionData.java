package com.ea.dto;

import com.ea.entities.AccountEntity;
import com.ea.entities.PersonaEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SessionData {

    private AccountEntity currentAccount;
    private PersonaEntity currentPersonna;

}
