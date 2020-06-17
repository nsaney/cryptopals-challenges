package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.set01.Challenge03.SingleCharXorCipherResult;

import java.util.*;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/6
 */
public class Challenge06 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] data = readFileBase64(args[0]);
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
                SingleCharXorCipherResult blockCipherResult = Challenge03.getMostLikelyEnglishCleartext(transposedBlock);
                // step 8
                probableKey[i] = blockCipherResult == null ? 0 : blockCipherResult.key;
            }
            RepeatingXorCipherResult fullCipherResult = new RepeatingXorCipherResult(data, probableKey);
            probableResults.add(fullCipherResult);
        }
        return Challenge03.getMostLikelyEnglishCleartext(probableResults);
    }
    
    
    ////// Static Inner Classes //////
    public static class RepeatingXorCipherResult extends CipherResult<byte[], RuntimeException> {
        
        //// Constructor ////
        public RepeatingXorCipherResult(byte[] input, byte[] _key) {
            super(input, _key.clone());
        }
        
        //// Instance Methods ////
        @Override
        public byte[] getResultFromInput(byte[] input) {
            return Challenge05.applyRepeatingKeyXor(input, this.key);
        }
        
        @Override
        public String getKeyText() {
            return toDisplayableText(this.key);
        }
        
    }
    
}
