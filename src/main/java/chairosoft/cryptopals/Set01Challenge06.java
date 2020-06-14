package chairosoft.cryptopals;

import chairosoft.cryptopals.Set01Challenge03.SingleCharXorCipherResult;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/6
 */
public class Set01Challenge06 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        File inputFile = new File(args[0]);
        byte[] dataBase64 = Files.readAllBytes(inputFile.toPath());
        byte[] data = fromBase64(dataBase64, true);
        int keySizeMin = Integer.parseInt(args[1]);
        int keySizeMax = Integer.parseInt(args[2]);
        RepeatingXorCipherResult output = breakRepeatingKeyXor(data, keySizeMin, keySizeMax);
        System.out.print(output);
    }
    
    
    ////// Static Methods //////
    public static RepeatingXorCipherResult breakRepeatingKeyXor(byte[] data, int keySizeMin, int keySizeMax) {
        // step 3
        Map<Integer, Integer> normalizedDistanceByKeySize = new HashMap<>();
        for (int keySize = keySizeMin; keySize <= keySizeMax; ++keySize) {
            int distance1v2 = hammingDistance(data, 0, keySize, keySize);
            int distance1v3 = hammingDistance(data, 0, 2*keySize, keySize);
            int distance2v3 = hammingDistance(data, keySize, 2*keySize, keySize);
            int normalizedAverageDistance = (distance1v2 + distance1v3 + distance2v3) / (3 * keySize);
            normalizedDistanceByKeySize.put(keySize, normalizedAverageDistance);
        }
        // step 4
        List<Map.Entry<Integer, Integer>> sortedKeySizeDistancePairs = normalizedDistanceByKeySize
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());
        int minDistance = sortedKeySizeDistancePairs.get(0).getValue();
        List<Integer> mostProbablyKeySizes = sortedKeySizeDistancePairs
            .stream()
            .filter(e -> e.getValue().equals(minDistance))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        List<RepeatingXorCipherResult> probableResults = new ArrayList<>(mostProbablyKeySizes.size());
        for (int keySize : mostProbablyKeySizes) {
            byte[] probableKey = new byte[keySize];
            for (int i = 0; i < keySize; ++i) {
                // step 5
                int transposedBlockSize = blockDecompositionSize(data.length, keySize, i);
                // step 6
                byte[] transposedBlock = new byte[transposedBlockSize];
                for (int j = 0, x = i; j < transposedBlockSize; ++j, x += keySize) {
                    transposedBlock[j] = data[x];
                }
                // step 7
                SingleCharXorCipherResult blockCipherResult = Set01Challenge03.getMostLikelyEnglishCleartext(transposedBlock);
                // step 8
                probableKey[i] = blockCipherResult == null ? 0 : blockCipherResult.key;
            }
            RepeatingXorCipherResult fullCipherResult = new RepeatingXorCipherResult(data, probableKey);
            probableResults.add(fullCipherResult);
        }
        return Set01Challenge03.getMostLikelyEnglishCleartext(probableResults);
    }
    
    public static int hammingDistance(byte[] x, byte[] y) {
        boolean isXShorter = x.length < y.length;
        int minLength = isXShorter ? x.length : y.length;
        int maxLength = isXShorter ? y.length : x.length;
        int overflowBytes = maxLength - minLength;
        int overflowBits = 8 * overflowBytes;
        int distanceBits = hammingDistance(x, 0, y, 0, minLength);
        return distanceBits + overflowBits;
    }
    
    public static int hammingDistance(byte[] data, int xOff, int yOff, int length) {
        return hammingDistance(data, xOff, data, yOff, length);
    }
    
    public static int hammingDistance(byte[] x, int xOff, byte[] y, int yOff, int length) {
        int distanceBits = 0;
        for (int i = 0; i < length; ++i) {
            byte xi = x[i + xOff];
            byte yi = y[i + yOff];
            int currenXor = (xi ^ yi) & 0xff;
            int currentDistanceBits = Integer.bitCount(currenXor);
            distanceBits += currentDistanceBits;
        }
        return distanceBits;
    }
    
    
    ////// Static Inner Classes //////
    public static class RepeatingXorCipherResult extends CipherResult<byte[]> {
        
        //// Constructor ////
        public RepeatingXorCipherResult(byte[] input, byte[] _key) {
            super(input, Arrays.copyOf(_key, _key.length));
        }
        
        //// Instance Methods ////
        @Override
        public byte[] getResultFromInput(byte[] input) {
            return Set01Challenge05.applyRepeatingKeyXor(input, this.key);
        }
        
        @Override
        public String getKeyText() {
            return toDisplayableText(this.key);
        }
        
    }
    
}
