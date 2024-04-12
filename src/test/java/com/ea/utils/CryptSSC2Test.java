package com.ea.utils;

import org.junit.jupiter.api.Test;

import static com.ea.utils.CryptSSC2.cryptSSC2StringDecrypt;
import static com.ea.utils.CryptSSC2.cryptSSC2StringEncrypt;

class CryptSSC2Test {
    @Test
    void cryptSSC2Test() {
        byte[] skey = { 0x51, (byte)0xba, (byte)0x8a, (byte)0xee, 0x64, (byte)0xdd, (byte)0xfa, (byte)0xca, (byte)0xe5, (byte)0xba, (byte)0xef, (byte)0xa6, (byte)0xbf, 0x61, (byte)0xe0, 0x09 };
        byte[] buf = new byte[32];
        String str = "pass";
        System.arraycopy(str.getBytes(), 0, buf, 0, str.length());

        System.out.println("encoding string: " + new String(buf).trim());

        cryptSSC2StringEncrypt(buf, buf.length, buf, skey, skey.length, skey.length);
        System.out.print("encoded bytes: ");
        for (int x = 0; x < buf.length; ++x)
            System.out.printf("%02x ", buf[x]);
        System.out.println();

        cryptSSC2StringDecrypt(buf, buf.length, buf, skey, skey.length, skey.length);
        System.out.print("decoded bytes: ");
        for (int x = 0; x < buf.length; ++x)
            System.out.printf("%02x ", buf[x]);
        System.out.println();
        String decoded = new String(buf);
        int nullPos = decoded.indexOf('\0');
        if (nullPos != -1) {
            decoded = decoded.substring(0, nullPos);
        }
        System.out.println("decoded string: " + decoded);
    }


}
