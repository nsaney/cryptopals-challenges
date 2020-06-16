package chairosoft.cryptopals;

import chairosoft.cryptopals.Common.*;

import java.util.List;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/8
 */
public class Set01Challenge08 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        List<byte[]> dataLines = readFileLinesHex(args[0]);
        for (int i = 0; i < dataLines.size(); ++i) {
            int line = i + 1;
            byte[] data = dataLines.get(i);
            CipherResult<?, ?> cipherResult = Set01Challenge06.breakRepeatingKeyXor(data, 1, Math.min(48, data.length / 3));
            if (cipherResult != null) {
                System.out.printf("Line #%04d: %s\n", line, cipherResult);
            }
            if (hasRepeatBlocks(data, 16)) {
                System.out.printf("Line #%04d: %s\n", line, toHex(data));
            }
        }
    }
    
    
    ////// Static Methods //////
    public static boolean hasRepeatBlocks(byte[] data, int blockSize) {
        int remainder = data.length % blockSize;
        int maxLen = data.length - remainder;
        for (int i = 0; i < maxLen; i += blockSize) {
            for (int j = i + blockSize; j < maxLen; j += blockSize) {
                int hammingDistance = Set01Challenge06.hammingDistance(data, i, j, blockSize);
                if (hammingDistance == 0) {
                    System.err.printf(
                        "Found match between [%04d:%04d] and [%04d:%04d]: %s\n\n",
                        i,
                        i + blockSize,
                        j,
                        j + blockSize,
                        toHex(data, i, blockSize)
                    );
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
