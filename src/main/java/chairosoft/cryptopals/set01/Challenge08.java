package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.Common.*;

import java.util.List;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/8
 */
public class Challenge08 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        List<byte[]> dataLines = readFileLinesHex(args[0]);
        for (int i = 0; i < dataLines.size(); ++i) {
            int line = i + 1;
            byte[] data = dataLines.get(i);
            CipherResult<?, ?> cipherResult = Challenge06.breakRepeatingKeyXor(data, 1, Math.min(48, data.length / 3));
            if (cipherResult != null) {
                System.out.printf("Line #%04d: %s\n", line, cipherResult);
            }
            if (hasRepeatBlocks(data, 16, true)) {
                System.out.printf("Line #%04d: %s\n", line, toHex(data));
            }
        }
    }
    
    
    ////// Static Methods //////
    public static boolean hasRepeatBlocks(byte[] data, int blockSize) {
        return hasRepeatBlocks(data, blockSize, false);
    }
    
    public static boolean hasRepeatBlocks(byte[] data, int blockSize, boolean showDebug) {
        int remainder = data.length % blockSize;
        int maxLen = data.length - remainder;
        for (int i = 0; i < maxLen; i += blockSize) {
            for (int j = i + blockSize; j < maxLen; j += blockSize) {
                if (areEqual(data, i, j, blockSize)) {
                    if (showDebug) {
                        System.err.printf(
                            "Found match between [%04d:%04d] and [%04d:%04d]: %s\n\n",
                            i,
                            i + blockSize,
                            j,
                            j + blockSize,
                            toHex(data, i, blockSize)
                        );
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
