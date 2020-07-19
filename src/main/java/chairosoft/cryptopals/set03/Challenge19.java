package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.set01.Challenge03;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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
            System.out.printf("%3s  %s\n", (i + 1), escapingNewlines(toDisplayableText(decrypted)));
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
            sortCandidates(candidates, crossSection);
            keyByteCandidates[i] = candidates;
            keyStream[i] = candidates.length > 0 ? candidates[0] : 0;
            int currentCandidateCount = candidates.length;
            if (currentCandidateCount > maxCandidateCount) { maxCandidateCount = currentCandidateCount; }
        }
        // DEBUG
        debug.println("Key Candidates:");
        Byte[][] candidateSlices = new Byte[maxCandidateCount][];
        for (int n = 0; n < maxCandidateCount; ++n) {
            debug.printf("[#%3s] |", n);
            Byte[] candidateSlice = getFullCrossSection(keyByteCandidates, n);
            candidateSlices[n] = candidateSlice;
            for (Byte candidate : candidateSlice) {
                if (candidate == null) {
                    debug.print("  ");
                }
                else {
                    debug.printf("%02x", candidate);
                }
                debug.print("|");
            }
            debug.println();
        }
        int debugLinesPerOutput = Math.min(maxCandidateCount, 5);
        for (int k = 0; k < encryptedItems.length; ++k) {
            byte[] encryptedItem = encryptedItems[k];
            debug.println("================================================================");
            debug.printf("Line #%2s:\n", (k + 1));
            for (int n = 0; n < debugLinesPerOutput; ++n) {
                debug.printf("[#%3s] |", n);
                Byte[] candidateSlice = candidateSlices[n];
                for (int i = 0; i < encryptedItem.length; ++i) {
                    Byte candidate = candidateSlice[i];
                    if (candidate == null) {
                        debug.print('#');
                    }
                    else {
                        byte c = encryptedItem[i];
                        byte debugResult = xor(c, candidate);
                        if (debugResult == (byte)'\n') {
                            debug.print('_');
                        }
                        else {
                            debug.print(toDisplayableText(debugResult));
                        }
                    }
                }
                debug.println();
            }
        }
        debug.println("================================================================");
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
            && !('0' <= code && code <= '9')
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
    
    public static void sortCandidates(byte[] candidates, byte[] crossSection) throws Exception {
        List<Byte> unsortedCandidateList = new ArrayList<>(candidates.length);
        for (byte candidate : candidates) {
            unsortedCandidateList.add(candidate);
        }
        List<Byte> sortedCandidateList = FrequencyTable.sortedViaTable(
            unsortedCandidateList,
            key -> new Challenge03.SingleCharXorCipherResult(crossSection, key).getFrequencyTable(),
            false,
            (byte)' ',
            (byte)'e', (byte)'E',
            (byte)'t', (byte)'T',
            (byte)'a', (byte)'A',
            (byte)'o', (byte)'O',
            (byte)'i', (byte)'I',
            (byte)'n', (byte)'N'
        );
        for (int i = 0; i < candidates.length; ++i) {
            byte key = sortedCandidateList.get(i);
            candidates[i] = key;
        }
    }
    
}
