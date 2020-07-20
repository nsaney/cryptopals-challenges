package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.set01.Challenge03;
import chairosoft.cryptopals.set01.Challenge06;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/3/challenges/20
 */
public class Challenge20 {
    
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
        byte[][] decryptedItems = breakFixedNonceCtrAsRepeatingKeyXor(encryptedItems);
        for (int i = 0; i < decryptedItems.length; ++i) {
            byte[] decrypted = decryptedItems[i];
            System.out.printf("%3s  %s\n", (i + 1), escapingNewlines(toDisplayableText(decrypted)));
        }
    }
    
    
    ////// Static Inner Classes //////
    public static class SingleCharXorCipherRequirements {
        //// Instance Fields ////
        public final IntPredicate acceptableCharTest;
        public final String expectedMostFrequentChars;
        public final int minimumFirstCharCount;
        //// Constructors ////
        public SingleCharXorCipherRequirements(IntPredicate _acceptableCharTest, String _expectedMostFrequentChars) {
            this(_acceptableCharTest, _expectedMostFrequentChars, 0);
        }
        public SingleCharXorCipherRequirements(
            IntPredicate _acceptableCharTest,
            String _expectedMostFrequentChars,
            int _minimumFirstCharCount
        ) {
            this.acceptableCharTest = _acceptableCharTest;
            this.expectedMostFrequentChars = _expectedMostFrequentChars;
            this.minimumFirstCharCount = _minimumFirstCharCount;
        }
    }
    
    
    ////// Static Methods //////
    public static byte[][] breakFixedNonceCtrAsRepeatingKeyXor(byte[][] encryptedItems) throws Exception {
        int minLength = Stream.of(encryptedItems).mapToInt(arr -> arr.length).min().orElse(0);
        int itemCount = encryptedItems.length;
        byte[][] decryptedItems = new byte[itemCount][];
        byte[] keyStream = breakFixedNonceCtrKeyStreamAsRepeatingKeyXor(encryptedItems);
        for (int i = 0; i < itemCount; ++i) {
            byte[] encryptedItem = encryptedItems[i];
            byte[] decryptedItem = encryptedItem == null ? null : xor(keyStream, encryptedItem);
            if (decryptedItem != null) {
                for (int j = minLength; j < decryptedItem.length; ++j) {
                    decryptedItem[j] = (byte)'?';
                }
            }
            decryptedItems[i] = decryptedItem;
        }
        return decryptedItems;
    }
    
    public static byte[] breakFixedNonceCtrKeyStreamAsRepeatingKeyXor(byte[][] encryptedItems) throws Exception {
        int blockSize = Stream.of(encryptedItems).mapToInt(arr -> arr.length).min().orElse(0);
        int maxLength = Stream.of(encryptedItems).mapToInt(arr -> arr.length).max().orElse(0);
        byte[] keyStream = new byte[maxLength];
        byte[] encryptedPrefixes = new byte[blockSize * encryptedItems.length];
        for (int i = 0; i < encryptedItems.length; ++i) {
            byte[] item = encryptedItems[i];
            copyBlocks(blockSize, item, 0, encryptedPrefixes, i, 1);
        }
        SingleCharXorCipherRequirements requirements = new SingleCharXorCipherRequirements(
            Challenge20::isExpectedChar,
            " eEtTaAoOiInNsShHrRdDlLuU"
        );
        Challenge06.RepeatingXorCipherResult cipherResult = breakRepeatingKeyXor(
            encryptedPrefixes,
            blockSize,
            requirements
        );
        copyBlocks(blockSize, cipherResult.key, 0, keyStream, 0, 1);
        return keyStream;
    }
    
    public static boolean isExpectedChar(int code) {
        return 'A' <= code && code <= 'Z'
            || 'a' <= code && code <= 'z'
            || '0' <= code && code <= '9'
            || " !\"$%&'()*,-./:;?\\[]".contains("" + (char)code);
    }
    
    // modified from Challenge03
    public static Challenge06.RepeatingXorCipherResult breakRepeatingKeyXor(
        byte[] data,
        int keySize,
        SingleCharXorCipherRequirements requirements
    ) {
        byte[] probableKey = new byte[keySize];
        for (int i = 0; i < keySize; ++i) {
            int transposedBlockSize = blockDecompositionSize(data.length, keySize, i);
            byte[] transposedBlock = new byte[transposedBlockSize];
            for (int j = 0, x = i; j < transposedBlockSize; ++j, x += keySize) {
                transposedBlock[j] = data[x];
            }
            Challenge03.SingleCharXorCipherResult blockCipherResult = getMostLikelyCleartext(transposedBlock, requirements);
            probableKey[i] = blockCipherResult == null ? 0 : blockCipherResult.key;
        }
        return new Challenge06.RepeatingXorCipherResult(data, probableKey);
    }
    
    public static Challenge03.SingleCharXorCipherResult getMostLikelyCleartext(
        byte[] singleCharXorCipher,
        SingleCharXorCipherRequirements requirements
    ) {
        List<Challenge03.SingleCharXorCipherResult> cipherResults = new ArrayList<>();
        for (int x = Byte.MIN_VALUE; x <= Byte.MAX_VALUE; ++x) {
            byte b = (byte)x;
            Challenge03.SingleCharXorCipherResult cipherResult = new Challenge03.SingleCharXorCipherResult(singleCharXorCipher, b);
            cipherResults.add(cipherResult);
        }
        return getMostLikelyCleartext(cipherResults, requirements);
    }
    
    public static <T extends CipherResult<?, ?>> T getMostLikelyCleartext(
        List<T> cipherResults,
        SingleCharXorCipherRequirements requirements
    ) {
        byte firstRequiredChar = (byte)requirements.expectedMostFrequentChars.charAt(0);
        List<T> resultsWithAcceptableChars = cipherResults
            .stream()
            .filter(r -> hasOnlyAcceptedChars(r.result, requirements.acceptableCharTest))
            .filter(r -> r.getFrequencyTable().getFrequency(firstRequiredChar) >= requirements.minimumFirstCharCount)
            .collect(Collectors.toList());
        if (resultsWithAcceptableChars.isEmpty()) {
            return null;
        }
        List<T> sorted = FrequencyTable.sortedViaTable(
            resultsWithAcceptableChars,
            r -> r.getFrequencyTable(),
            false,
            requirements.expectedMostFrequentChars.chars().mapToObj(c -> (byte)c).toArray(Byte[]::new)
        );
        return sorted.get(0);
    }
    
    public static boolean hasOnlyAcceptedChars(byte[] data, IntPredicate acceptableCharTest) {
        for (byte b : data) {
            if (!acceptableCharTest.test(b)) {
                return false;
            }
        }
        return true;
    }
    
}
