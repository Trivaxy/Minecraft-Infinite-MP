package net.minecraft.statistics;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {
    private String string;

    public MD5Hash(String hash) {
        this.string = hash;
    }

    public String hash(String string1) {
        try {
            String string2 = this.string + string1;
            MessageDigest mmsg = MessageDigest.getInstance("MD5");
            mmsg.update(string2.getBytes(), 0, string2.length());
            return new BigInteger(1, mmsg.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
