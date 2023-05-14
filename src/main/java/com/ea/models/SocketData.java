package com.ea.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocketData {

    private String id;
    private String request;
    private String response;
    private int responseFlags;

}
