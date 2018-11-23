package com.activitytracker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class is used to securely store sensitive string-like information such as user passwords.
 */
public class SecureString {

    /**
     * The encrypted string.
     */
    private String secureString;
    /**
     * The salt that was used to encrypt the plain text string.
     */
    private byte[] salt;

    /**
     * The SecureString() constructor takes as an argument a plain text string, encrypts it, and stores the encrypted
     * string in the variable SecureString#secureString.
     *
     * Salt is generated using SecureString#generateSalt().
     *
     * @param plaintext The string to be encrypted. May contain sensitive information.
     */
    SecureString(final String plaintext) {

        try {
            this.salt = generateSalt();
        }
        catch (final NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        this.secureString = generateSecureString(plaintext, this.salt);

    }

    /**
     * The SecureString() constructor takes as an argument a plain text string and a previously-generated salt,
     * encrypts the plain text string with the provided salt, and stores the encrypted string in the variable
     * SecureString#secureString.
     *
     * @param plaintext The string to be encrypted. May contain sensitive information.
     * @param salt Salt that is used to encrypt \em plaintext. This parameter is used whenever we wish to encrypt using
     *             a previously-generated salt for the purpose of encrypted string comparison.
     */
    SecureString(final String plaintext, final byte[] salt) {

        this.salt = salt;
        this.secureString = generateSecureString(plaintext, salt);

    }

    /**
     * Compares the secure string to the \em other parameter for equality.
     *
     * This method will likely be used to authenticate a user from a password hash existing in the database.
     *
     * @param other A (previously encrypted) string with with we compare SecureString#secureString.
     *
     * @return This method returns True if the hashes of both strings are the same, and False otherwise.
     */
    public boolean equalString(final String other) {

        return this.secureString.equals(other);

    }

    // Generate salt for encryption

    /**
     * This method generates salt for encryption of a plain text string.
     *
     * @return Returns a byte array of length sixteen (16) containing the encryption salt.
     *
     * @throws NoSuchAlgorithmException Required as \em SecureRandom.getInstace() may throw this exception and we would
     *                                  like the invoking method to decide how to handle it rather than catching and
     *                                  dismissing it here.
     */
    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * Encrypt string and return secure version.
     *
     * Due to the importance of securely storing passwords, a "tried and true" method for encrypting passwords found at
     * <a href="https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/"
     * >this link</a> has been used.
     *
     * @param strToSecure The plain text string we wish to encrypt.
     * @param salt The salt with which we will encrypt \em strToSecure.
     *
     * @return This private method returns the encrypted string to the SecureString() constructor.
     */
    private String generateSecureString(final String strToSecure, final byte[] salt) {
        String generatedPassword = null;
        try {
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

    /**
     * @return Returns the byte array-type salt used to encrypt the text given to the object's constructor.
     */
    public byte[] getSalt() {
        return this.salt;
    }

    /**
     * Overrided method to return the object as a Java String.
     *
     * The encrypted string will be returned, though it should be noted for completeness that this is not a full
     * representation of the object since the salt is crucial in arriving at SecureString#secureString being returned.
     *
     * @return Returns the encrypted string.
     */
    @Override
    public String toString() {
        return this.secureString;
    }
}
