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
        profile.put("uid", "10");
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
        System.err.printf("Baseline: %s\n", debugText(-1, baseline, decryptFn));
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
            debugText(-1, previousOutput, decryptFn),
            debugText(-1, currentOutput, decryptFn)
        );
        int blockSize = overlaps.get(0).length;
        // force boundary split '...role='|'user...'
        System.err.println("Forcing boundary split: '...role='|'user...'");
        String targetCurrentValue = "user";
        int baseSize = inputBytes.length + 1;
        int maxSize = baseSize + blockSize + 1;
        byte[] outputWithTargetSplit = null;
        int blockSplitIndex = -1;
        for (int i = baseSize; i < maxSize; ++i) {
            inputBytes = extendRepeat(singleA, i);
            String inputText = toUtf8(inputBytes);
            outputWithTargetSplit = oracleFn.apply(inputText);
            int blockCount = outputWithTargetSplit.length / blockSize;
            for (blockSplitIndex = 0; blockSplitIndex < blockCount; ++blockSplitIndex) {
                int j = blockSplitIndex * blockSize;
                byte[] block = Arrays.copyOfRange(outputWithTargetSplit, j, j + blockSize);
                byte[] blockWithSuffix = appendBlocks(blockSize, block, outputWithTargetSplit, blockCount - 1, 1);
                if (decryptFn.apply(blockWithSuffix).containsKey(targetCurrentValue)) {
                    i = maxSize;
                    break;
                }
            }
            if (i < maxSize) {
                outputWithTargetSplit = null;
            }
        }
        if (outputWithTargetSplit == null || blockSplitIndex < 0) {
            throw new IllegalStateException("Unable to force boundary: '...role='|'user...'");
        }
        System.err.println("Target Split at block #" + (blockSplitIndex + 1));
        debugAllBlockSuffixes("Output With Target Split", blockSize, outputWithTargetSplit, decryptFn);
        // force boundary split '...'|'admin...'
        System.err.println("Forcing boundary split: '...'|'admin...'");
        String targetReplacementValueText = "admin";
        byte[] targetReplacementValue = fromUtf8(targetReplacementValueText);
        baseSize = targetReplacementValue.length;
        maxSize = baseSize + blockSize + 1;
        byte[] blockWithReplacement = null;
        for (int i = baseSize; i < maxSize; ++i) {
            inputBytes = extendRepeat(singleA, i);
            System.arraycopy(
                targetReplacementValue,
                0,
                inputBytes,
                inputBytes.length - targetReplacementValue.length,
                targetReplacementValue.length
            );
            String inputText = toUtf8(inputBytes);
            byte[] outputBytes = oracleFn.apply(inputText);
            int blockCount = outputBytes.length / blockSize;
            for (int replacementBlockIndex = 0; replacementBlockIndex < blockCount; ++replacementBlockIndex) {
                int j = replacementBlockIndex * blockSize;
                blockWithReplacement = Arrays.copyOfRange(outputBytes, j, j + blockSize);
                byte[] blockWithSuffix = appendBlocks(blockSize, blockWithReplacement, outputBytes, blockCount - 1, 1);
                if (decryptFn.apply(blockWithSuffix).containsKey(targetReplacementValueText)) {
                    debugAllBlockSuffixes("With Replacement", blockSize, blockWithSuffix, decryptFn);
                    i = maxSize;
                    break;
                }
                blockWithReplacement = null;
            }
        }
        if (blockWithReplacement == null) {
            throw new IllegalStateException("Unable to force boundary: '...'|'admin...'");
        }
        // [admin&role=user&|uid=#10]
        // make replacement
        System.err.println("Combining to get result...");
        int blockCount = outputWithTargetSplit.length / blockSize;
        byte[] result = appendBlocks(blockSize, outputWithTargetSplit, outputWithTargetSplit, blockCount - 1, 1);
        copyBlocks(blockSize, blockWithReplacement, 0, result, blockSplitIndex, 1);
        System.err.printf(
            "%10s: %s\n%10s: %s\n%10s: %s\n",
            "owts", toBlockedHex(blockSize, outputWithTargetSplit),
            "bwr", toBlockedHex(blockSize, blockWithReplacement),
            "result", toBlockedHex(blockSize, result)
        );
        debugAllBlockSuffixes("Result", blockSize, result, decryptFn);
        return result;
    }
    
    public static String debugText(int blockSize, byte[] encryptedBytes, DecryptFunction13 decryptFn) throws Exception {
        return String.format(
            "%s  %s",
            toBlockedHex(blockSize, encryptedBytes),
            toDisplayableText(fromUtf8(formatKv(decryptFn.apply(encryptedBytes))))
        );
    }
    
    public static void debugAllBlockSuffixes(String name, int blockSize, byte[] encryptedBytes, DecryptFunction13 decryptFn) throws Exception {
        int blockCount = encryptedBytes.length / blockSize;
        for (int i = 1; i <= blockCount; ++i) {
            int suffixLength = i * blockSize;
            byte[] suffixBytes = Arrays.copyOfRange(encryptedBytes, encryptedBytes.length - suffixLength, encryptedBytes.length);
            System.err.printf("Last %s Blocks of %s: %s\n", i, name, debugText(blockSize, suffixBytes, decryptFn));
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
