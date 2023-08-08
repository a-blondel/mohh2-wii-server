CREATE TABLE IF NOT EXISTS ACCOUNT (
    ID numeric AUTO_INCREMENT PRIMARY KEY NOT NULL,
    NAME varchar(255) NOT NULL,
    PASS varchar(255) NOT NULL,
    MAIL varchar(255) NOT NULL,
    LOC varchar(10) NOT NULL,
    BORN varchar(8) NOT NULL,
    ZIP varchar(20) NOT NULL,
    GEND varchar(1) NOT NULL,
    SPAM varchar(2) NOT NULL,
    TOS numeric NOT NULL,
    TICK varchar(255) NOT NULL,
    GAMECODE varchar(255) NOT NULL,
    VERS varchar(255) NOT NULL,
    SKU varchar(255) NOT NULL,
    SLUS varchar(20) NOT NULL,
    SDKVERS varchar(20) NOT NULL,
    BUILDDATE varchar(20) NOT NULL,
    CREATED_ON timestamp NOT NULL,
    UPDATED_ON timestamp NULL
);

CREATE TABLE IF NOT EXISTS PERSONA (
    ID numeric AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ACCOUNT_ID numeric NOT NULL,
    PERS varchar(255) NOT NULL,
    KILLS numeric NOT NULL DEFAULT 0,
    DEATHS numeric NOT NULL DEFAULT 0,
    CREATED_ON timestamp NOT NULL,
    DELETED_ON timestamp NULL,
    CONSTRAINT FK_PERSONA_ACCOUNT_ID FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ID)
);

CREATE TABLE IF NOT EXISTS CONNECTIONS (
    ID numeric AUTO_INCREMENT PRIMARY KEY NOT NULL,
    IP varchar(255) NOT NULL,
    ACCOUNT_ID numeric NULL,
    START_TIME timestamp NOT NULL,
    LAST_PING timestamp NOT NULL,
    END_TIME timestamp NULL,
    CONSTRAINT FK_CONNECTIONS_ACCOUNT_ID FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ID)
);

-- Not quite sure yet --
CREATE TABLE IF NOT EXISTS LOBBY (
    ID numeric AUTO_INCREMENT PRIMARY KEY NOT NULL,
    MODE varchar(10) NOT NULL,
    MAP varchar(10) NOT NULL,
    -- other params --
    START_TIME timestamp NOT NULL,
    END_TIME timestamp NULL
);

-- Not quite sure yet --
CREATE TABLE IF NOT EXISTS LOBBY_REPORT (
    ID numeric AUTO_INCREMENT PRIMARY KEY NOT NULL,
    LOBBY_ID numeric NOT NULL,
    PERSONA_ID numeric NOT NULL,
    KILLS numeric NOT NULL DEFAULT 0,
    DEATHS numeric NOT NULL DEFAULT 0,
    START_TIME timestamp NOT NULL,
    END_TIME timestamp NULL,
    CONSTRAINT FK_LOBBY_REPORT_LOBBY_ID FOREIGN KEY (LOBBY_ID) REFERENCES LOBBY(ID),
    CONSTRAINT FK_LOBBY_REPORT_PERSONA_ID FOREIGN KEY (PERSONA_ID) REFERENCES PERSONA(ID)
);
