package chairosoft.cryptopals;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Common {
    
    ////// Constructor //////
    private Common() { throw new UnsupportedOperationException(); }
    
    
    ////// Constants //////
    public static final Charset COMMON_CHARSET = StandardCharsets.UTF_8;
    public static final ThreadLocal<Random> THREAD_LOCAL_RANDOM = ThreadLocal.withInitial(Random::new);
    
    
    ////// Static Methods - Data Formats and Display //////
    public static int maxIndex(byte[] data, int off, int len) {
        return Math.min(data.length, off + len);
    }
    
    public static byte[] fromUtf8(String text) {
        return text.getBytes(COMMON_CHARSET);
    }
    
    public static String toUtf8(byte... data) {
        return new String(data, COMMON_CHARSET);
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
        toHex(sb, data, off, max);
        return sb.toString();
    }
    
    public static void toHex(StringBuilder sb, byte[] data, int off, int max) {
        for (int i = off; i < max; ++i) {
            byte valueData = data[i];
            int valueUnsigned = valueData & 0xff;
            String valueText = Integer.toString(valueUnsigned, 16);
            if (valueUnsigned < 16) {
                sb.append("0");
            }
            sb.append(valueText);
        }
    }
    
    public static String toBlockedHex(int blockSize, byte... data) {
        return toBlockedHex(blockSize, data, 0, data.length);
    }
    
    public static String toBlockedHex(int blockSize, byte[] data, int off, int len) {
        if (blockSize < 1) {
            return "[" + toHex(data, off, len) + "]";
        }
        int max = maxIndex(data, off, len);
        int dataLen = max - off;
        int blockCount = dataLen / blockSize;
        int textLen = (dataLen * 2) + blockCount + 1;
        StringBuilder sb = new StringBuilder(textLen);
        for (int blockIndex = 0; blockIndex < blockCount; ++blockIndex) {
            char startChar = blockIndex == 0 ? '[' : '|';
            sb.append(startChar);
            int blockOff = off + (blockIndex * blockSize);
            int blockMax = blockOff + blockSize;
            toHex(sb, data, blockOff, blockMax);
        }
        int remainderLen = dataLen % blockSize;
        if (remainderLen > 0) {
            char remainderChar = blockCount == 0 ? '[' : '|';
            sb.append(remainderChar);
            int remainderOff = off + (blockCount * blockSize);
            toHex(sb, data, remainderOff, max);
            int paddingLen = blockSize - remainderLen;
            for (int i = 0; i < paddingLen; ++i) {
                sb.append("<>");
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static String parseFromHex(String hexText) {
        byte[] data = fromHex(hexText);
        return toDisplayableText(data);
    }
    
    public static Base64.Decoder decoder(boolean useMime) {
        return useMime ? Base64.getMimeDecoder() : Base64.getDecoder();
    }
    
    public static byte[] fromBase64(byte... base64Data) {
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
    
    public static byte[] toBase64(byte... data) {
        return Base64.getEncoder().encode(data);
    }
    
    public static String toBase64Text(byte... data) {
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
    
    public static String escapingNewlines(String text) {
        return text.replaceAll(Pattern.quote("\n"), "\\n");
    }
    
    public static int blockDecompositionSize(int totalSize, int blockCount, int blockIndex) {
        int baseBlockSize = totalSize / blockCount;
        int numberOfIndicesWithOneExtra = totalSize % blockCount;
        boolean indexHasOneExtra = blockIndex < numberOfIndicesWithOneExtra;
        return baseBlockSize + (indexHasOneExtra ? 1 : 0);
    }
    
    ////// Static Methods - Simple Manipulation //////
    public static int count(byte c, byte... bytes) {
        int sum = 0;
        for (byte b : bytes) {
            if (b == c) {
                ++sum;
            }
        }
        return sum;
    }
    
    public static int firstIndexOf(byte c, byte... bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            if (b == c) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(byte c, byte... bytes) {
        for (int i = bytes.length; i --> 0; ) {
            byte b = bytes[i];
            if (b == c) {
                return i;
            }
        }
        return -1;
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
            int currentXor = (xi ^ yi) & 0xff;
            int currentDistanceBits = Integer.bitCount(currentXor);
            distanceBits += currentDistanceBits;
        }
        return distanceBits;
    }
    
    public static boolean areEqual(byte[] x, byte[] y) {
        return Arrays.equals(x, y);
    }
    
    public static boolean areEqual(byte[] data, int xOff, int yOff, int length) {
        return areEqual(data, xOff, data, yOff, length);
    }
    
    public static boolean areEqual(byte[] x, int xOff, byte[] y, int yOff, int length) {
        for (int i = 0; i < length; ++i) {
            byte xi = x[i + xOff];
            byte yi = y[i + yOff];
            if (xi != yi) {
                return false;
            }
        }
        return true;
    }
    
    public static byte[] extendRepeat(byte[] original, int newLength) {
        byte[] result = Arrays.copyOf(original, newLength);
        int originalLength = original.length;
        for (int i = originalLength; i < newLength; ++i) {
            int j = i % originalLength;
            result[i] = result[j];
        }
        return result;
    }
    
    public static void copyBlocks(
        int blockSize,
        byte[] src,
        int srcBlkOff,
        byte[] dest,
        int destBlkOff,
        int blockCount
    ) {
        int srcPos = srcBlkOff * blockSize;
        int destPos = destBlkOff * blockSize;
        int length = blockCount * blockSize;
        System.arraycopy(src, srcPos, dest, destPos, length);
    }
    
    public static byte[] appendBlocks(int blockSize, byte[] original, byte[] src, int srcBlkOff, int blockCount) {
        int srcPos = srcBlkOff * blockSize;
        int appendedLength = blockCount * blockSize;
        byte[] result = Arrays.copyOf(original, original.length + appendedLength);
        System.arraycopy(src, srcPos, result, original.length, appendedLength);
        return result;
    }
    
    public static byte[] concatenate(byte[] x, byte... y) {
        int resultLen = x.length + y.length;
        byte[] result = Arrays.copyOf(x, resultLen);
        System.arraycopy(y, 0, result, x.length, y.length);
        return result;
    }
    
    public static List<OverlapDetails> getOverlaps(byte[] x, byte[] y) {
        List<OverlapDetails> result = new ArrayList<>();
        int minLength = Math.min(x.length, y.length);
        int currentOverlapOffset = -1;
        for (int i = 0; i <= minLength; ++i) {
            boolean currentMatch = i < minLength && (x[i] == y[i]);
            if (currentMatch && currentOverlapOffset <= -1) {
                currentOverlapOffset = i;
            }
            if (!currentMatch && currentOverlapOffset > -1) {
                int currentOverlapLength = i - currentOverlapOffset;
                OverlapDetails overlap = new OverlapDetails(currentOverlapOffset, currentOverlapLength);
                result.add(overlap);
                currentOverlapOffset = -1;
            }
        }
        return result;
    }
    
    public static int getInitialOverlappingBlockCount(int blockSize, byte[] x, byte[] y) {
        int len = Math.min(x.length, y.length);
        int count = 0;
        for (int i = 0; i < len; i += blockSize) {
            if (!areEqual(x, i, y, i, blockSize)) {
                break;
            }
            ++count;
        }
        return count;
    }
    
    
    ////// Static Methods - Crypto //////
    public static byte xor(byte xVal, byte yVal) {
        return (byte)((xVal ^ yVal) & 0xff);
    }
    
    public static byte[] xor(byte[] x, byte[] y) {
        return xor(x, 0, y, 0, null, -1, x.length);
    }
    
    public static byte[] xor(byte[] x, int xOff, byte[] y, int yOff, byte[] out, int outOff, int len) {
        int xMax = maxIndex(x, xOff, len);
        int xLen = xMax - xOff;
        int yMax = maxIndex(y, yOff, len);
        int yLen = yMax - yOff;
        int outMax = out == null ? xLen : maxIndex(out, outOff, len);
        int outLen = out == null ? xLen : (outMax - outOff);
        if (xLen != yLen || xLen != outLen) {
            throw new ArrayIndexOutOfBoundsException(String.format(
                "Mismatched lengths: x[%s] %s:%s => %s || y[%s] %s:%s => %s || out[%s] %s:%s => %s",
                x.length, xOff, xMax, xLen,
                y.length, yOff, yMax, yLen,
                (out == null) ? null : out.length, outOff, outMax, outLen
            ));
        }
        byte[] result = out == null ? new byte[outLen] : out;
        if (out == null) { outOff = 0; }
        for (int i = xOff, j = yOff, k = outOff; k < outMax; ++i, ++j, ++k) {
            byte xi = x[i];
            byte yj = y[j];
            result[k] = xor(xi, yj);
        }
        return result;
    }
    
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
        return applyCipher(algorithmName, algorithmMode, paddingScheme, cipherMode, key, null, data);
    }
    
    public static byte[] applyCipher(
        String algorithmName,
        String algorithmMode,
        String paddingScheme,
        int cipherMode,
        byte[] key,
        AlgorithmParameterSpec params,
        byte[] data
    )
        throws GeneralSecurityException
    {
        return applyCipher(
            algorithmName,
            algorithmMode,
            paddingScheme,
            cipherMode,
            key,
            params,
            data,
            0,
            null,
            -1,
            data.length
        );
    }
    
    public static byte[] applyCipher(
        String algorithmName,
        String algorithmMode,
        String paddingScheme,
        int cipherMode,
        byte[] key,
        AlgorithmParameterSpec params,
        byte[] data,
        int dataOff,
        byte[] out,
        int outOff,
        int len
    )
        throws GeneralSecurityException
    {
        String transformation = algorithmName + "/" + algorithmMode + "/" + paddingScheme;
        Cipher cipher = Cipher.getInstance(transformation);
        SecretKey secretKey = new SecretKeySpec(key, algorithmName);
        cipher.init(cipherMode, secretKey, params);
        if (out == null) {
            return cipher.doFinal(data, dataOff, len);
        }
        else {
            cipher.doFinal(data, dataOff, len, out, outOff);
            return out;
        }
    }
    
    public static byte[] withoutPkcs7Padding(int blockSize, byte... data) throws GeneralSecurityException {
        if (data == null || data.length == 0) {
            return data;
        }
        int paddingLen = getPkcs7PaddingLength(blockSize, data);
        if (paddingLen < 1) {
            int blockCount = data.length / blockSize;
            byte[] lastBlock = new byte[blockSize];
            copyBlocks(blockSize, data, blockCount - 1, lastBlock, 0, 1);
            throw new BadPaddingException(String.format(
                "Invalid PKCS #7 padding for data: [...|%s].",
                toDisplayableText(lastBlock)
            ));
        }
        int newLen = data.length - paddingLen;
        return Arrays.copyOf(data, newLen);
    }
    
    public static int getPkcs7PaddingLength(int blockSize, byte... data) throws GeneralSecurityException {
        // see com.sun.crypto.provider.PKCS5Padding#unpad
        if (data == null || data.length == 0) {
            return 0;
        }
        int dataLenModBlockSize = data.length % blockSize;
        if (dataLenModBlockSize != 0) {
            throw new IllegalBlockSizeException(String.format(
                "Expected data with block size of %s but modulo value was %s.",
                blockSize,
                dataLenModBlockSize
            ));
        }
        int lastIndex = data.length - 1;
        byte lastByte = data[lastIndex];
        if (lastByte < 1 || lastByte > blockSize) {
            return -1;
        }
        for (byte i = 1; i < lastByte; ++i) {
            int index = lastIndex - i;
            byte b = data[index];
            if (b != lastByte) {
                return -1;
            }
        }
        return lastByte;
    }
    
    
    ////// Static Methods - Random //////
    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        randomBytes(bytes);
        return bytes;
    }
    
    public static void randomBytes(byte[] bytes) {
        randomBytes(bytes, 0, bytes.length);
    }
    
    public static void randomBytes(byte[] bytes, int off, int len) {
        Random random = THREAD_LOCAL_RANDOM.get();
        int max = maxIndex(bytes, off, len);
        // logic taken from Random#nextBytes()
        for (int i = off; i < max; ) {
            int rnd = random.nextInt();
            int n = Math.min(max - i, Integer.SIZE / Byte.SIZE);
            for ( ; n --> 0; ++i, rnd >>= Byte.SIZE) {
                bytes[i] = (byte)rnd;
            }
        }
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
    
    public static class OverlapDetails implements Comparable<OverlapDetails> {
        public static final Comparator<OverlapDetails> COMPARATOR = Comparator
            .comparing((OverlapDetails o) -> o.offset)
            .thenComparing(o -> o.length);
        public final int offset;
        public final int length;
        public OverlapDetails(int _offset, int _length) {
            this.offset = _offset;
            this.length = _length;
        }
        @Override
        public int hashCode() {
            return Objects.hash(this.offset, this.length);
        }
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof OverlapDetails && this.compareTo((OverlapDetails)obj) == 0);
        }
        @Override
        public int compareTo(OverlapDetails that) {
            return COMPARATOR.compare(this, that);
        }
        @Override
        public String toString() {
            return String.format("{off=%s,len=%s}", this.offset, this.length);
        }
    }
    
}
