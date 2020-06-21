package chairosoft.cryptopals.set02;

import javax.crypto.Cipher;
import java.util.*;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/13
 */
public class Challenge13 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromUtf8(args[0]);
        OracleFunction13 oracleFn = email -> encryptProfileFor(email, key);
        DecryptFunction13 decryptFn = in -> decryptProfile(in, key);
        byte[] encryptedAdminProfile = createEncryptedAdminProfile(oracleFn, decryptFn);
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
            if (key != null && !key.isEmpty()) {
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
    
    public static byte[] createEncryptedAdminProfile(OracleFunction13 oracleFn, DecryptFunction13 decryptFn) throws Exception {
        byte[] baseline = oracleFn.apply("");
        System.err.printf("Baseline: %s\n", debugText(baseline, decryptFn));
        byte[] singleA = fromUtf8("A");
        // find block size
        int minBlockSize = 8;
        int maxBlockSize = baseline.length;
        byte[] previousOutput = baseline;
        byte[] currentOutput = baseline;
        byte[] inputBytes = singleA;
        List<OverlapDetails> overlaps = null;
        for (int i = 1; i <= maxBlockSize; ++i) {
            inputBytes = extendRepeat(singleA, i);
            String inputText = toUtf8(inputBytes);
            currentOutput = oracleFn.apply(inputText);
            overlaps = getOverlaps(previousOutput, currentOutput);
            if (overlaps.stream().anyMatch(o -> o.length > minBlockSize)) {
                break;
            }
            previousOutput = currentOutput;
        }
        if (overlaps == null || overlaps.isEmpty()) {
            throw new IllegalStateException("Unable to determine block size for oracle.");
        }
        System.err.printf(
            "Block Overlaps: %s\n%s\n%s\n",
            overlaps,
            debugText(previousOutput, decryptFn),
            debugText(currentOutput, decryptFn)
        );
        int blockSize = overlaps.get(0).length;
        // generate unpadded output
        int baseSize = inputBytes.length + 1;
        int maxSize = baseSize + blockSize;
        byte[] boundarySplitUser = null;
        byte[] lastBlock = null;
        for (int i = baseSize; i < maxSize; ++i) {
            inputBytes = extendRepeat(singleA, i);
            String inputText = toUtf8(inputBytes);
            boundarySplitUser = oracleFn.apply(inputText);
            lastBlock = Arrays.copyOfRange(boundarySplitUser, boundarySplitUser.length - blockSize, boundarySplitUser.length);
            if (decryptFn.apply(lastBlock).containsKey("user")) {
                break;
            }
        }
        if (boundarySplitUser == null) {
            throw new IllegalStateException("Unable to force boundary 'role=|user'.");
        }
        debugAllBlockSuffixes("Boundary Split User", blockSize, boundarySplitUser, decryptFn);
        
        // TODO: actually return something correct
        return baseline;
    }
    
    public static String debugText(byte[] encryptedBytes, DecryptFunction13 decryptFn) throws Exception {
        return String.format("%s  %s", toHex(encryptedBytes), toDisplayableText(fromUtf8(formatKv(decryptFn.apply(encryptedBytes)))));
    }
    
    public static void debugAllBlockSuffixes(String name, int blockSize, byte[] encryptedBytes, DecryptFunction13 decryptFn) throws Exception {
        int blockCount = encryptedBytes.length / blockSize;
        for (int i = 1; i <= blockCount; ++i) {
            int suffixLength = i * blockSize;
            byte[] suffixBytes = Arrays.copyOfRange(encryptedBytes, encryptedBytes.length - suffixLength, encryptedBytes.length);
            System.err.printf("Last %s Blocks of %s: %s\n", i, name, debugText(suffixBytes, decryptFn));
        }
        if (blockCount > 2) {
            byte[] copyFirstTwice = Arrays.copyOf(encryptedBytes, encryptedBytes.length);
            System.arraycopy(copyFirstTwice, 0, copyFirstTwice, blockSize, blockSize);
            System.err.printf("CopyCopy of %s     : %s\n", name, debugText(copyFirstTwice, decryptFn));
        }
    }
    
    
    ////// Static Inner Classes //////
    @FunctionalInterface
    public interface OracleFunction13 {
        byte[] apply(String emailAddress) throws Exception;
    }
    
    @FunctionalInterface
    public interface DecryptFunction13 {
        Map<String, String> apply(byte[] input) throws Exception;
    }
    
}
