package com.ea.utils;

import java.util.HexFormat;
import java.util.zip.CRC32;

public class SocketUtils {

    public static String getValueFromSocket(String data, String key) {
        String result = null;
        String[] entries = data.split("\\R");
        for (String entry : entries) {
            String[] parts = entry.trim().split("=");
            if(key.equals(parts[0])) {
                if (parts.length > 1) {
                    result = parts[1];
                }
                break;
            }
        }
        return result;
    }

    /**
     * Calculate the CRC32 checksum
     * E.g. : value = 1d0000001c000000fcff1f00e0ff1f00e0ff1f00e0ff1f0400, return = 21421344
     * @param value
     * @return
     */
    public String calcCRC32Checksum(String value) {
        byte[] bytes = parseHexString(value);
        CRC32 crc = new CRC32();
        crc.update(bytes);
        // The int cast is required to only have one Word instead of two with the long type
        // Still unsure if int cast is safe here... Could also use long and split the result at first Word
        return Integer.toHexString(swapEndian((int) crc.getValue()));
    }

    /**
     * Swap endianness (little endian to big endian, and the opposite)
     * E.g. : 21421344 <-> 44134221
     * @param value
     * @return
     */
    public static int swapEndian(int value) {
        return Integer.reverseBytes(value);
    }

    /**
     * Format byte array to hex string
     * @param bytes
     * @return
     */
    public static String formatHexString(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

    /**
     * Parse hex string to byte array
     * @param hexString
     * @return
     */
    public static byte[] parseHexString(String hexString) {
        return HexFormat.of().parseHex(hexString);
    }

    /**
     * Format an int into a Word (32 bits - 4 bytes - 8 hex digits)
     * E.g. : value = 2, return = 00000002
     * @param value
     * @return
     */
    public static String formatIntToWord(int value) {
        return String.format("%08X", value);
    }

}
