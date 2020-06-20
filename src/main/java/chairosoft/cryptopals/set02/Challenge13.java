package chairosoft.cryptopals.set02;

import javax.crypto.Cipher;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/13
 */
public class Challenge13 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromUtf8(args[0]);
        byte[] encryptedAdminProfile = createEncryptedAdminProfile(key);
        Map<String, String> decryptedProfile = decryptProfile(encryptedAdminProfile, key);
        String decryptedProfileText = formatKv(decryptedProfile);
        System.out.println(decryptedProfileText);
    }
    
    
    ////// Static Methods //////
    public static Map<String, String> parseKv(String text) {
        Map<String, String> result = new HashMap<>();
        String[] entryTexts = text.split("&", 0);
        for (String entryText : entryTexts) {
            String[] kv = entryText.split("=", 2);
            String key = kv.length > 0 ? kv[0] : null;
            String value = kv.length > 1 ? kv[1] : "";
            if (key != null) {
                result.put(key, value);
            }
        }
        return result;
    }
    
    public static String formatKv(Map<String, String> kvMap) {
        return kvMap
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> sanitizeKvText(e.getKey()) + "=" + sanitizeKvText(e.getValue()))
            .collect(Collectors.joining("&"));
    }
    
    public static String sanitizeKvText(String input) {
        return input.replaceAll("[&=]", "");
    }
    
    public static String profileFor(String emailAddress) {
        Map<String, String> profile = new HashMap<>();
        String sanitizedEmailAddress = sanitizeKvText(emailAddress);
        profile.put("email", sanitizedEmailAddress);
        profile.put("uid", "#10");
        profile.put("role", "user");
        return formatKv(profile);
    }
    
    public static byte[] encryptProfileFor(String emailAddress, byte[] key) throws Exception {
        String profileText = profileFor(emailAddress);
        byte[] profileBytes = fromUtf8(profileText);
        return applyCipher(
            "AES",
            "ECB",
            "PKCS5Padding",
            Cipher.ENCRYPT_MODE,
            key,
            profileBytes
        );
    }
    
    public static Map<String, String> decryptProfile(byte[] encryptedProfileBytes, byte[] key) throws Exception {
        byte[] profileBytes = applyCipher(
            "AES",
            "ECB",
            "PKCS5Padding",
            Cipher.DECRYPT_MODE,
            key,
            encryptedProfileBytes
        );
        String profileText = toUtf8(profileBytes);
        return parseKv(profileText);
    }
    
    public static byte[] createEncryptedAdminProfile(byte[] key) throws Exception {
        OracleFunction13 oracleFn = ea -> encryptProfileFor(ea, key);
        byte[] baseline = oracleFn.apply("");
        System.err.printf("Baseline: %-96s  %s\n", toHex(baseline), formatKv(decryptProfile(baseline, key)));
        byte[] singleA = fromUtf8("A");
        for (int i = 1; i < 16; ++i) {
            byte[] inputBytes = extendRepeat(singleA, i);
            String inputText = toUtf8(inputBytes);
            byte[] output = oracleFn.apply(inputText);
            System.err.printf("Out #%3s: %-96s  %s\n", i, toHex(output), formatKv(decryptProfile(output, key)));
        }
        for (int a = 0; a < 26; ++a) {
            byte b = (byte)(65 + a);
            byte[] inputBytes = extendRepeat(singleA, 10);
            inputBytes[inputBytes.length - 1] = b;
            String inputText = toUtf8(inputBytes);
            byte[] output = oracleFn.apply(inputText);
            System.err.printf("Out @%3s: %-96s  %s\n", b, toHex(output), formatKv(decryptProfile(output, key)));
        }
        // TODO: actually return something correct
        return baseline;
    }
    
    
    ////// Static Inner Classes //////
    @FunctionalInterface
    public interface OracleFunction13 {
        byte[] apply(String emailAddress) throws Exception;
    }
    
}
