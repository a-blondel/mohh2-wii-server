package com.ea.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocketRequest {

    private String id;
    private int length;
    private String content;

}
