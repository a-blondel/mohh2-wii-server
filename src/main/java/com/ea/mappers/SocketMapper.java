package com.ea.mappers;

import com.ea.entities.AccountEntity;
import com.ea.entities.LobbyEntity;
import com.ea.utils.PasswordUtils;
import com.ea.utils.SocketUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring")
public abstract class SocketMapper {

    @Autowired
    protected SocketUtils socketUtils;

    @Autowired
    protected PasswordUtils passwordUtils;

    @BeanMapping(qualifiedByName = "LobbyEntityForCreation")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(socketUtils.getValueFromSocket(socket, \"NAME\"))")
    @Mapping(target = "params", expression = "java(socketUtils.getValueFromSocket(socket, \"PARAMS\"))")
    @Mapping(target = "sysflags", expression = "java(socketUtils.getValueFromSocket(socket, \"SYSFLAGS\"))")
    @Mapping(target = "pass", expression = "java(socketUtils.getValueFromSocket(socket, \"PASS\"))")
    @Mapping(target = "minsize", expression = "java(Integer.parseInt(socketUtils.getValueFromSocket(socket, \"MINSIZE\")))")
    @Mapping(target = "maxsize", expression = "java(Integer.parseInt(socketUtils.getValueFromSocket(socket, \"MAXSIZE\")))")
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "lobbyReports", ignore = true)
    public abstract LobbyEntity toLobbyEntityForCreation(String socket);

    @BeanMapping(qualifiedByName = "AccountEntityForCreation")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(socketUtils.getValueFromSocket(socket, \"NAME\"))")
    @Mapping(target = "pass", expression = "java(socketUtils.getValueFromSocket(socket, \"PASS\"))")
    @Mapping(target = "loc", expression = "java(socketUtils.getValueFromSocket(socket, \"LOC\"))")
    @Mapping(target = "mail", expression = "java(socketUtils.getValueFromSocket(socket, \"MAIL\"))")
    @Mapping(target = "born", expression = "java(socketUtils.getValueFromSocket(socket, \"BORN\"))")
    @Mapping(target = "zip", expression = "java(socketUtils.getValueFromSocket(socket, \"ZIP\"))")
    @Mapping(target = "gend", expression = "java(socketUtils.getValueFromSocket(socket, \"GEND\"))")
    @Mapping(target = "spam", expression = "java(socketUtils.getValueFromSocket(socket, \"SPAM\"))")
    @Mapping(target = "tos", expression = "java(Integer.parseInt(socketUtils.getValueFromSocket(socket, \"TOS\")))")
    @Mapping(target = "tick", expression = "java(socketUtils.getValueFromSocket(socket, \"TICK\"))")
    @Mapping(target = "gamecode", expression = "java(socketUtils.getValueFromSocket(socket, \"GAMECODE\"))")
    @Mapping(target = "vers", expression = "java(socketUtils.getValueFromSocket(socket, \"VERS\"))")
    @Mapping(target = "sku", expression = "java(socketUtils.getValueFromSocket(socket, \"SKU\"))")
    @Mapping(target = "slus", expression = "java(socketUtils.getValueFromSocket(socket, \"SLUS\"))")
    @Mapping(target = "sdkvers", expression = "java(socketUtils.getValueFromSocket(socket, \"SDKVERS\"))")
    @Mapping(target = "builddate", expression = "java(socketUtils.getValueFromSocket(socket, \"BUILDDATE\"))")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "personas", ignore = true)
    public abstract AccountEntity toAccountEntityForCreation(String socket);

    @Named("AccountEntityForCreation")
    @AfterMapping
    public void updateAccountEntityForCreation(@MappingTarget AccountEntity accountEntity) {
        String pass = accountEntity.getPass();
        // The game sends a tilde before the password
        if (pass.charAt(0) == '~') {
            pass = pass.substring(1);
        }
        accountEntity.setPass(passwordUtils.encode(pass));
        accountEntity.setCreatedOn(Timestamp.from(Instant.now()));
    }
    
}
