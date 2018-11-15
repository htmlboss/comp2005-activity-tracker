package com.activitytracker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureString {

    String secureString;

    // Constructor takes plaintext string
    // Encrypts it and stores the encrypted version
    SecureString(final String plaintext) {

        this.secureString = generateSecureString(plaintext);

    }

    // Compare to String for equality
    // Likely used to authenticate a user from the DB\
    public boolean equalString(final String other) {

        return this.secureString.equals(other);

    }

    // Generate salt for encryption
    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    // Encrypt string and return secure version.
    //
    // Due to the importance of securely storing passwords, a "tried and true" method for encrypting passwords
    // found at https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    // has been used
    private static String generateSecureString(String strToSecure) {
        String generatedPassword = null;
        try {
            byte[] salt = getSalt();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] strBytes = md.digest(strToSecure.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < strBytes.length; i++) {
                sb.append(Integer.toString((strBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (final NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        return generatedPassword;
    }

    // Returns the encrypted string
    @Override
    public String toString() {

        return this.secureString;

    }
}
