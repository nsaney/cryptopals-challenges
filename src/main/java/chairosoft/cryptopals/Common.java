package chairosoft.cryptopals;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Common {
    
    ////// Constructor //////
    private Common() { throw new UnsupportedOperationException(); }
    
    
    ////// Constants //////
    public static final Charset COMMON_CHARSET = StandardCharsets.UTF_8;
    
    
    ////// Static Methods - Data Formats and Display //////
    public static int maxIndex(byte[] data, int off, int len) {
        return Math.min(data.length, off + len);
    }
    
    public static byte[] fromHex(String hexText) {
        int textLen = hexText.length();
        int dataLen = textLen / 2;
        byte[] data = new byte[dataLen];
        for (int i = 0, j = 0; j < dataLen; i += 2, ++j) {
            String valueText = hexText.substring(i, i + 2);
            int valueDataInt = Integer.parseInt(valueText, 16);
            byte valueData = (byte)valueDataInt;
            data[j] = valueData;
        }
        return data;
    }
    
    public static String toHex(byte... data) {
        return toHex(data, 0, data.length);
    }
    
    public static String toHex(byte[] data, int off, int len) {
        int max = maxIndex(data, off, len);
        int dataLen = max - off;
        int textLen = dataLen * 2;
        StringBuilder sb = new StringBuilder(textLen);
        for (int i = off; i < max; ++i) {
            byte valueData = data[i];
            int valueUnsigned = valueData & 0xff;
            String valueText = Integer.toString(valueUnsigned, 16);
            if (valueUnsigned < 16) {
                sb.append("0");
            }
            sb.append(valueText);
        }
        return sb.toString();
    }
    
    public static String parseFromHex(String hexText) {
        byte[] data = fromHex(hexText);
        return toDisplayableText(data);
    }
    
    public static Base64.Decoder decoder(boolean useMime) {
        return useMime ? Base64.getMimeDecoder() : Base64.getDecoder();
    }
    
    public static byte[] fromBase64(byte[] base64Data) {
        return fromBase64(base64Data, false);
    }
    
    public static byte[] fromBase64(byte[] base64Data, boolean useMime) {
        return decoder(useMime).decode(base64Data);
    }
    
    public static byte[] fromBase64Text(String base64Text) {
        return fromBase64Text(base64Text, false);
    }
    
    public static byte[] fromBase64Text(String base64Text, boolean useMime) {
        return decoder(useMime).decode(base64Text);
    }
    
    public static byte[] toBase64(byte[] data) {
        return Base64.getEncoder().encode(data);
    }
    
    public static String toBase64Text(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    
    public static byte[] readFileBase64(String fileName) throws IOException {
        File dataFile = new File(fileName);
        byte[] dataBase64 = Files.readAllBytes(dataFile.toPath());
        return fromBase64(dataBase64, true);
    }
    
    public static List<byte[]> readFileLinesHex(String fileName) throws IOException {
        File dataFile = new File(fileName);
        List<String> hexLines = Files.readAllLines(dataFile.toPath());
        return hexLines.stream().map(Common::fromHex).collect(Collectors.toList());
    }
    
    public static boolean isDisplayableChar(int code) {
        return code == '\t' || code == '\r' || code == '\n' || (' ' <= code && code < 127);
    }
    
    public static boolean isSpecialChar(int code) {
        return !isDisplayableChar(code);
    }
    
    public static String toDisplayableText(byte... data) {
        return toDisplayableText(data, 0, data.length);
    }
    
    public static String toDisplayableText(byte[] data, int off, int len) {
        int max = maxIndex(data, off, len);
        StringBuilder sb = new StringBuilder(max);
        boolean inSpecialMode = false;
        for (int i = off; i < max; ++i) {
            byte b = data[i];
            int code = b & 0xff;
            boolean isSpecial = isSpecialChar(code);
            boolean changeMode = isSpecial != inSpecialMode;
            inSpecialMode = isSpecial;
            if (inSpecialMode) {
                if (changeMode) {
                    sb.append("\\x[");
                }
                String s = String.format("%02x", code);
                sb.append(s);
            }
            else {
                if (changeMode) {
                    sb.append("]");
                }
                char c = (char) code;
                if (c == '\\') {
                    sb.append("\\\\");
                }
                else if (c == '\t') {
                    sb.append("\\t");
                }
                else if (c == '\r') {
                    sb.append("\\r");
                }
                else {
                    sb.append(c);
                }
            }
        }
        if (inSpecialMode) {
            sb.append("]");
        }
        return sb.toString();
    }
    
    public static int blockDecompositionSize(int totalSize, int blockCount, int blockIndex) {
        int baseBlockSize = totalSize / blockCount;
        int numberOfIndicesWithOneExtra = totalSize % blockCount;
        boolean indexHasOneExtra = blockIndex < numberOfIndicesWithOneExtra;
        return baseBlockSize + (indexHasOneExtra ? 1 : 0);
    }
    
    ////// Static Methods - Crypto //////
    public static byte[] applyCipher(
        String algorithmName,
        String algorithmMode,
        String paddingScheme,
        int cipherMode,
        byte[] key,
        byte[] data
    )
        throws GeneralSecurityException
    {
        String transformation = algorithmName + "/" + algorithmMode + "/" + paddingScheme;
        Cipher cipher = Cipher.getInstance(transformation);
        SecretKey secretKey = new SecretKeySpec(key, algorithmName);
        cipher.init(cipherMode, secretKey);
        return cipher.doFinal(data);
    }
    
    
    ////// Static Inner Classes //////
    public static abstract class CipherResult<T, Ex extends Exception> {
        
        //// Instance Fields ////
        public final T key;
        public final byte[] result;
        
        //// Constructor ////
        public CipherResult(byte[] input, T _key) throws Ex {
            this.key = _key;
            this.result = this.getResultFromInput(input);
        }
        
        //// Instance Methods - Abstract ////
        public abstract byte[] getResultFromInput(byte[] input) throws Ex;
        public abstract String getKeyText();
        
        //// Instance Methods - Concrete ////
        @Override
        public String toString() {
            String keyText = this.getKeyText();
            String resultText = toDisplayableText(this.result);
            return String.format("[%s]: %s", keyText, resultText);
        }
        
        public FrequencyTable<Byte> getFrequencyTable() {
            FrequencyTable<Byte> frequencyTable = new FrequencyTable<>();
            for (byte b : this.result) {
                frequencyTable.incrementFrequency(b);
            }
            return frequencyTable;
        }
        
    }
    
    public static class AlgorithmCipherInfo {
        
        //// Instance Fields ////
        public final String algorithmName;
        public final String algorithmMode;
        public final String paddingScheme;
        public final byte[] key;
        
        //// Constructor ////
        public AlgorithmCipherInfo(
            String _algorithmName,
            String _algorithmMode,
            String _paddingScheme,
            byte[] _key
        ) {
            this.algorithmName = _algorithmName;
            this.algorithmMode = _algorithmMode;
            this.paddingScheme = _paddingScheme;
            this.key = _key.clone();
        }
        
    }
    
    public static class AlgorithmCipherResult extends CipherResult<AlgorithmCipherInfo, GeneralSecurityException> {
        
        //// Constructor ////
        public AlgorithmCipherResult(byte[] input, AlgorithmCipherInfo _key) throws GeneralSecurityException {
            super(input, _key);
        }
        
        //// Instance Methods ////
        @Override
        public byte[] getResultFromInput(byte[] input) throws GeneralSecurityException {
            return applyCipher(
                this.key.algorithmName,
                this.key.algorithmMode,
                this.key.paddingScheme,
                Cipher.DECRYPT_MODE,
                this.key.key,
                input
            );
        }
        
        @Override
        public String getKeyText() {
            return String.format(
                "alg=%s/%s/%s;key=%s",
                this.key.algorithmName,
                this.key.algorithmMode,
                this.key.paddingScheme,
                toDisplayableText(this.key.key)
            );
        }
        
    }
    
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
    
}
