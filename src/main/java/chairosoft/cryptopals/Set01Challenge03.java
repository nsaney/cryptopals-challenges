package chairosoft.cryptopals;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.*;
import static chairosoft.cryptopals.Set01Challenge02.xor;

/**
 * https://cryptopals.com/sets/1/challenges/3
 */
public class Set01Challenge03 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] input = fromHex(args[0]);
        SingleCharXorCipherResult result = getEnglishCleartext(input);
        System.out.println(result);
    }
    
    
    ////// Static Methods //////
    public static SingleCharXorCipherResult getEnglishCleartext(byte[] singleCharXorCipher) {
        List<SingleCharXorCipherResult> cipherResults = new ArrayList<>();
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; ++b) {
            SingleCharXorCipherResult cipherResult = new SingleCharXorCipherResult(singleCharXorCipher, b);
            cipherResults.add(cipherResult);
        }
        return cipherResults
            .stream()
            .filter(r -> hasOnlyDisplayableChars(r.result))
            .map(r -> new AbstractMap.SimpleEntry<>(r, r.getFrequencyTable().getFrequency((byte)' ')))
            .filter(e -> e.getValue() > 0)
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    public static boolean hasOnlyDisplayableChars(byte[] data) {
        for (byte b : data) {
            if (isSpecialChar(b)) {
                return false;
            }
        }
        return true;
    }
    
    
    ////// Static Inner Classes //////
    public static class Frequency<T> implements Comparable<Frequency<T>> {
        
        //// Instance Fields ////
        public final T key;
        
        //// Instance Properties ////
        private int count = 0;
        public int getCount() {
            return this.count;
        }
        public int incrementCount() {
            return ++this.count;
        }
        
        //// Constructor ////
        public Frequency(T _key) {
            this.key = _key;
        }
        
        //// Instance Methods ////
        @Override
        public int compareTo(Frequency<T> that) {
            return -1 * Integer.compare(this.count, that.count);
        }
        
        @Override
        public String toString() {
            return this.toString(Objects::toString);
        }
        
        public String toString(Function<T, String> keyToStringFn) {
            String keyText = keyToStringFn.apply(this.key);
            return String.format("%s:%s", keyText, this.count);
        }
        
    }
    
    public static class FrequencyTable<T> {
        
        //// Instance Fields ////
        private final Map<T, Frequency<T>> innerMap = new HashMap<>();
        
        //// Instance Methods ////
        public int getFrequency(T obj) {
            if (obj == null) {
                return 0;
            }
            Frequency<T> frequency = this.innerMap.get(obj);
            if (frequency == null) {
                return 0;
            }
            return frequency.getCount();
        }
        
        public int incrementFrequency(T obj) {
            if (obj == null) {
                return 0;
            }
            Frequency<T> frequency = this.innerMap.computeIfAbsent(obj, Frequency::new);
            return frequency.incrementCount();
        }
        
        @SafeVarargs
        public final void incrementFrequencies(T... objects) {
            for (T obj : objects) {
                this.incrementFrequency(obj);
            }
        }
        
        @Override
        public String toString() {
            return this.toString(Objects::toString);
        }
        
        public String toString(Function<T, String> keyToStringFn) {
            return this
                .innerMap
                .values()
                .stream()
                .sorted()
                .map(frequencyToStringFn(keyToStringFn))
                .collect(Collectors.joining("; ", "[", "]"));
        }
        
        public Function<Frequency<T>, String> frequencyToStringFn(Function<T, String> keyToStringFn) {
            return f -> f.toString(keyToStringFn);
        }
        
    }
    
    public static class SingleCharXorCipherResult {
        
        //// Instance Fields ////
        public final byte xorByte;
        public final byte[] result;
        
        //// Constructor ////
        public SingleCharXorCipherResult(byte[] input, byte _xorByte) {
            int len = input.length;
            this.xorByte = _xorByte;
            this.result = new byte[len];
            for (int i = 0; i < len; ++i) {
                byte b = input[i];
                this.result[i] = xor(b, this.xorByte);
            }
        }
        
        //// Instance Methods ////
        @Override
        public String toString() {
            String resultText = toDisplayableText(this.result);
            return String.format("[%02x]: %s", this.xorByte, resultText);
        }
        
        public FrequencyTable<Byte> getFrequencyTable() {
            FrequencyTable<Byte> frequencyTable = new FrequencyTable<>();
            for (byte b : this.result) {
                frequencyTable.incrementFrequency(b);
            }
            return frequencyTable;
        }
        
    }
    
}
