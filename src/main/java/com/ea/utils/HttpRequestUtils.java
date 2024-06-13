package com.ea.utils;

import com.ea.dto.HttpRequestData;

public class HttpRequestUtils {


    /**
     * Check if the packet is an HTTP packet
     * @param buffer the request buffer
     * @return boolean - true if the packet is an HTTP packet
     */
    public static boolean isHttpPacket(byte[] buffer) {
        String packetStart = new String(buffer, 0, Math.min(buffer.length, 10)).toUpperCase();
        return packetStart.startsWith("GET ") || packetStart.startsWith("POST ")
                || packetStart.startsWith("PUT ") || packetStart.startsWith("DELETE ")
                || packetStart.startsWith("HEAD ") || packetStart.startsWith("OPTIONS ")
                || packetStart.startsWith("PATCH ") || packetStart.startsWith("CONNECT ");
    }

    /**
     * Extracts the HTTP request from the buffer
     * @param buffer the buffer to extract from
     * @return the extracted HTTP request
     */
    public static HttpRequestData extractHttpRequest(byte[] buffer) {
        String requestLine = new String(buffer).split("\n")[0];
        String[] parts = requestLine.split(" ");

        HttpRequestData request = new HttpRequestData();
        request.setMethod(parts[0]);
        request.setUri(parts[1]);

        return request;
    }
}
