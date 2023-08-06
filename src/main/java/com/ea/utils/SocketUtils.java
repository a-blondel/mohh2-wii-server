package com.ea.utils;

import org.springframework.stereotype.Component;

@Component
public class SocketUtils {

    public String getValueFromSocket(String data, String key) {
        String result = null;
        String[] entries = data.split("\\R");
        for (String entry : entries) {
            String[] parts = entry.trim().split("=");
            if(key.equals(parts[0])) {
                result = parts[1];
                break;
            }
        }
        return result;
    }

}
