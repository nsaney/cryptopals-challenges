package chairosoft.cryptopals.set02;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/16
 */
public class Challenge16 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromBase64Text(args[0]);
        byte[] iv = fromBase64Text(args[1]);
        EncryptFunction16 encryptFn = data -> encryptUserData(data, key, iv);
        DecryptFunction16 decryptFn = enc -> Challenge10.decryptAesCbc(enc, key, iv);
        byte[] encryptedAdminProfile = createEncryptedAdminProfile(encryptFn, decryptFn);
        boolean isAdmin = isAdminProfile(encryptedAdminProfile, key, iv);
        System.out.println("profile = " + toBlockedHex(iv.length, encryptedAdminProfile));
        System.out.println("isAdmin = " + isAdmin);
    }
    
    ////// Constants //////
    private static final byte SEMICOLON = ';';
    private static final String SEMICOLON_TEXT = toUtf8(SEMICOLON);
    private static final byte[] ESCAPED_SEMICOLON = fromUtf8("%3b");
    private static final byte EQUAL_SIGN = '=';
    private static final String EQUAL_SIGN_TEXT = toUtf8(EQUAL_SIGN);
    private static final byte[] ESCAPED_EQUAL_SIGN = fromUtf8("%3d");
    private static final byte[] PREFIX = fromUtf8("comment1=cooking%20MCs;userdata=");
    private static final byte[] SUFFIX = fromUtf8(";comment2=%20like%20a%20pound%20of%20bacon");
    private static final String ADMIN_TEXT = "admin";
    private static final String TRUE_TEXT = "true";
    
    
    ////// Static Methods //////
    public static byte[] encryptUserData(byte[] userData, byte[] key, byte[] iv) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte b : userData) {
            switch (b) {
                case SEMICOLON: baos.write(ESCAPED_SEMICOLON); break;
                case EQUAL_SIGN: baos.write(ESCAPED_EQUAL_SIGN); break;
                default: baos.write(b);
            }
        }
        byte[] escapedUserData = baos.toByteArray();
        byte[] wrappedUserData = concatenate(concatenate(PREFIX, escapedUserData), SUFFIX);
        return Challenge10.encryptAesCbc(wrappedUserData, key, iv);
    }
    
    public static boolean isAdminProfile(byte[] encryptedUserData, byte[] key, byte[] iv) throws GeneralSecurityException {
        byte[] wrappedUserData = Challenge10.decryptAesCbc(encryptedUserData, key, iv);
        String wrappedUserDataText = toUtf8(wrappedUserData);
        String[] entries = wrappedUserDataText.split(Pattern.quote(SEMICOLON_TEXT));
        for (String entry : entries) {
            String[] entryParts = entry.split(Pattern.quote(EQUAL_SIGN_TEXT));
            String entryKey = entryParts.length > 0 ? entryParts[0] : null;
            String entryValue = entryParts.length > 1 ? entryParts[1] : null;
            if (ADMIN_TEXT.equals(entryKey) && TRUE_TEXT.equals(entryValue)) {
                return true;
            }
        }
        return false;
    }
    
    public static byte[] createEncryptedAdminProfile(EncryptFunction16 encryptFn, DecryptFunction16 decryptFn) throws Exception {
        int blockSize = 16;
        byte[] empty = new byte[0];
        byte[] enc = encryptFn.encrypt(empty);
        System.err.println("Enc: " + toBlockedHex(blockSize, enc));
        byte[] dec = decryptFn.decrypt(enc);
        System.err.println("Dec: " + toBlockedHex(blockSize, dec));
        System.err.println("===: " + escapingNewlines(toDisplayableText(dec)));
        byte[] mod = Arrays.copyOf(enc, enc.length);
        String targetDataText = ";admin=true;";
        byte[] targetData = fromUtf8(targetDataText);
        for (int i = 0; i < targetData.length; ++i) {
            int j = i + blockSize;
            mod[i] ^= dec[j];
            mod[i] ^= targetData[i];
        }
        System.err.println("Mod: " + toBlockedHex(blockSize, mod));
        byte[] dmd = decryptFn.decrypt(mod);
        System.err.println("Dmd: " + toBlockedHex(blockSize, dmd));
        System.err.println("===: " + escapingNewlines(toDisplayableText(dmd)));
        return mod;
    }
    
    
    ////// Static Inner Classes //////
    @FunctionalInterface
    public interface EncryptFunction16 {
        byte[] encrypt(byte[] data) throws IOException, GeneralSecurityException;
    }
    
    @FunctionalInterface
    public interface DecryptFunction16 {
        byte[] decrypt(byte[] data) throws IOException, GeneralSecurityException;
    }
    
}
