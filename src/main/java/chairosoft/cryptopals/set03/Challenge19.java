package chairosoft.cryptopals.set03;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/3/challenges/19
 */
public class Challenge19 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromBase64Text(args[0]);
        byte[] nonce = fromBase64Text(args[1]);
        boolean littleEndianBlockCount = Boolean.parseBoolean(args[2]);
        ByteOrder byteOrder = littleEndianBlockCount ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        File inputsFile = new File(args[3]);
        List<String> inputLines = Files.readAllLines(inputsFile.toPath(), COMMON_CHARSET);
        int inputCount = inputLines.size();
        List<byte[]> inputs = new ArrayList<>(inputCount);
        for (String inputLine : inputLines) { inputs.add(fromBase64Text(inputLine)); }
        EncryptFunction encryptFn = data -> Challenge18.applyCounterCipher(key, nonce, byteOrder, data);
        List<byte[]> encryptedItemList = new ArrayList<>(inputCount);
        for (byte[] input : inputs) { encryptedItemList.add(encryptFn.encrypt(input)); }
        byte[][] encryptedItems = encryptedItemList.toArray(new byte[0][]);
        byte[][] decryptedItems = breakFixedNonceCtr(encryptedItems);
        for (int i = 0; i < decryptedItems.length; ++i) {
            byte[] decrypted = decryptedItems[i];
            System.out.printf("%2s ==>> %s\n", (i + 1), escapingNewlines(toDisplayableText(decrypted)));
        }
    }
    
    
    ////// Static Methods //////
    public static byte[][] breakFixedNonceCtr(byte[][] encryptedItems) throws Exception {
        int itemCount = encryptedItems.length;
        byte[][] decryptedItems = new byte[itemCount][];
        byte[] keyStream = breakFixedNonceCtrKeyStream(encryptedItems);
        for (int i = 0; i < itemCount; ++i) {
            byte[] encryptedItem = encryptedItems[i];
            decryptedItems[i] = encryptedItem == null ? null : xor(keyStream, encryptedItem);
        }
        return decryptedItems;
    }
    
    public static byte[] breakFixedNonceCtrKeyStream(byte[][] encryptedItems) throws Exception {
        int maxLength = 0;
        for (byte[] item : encryptedItems) {
            int currentLength = item == null ? 0 : item.length;
            if (currentLength > maxLength) { maxLength = currentLength; }
        }
        byte[] keyStream = new byte[maxLength];
        byte[][] keyByteCandidates = new byte[maxLength][];
        int maxCandidateCount = 0;
        for (int i = 0; i < maxLength; ++i) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] crossSection = getReducedCrossSection(encryptedItems, i);
            for (int x = Byte.MIN_VALUE; x <= Byte.MAX_VALUE; ++x) {
                byte b = (byte)x;
                if (isPossibleXorKey(b, crossSection)) {
                    baos.write(b);
                }
            }
            byte[] candidates = baos.toByteArray();
            keyByteCandidates[i] = candidates;
            int currentCandidateCount = candidates.length;
            if (currentCandidateCount > maxCandidateCount) { maxCandidateCount = currentCandidateCount; }
        }
        // DEBUG
        System.err.println("Key Candidates:");
        Byte[][] candidateSlices = new Byte[maxCandidateCount][];
        for (int n = 0; n < maxCandidateCount; ++n) {
            System.err.printf("[#%3s] |", n);
            Byte[] candidateSlice = getFullCrossSection(keyByteCandidates, n);
            candidateSlices[n] = candidateSlice;
            for (Byte candidate : candidateSlice) {
                if (candidate == null) {
                    System.err.print("  ");
                }
                else {
                    System.err.printf("%02x", candidate);
                }
                System.err.print("|");
            }
            System.err.println();
        }
        int debugLinesPerOutput = Math.min(maxCandidateCount, 10);
        for (byte[] encryptedItem : encryptedItems) {
            System.err.println("================================================================");
            for (int n = 0; n < debugLinesPerOutput; ++n) {
                System.err.printf("[#%3s] |", n);
                Byte[] candidateSlice = candidateSlices[n];
                for (int i = 0; i < encryptedItem.length; ++i) {
                    Byte candidate = candidateSlice[i];
                    if (candidate == null) {
                        System.err.print('#');
                    }
                    else {
                        byte c = encryptedItem[i];
                        byte debugResult = xor(c, candidate);
                        if (debugResult == (byte)'\n') {
                            System.err.print('_');
                        }
                        else {
                            System.err.print(toDisplayableText(debugResult));
                        }
                    }
                }
                System.err.println();
            }
        }
        return keyStream;
    }
    
    public static byte[] getReducedCrossSection(byte[][] items, int index) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(items.length);
        for (byte[] item : items) {
            if (item != null && item.length > index) {
                byte b = item[index];
                baos.write(b);
            }
        }
        return baos.toByteArray();
    }
    
    public static Byte[] getFullCrossSection(byte[][] items, int index) {
        List<Byte> resultList = new ArrayList<>(items.length);
        for (byte[] item : items) {
            boolean hasValue = (item != null && item.length > index);
            Byte b = hasValue ? item[index] : null;
            resultList.add(b);
        }
        return resultList.toArray(new Byte[0]);
    }
    
    public static boolean isPossibleXorKey(byte key, byte[] data) {
        for (byte dataValue : data) {
            byte potentialResult = xor(key, dataValue);
            if (!isExpectedChar(potentialResult)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isExpectedChar(byte code) {
        return isDisplayableChar(code)
            && code != '~'
            && code != '`'
            && code != '#'
            && code != '$'
            && code != '%'
            && code != '^'
            && code != '*'
            && code != '_'
            && code != '+'
            && code != '='
            && code != '|'
            && code != '\\'
            && code != '<'
            && code != '>'
            && code != '{'
            && code != '}'
            ;
    }
    
}
