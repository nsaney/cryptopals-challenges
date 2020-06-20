package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.set01.Challenge08;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
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
        //
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
        profile.put("uid", "#" + sanitizedEmailAddress.hashCode());
        profile.put("role", "user");
        return formatKv(profile);
    }
    
    
    ////// Static Inner Classes //////
    
}
