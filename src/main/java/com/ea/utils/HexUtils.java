package com.ea.utils;

import java.io.UnsupportedEncodingException;

public final class HexUtils {

    /**
     * Convert a string to a hex string
     * @param text The string
     * @return The hex string
     */
    public static String stringToHex(String text) {
        StringBuilder hexString = new StringBuilder();
        for (char ch : text.toCharArray()) {
            hexString.append(Integer.toHexString(ch));
        }
        return hexString.toString();
    }

    /**
     * Convert a hex string to a string
     * @param hex The hex string
     * @return The string
     */
    public static String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    /**
     * Format a byte array as a hex dump
     * @param array The byte array
     * @return The hex dump
     */
    public static String formatHexDump(byte[] array) {
        final int width = 16;

        StringBuilder builder = new StringBuilder();

        for (int rowOffset = 0; rowOffset < array.length; rowOffset += width) {
            builder.append(String.format("%06d:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                if (rowOffset + index < array.length) {
                    builder.append(String.format("%02x ", array[rowOffset + index]));
                } else {
                    builder.append("   ");
                }
            }

            if (rowOffset < array.length) {
                int asciiWidth = Math.min(width, array.length - rowOffset);
                builder.append("  |  ");
                try {
                    //builder.append(new String(array, rowOffset, asciiWidth, "UTF-8").replaceAll("\r\n", " ").replaceAll("\n", " "));
                    builder.append(new String(array, rowOffset, asciiWidth, "UTF-8").replaceAll("[^\\x20-\\x7E]", "."));
                } catch (UnsupportedEncodingException e) {
                    //If UTF-8 isn't available as an encoding then what can we do?!
                    e.printStackTrace();
                }
            }

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }
}
