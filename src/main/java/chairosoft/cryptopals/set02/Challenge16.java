package chairosoft.cryptopals.set02;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
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
        byte[] encryptedAdminProfile = createEncryptedAdminProfile(encryptFn);
        byte[] profile = Challenge10.decryptAesCbc(encryptedAdminProfile, key, iv);
        boolean isAdmin = isAdminProfile(encryptedAdminProfile, key, iv);
        System.err.println("encrypted = " + toBlockedHex(iv.length, encryptedAdminProfile));
        System.err.println("decrypted = " + toBlockedHex(iv.length, profile));
        System.err.println("......... = " + escapingNewlines(toDisplayableText(profile)));
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
    public static byte[] encryptUserData(byte[] userData, byte[] key, byte[] iv) throws GeneralSecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (byte b : userData) {
            try {
                switch (b) {
                    case SEMICOLON: baos.write(ESCAPED_SEMICOLON); break;
                    case EQUAL_SIGN: baos.write(ESCAPED_EQUAL_SIGN); break;
                    default: baos.write(b);
                }
            }
            catch (IOException ex) {
                throw new GeneralSecurityException(ex);
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
    
    public static byte[] createEncryptedAdminProfile(EncryptFunction16 encryptFn) throws Exception {
        // get block size
        Challenge12.OracleFunction12 oracleFn = encryptFn::encrypt;
        int firstBlockBarrier = Challenge12.countBytesUntilNewBlock(oracleFn, 0);
        int blockSize = Challenge12.countBytesUntilNewBlock(oracleFn, firstBlockBarrier);
        // get data offset
        byte[] previousOutput = encryptFn.encrypt(new byte[0]);
        byte[] singleA = fromUtf8("A");
        int overlap = 0;
        int dataOffset = -1;
        for (int i = 0; i <= blockSize; ++i) {
            byte[] input = extendRepeat(singleA, i + 1);
            byte[] output = encryptFn.encrypt(input);
            int previousOverlap = overlap;
            overlap = getInitialOverlappingBlockCount(blockSize, previousOutput, output);
            previousOutput = output;
            if (i > 0 && previousOverlap != overlap) {
                dataOffset = (overlap * blockSize) - i;
                break;
            }
        }
        if (dataOffset < 0) {
            throw new IllegalStateException("Unable to determine data offset.");
        }
        System.err.println("data offset: " + dataOffset);
        // manufacture encrypted content
        int gapToNextBlock = dataOffset % blockSize;
        byte[] sourceData = fromUtf8("XadminYtrueZ");
        byte[] sourceInput = new byte[gapToNextBlock + (2 * blockSize)];
        System.arraycopy(sourceData, 0, sourceInput, gapToNextBlock + blockSize, sourceData.length);
        byte[] sourceOutput = encryptFn.encrypt(sourceInput);
        byte[] targetOutput = Arrays.copyOf(sourceOutput, sourceOutput.length);
        int modOffset = dataOffset + gapToNextBlock;
        int modIndex01 = modOffset + firstIndexOf((byte)'X', sourceData);
        targetOutput[modIndex01] ^= 'X' ^ ';';
        int modIndex02 = modOffset + firstIndexOf((byte)'Y', sourceData);
        targetOutput[modIndex02] ^= 'Y' ^ '=';
        int modIndex03 = modOffset + firstIndexOf((byte)'Z', sourceData);
        targetOutput[modIndex03] ^= 'Z' ^ ';';
        return targetOutput;
    }
    
    
    ////// Static Inner Classes //////
    @FunctionalInterface
    public interface EncryptFunction16 {
        byte[] encrypt(byte[] data) throws GeneralSecurityException;
    }
    
    @FunctionalInterface
    public interface DecryptFunction16 {
        byte[] decrypt(byte[] data) throws GeneralSecurityException;
    }
    
}
