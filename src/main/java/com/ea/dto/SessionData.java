package com.ea.dto;

import com.ea.entities.AccountEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SessionData {

    private AccountEntity currentAccount;

}
