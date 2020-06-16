package chairosoft.cryptopals;

import java.util.*;

import static chairosoft.cryptopals.Common.*;
import static chairosoft.cryptopals.Set01Challenge02.xor;

/**
 * https://cryptopals.com/sets/1/challenges/3
 */
public class Set01Challenge03 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] input = fromHex(args[0]);
        SingleCharXorCipherResult result = getMostLikelyEnglishCleartext(input);
        System.out.println(result);
    }
    
    
    ////// Static Methods //////
    public static SingleCharXorCipherResult getMostLikelyEnglishCleartext(byte[] singleCharXorCipher) {
        List<SingleCharXorCipherResult> cipherResults = new ArrayList<>();
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; ++b) {
            SingleCharXorCipherResult cipherResult = new SingleCharXorCipherResult(singleCharXorCipher, b);
            cipherResults.add(cipherResult);
        }
        return getMostLikelyEnglishCleartext(cipherResults);
    }
    
    public static <T extends CipherResult<?, ?>> T getMostLikelyEnglishCleartext(List<T> cipherResults) {
        return cipherResults
            .stream()
            .filter(r -> hasOnlyEnglishChars(r.result))
            .map(r -> new AbstractMap.SimpleEntry<>(r, r.getFrequencyTable().getFrequency((byte)' ')))
            .filter(e -> e.getValue() > 0)
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    public static boolean isEnglishChar(byte code) {
        return code == '\t'
            || code == '\r'
            || code == '\n'
            || (' ' <= code && code <= ';')
            || ('@' <= code && code <= 'Z')
            || ('a' <= code && code <= 'z');
    }
    
    public static boolean hasOnlyEnglishChars(byte[] data) {
        for (byte b : data) {
            if (!isEnglishChar(b)) {
                return false;
            }
        }
        return true;
    }
    
    
    ////// Static Inner Classes //////
    public static class SingleCharXorCipherResult extends CipherResult<Byte, RuntimeException> {
        
        //// Constructor ////
        public SingleCharXorCipherResult(byte[] input, byte _key) {
            super(input, _key);
        }
        
        //// Instance Methods ////
        @Override
        public byte[] getResultFromInput(byte[] input) {
            int len = input.length;
            byte[] result = new byte[len];
            for (int i = 0; i < len; ++i) {
                byte b = input[i];
                result[i] = xor(b, this.key);
            }
            return result;
        }
    
        @Override
        public String getKeyText() {
            return String.format("%02x", this.key);
        }
        
    }
    
}
