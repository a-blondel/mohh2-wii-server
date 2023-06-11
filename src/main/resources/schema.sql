CREATE TABLE ACCOUNT {
    ID numeric PRIMARY KEY NOT NULL,
    MAIL varchar(255) NOT NULL;
    PASS varchar(255) NOT NULL,
    LOC varchar(10) NOT NULL,
    NAME varchar(255) NOT NULL,
    CREATED_ON timestamp NOT NULL;
}

CREATE TABLE PLAYER {
    ID numeric PRIMARY KEY NOT NULL,
    ACCOUNT_ID numeric NOT NULL;
    NAME varchar(255) NOT NULL,
    KILLS numeric NOT NULL DEFAULT 0,
    DEATHS numeric NOT NULL DEFAULT 0,
    CREATED_ON timestamp NOT NULL,
    FOREIGN KEY ACCOUNT_ID REFERENCES ACCOUNT.ID;
}

-- Not quite sure yet --
CREATE TABLE LOGIN_HISTORY {
    ID numeric PRIMARY KEY NOT NULL,
    IP varchar(255) NOT NULL,
    ACCOUNT_ID numeric NULL,
    FOREIGN KEY ACCOUNT_ID REFERENCES ACCOUNT.ID;
}

-- Not quite sure yet --
CREATE TABLE LOBBY {
    ID numeric PRIMARY KEY NOT NULL,
    MODE varchar(10) NOT NULL,
    MAP varchar(10) NOT NULL,
    -- other params --
    START_TIME timestamp NOT NULL,
    END_TIME timestamp NULL;
}

-- Not quite sure yet --
CREATE TABLE LOBBY_REPORT {
    ID numeric PRIMARY KEY NOT NULL,
    LOBBY_ID numeric NOT NULL,
    PLAYER_ID numeric NOT NULL,
    KILLS numeric NOT NULL DEFAULT 0,
    DEATHS numeric NOT NULL DEFAULT 0,
    START_TIME timestamp NOT NULL,
    END_TIME timestamp NULL,
    FOREIGN KEY LOBBY_ID REFERENCES LOBBY.ID,
    FOREIGN KEY PLAYER_ID REFERENCES PLAYER.ID;
}