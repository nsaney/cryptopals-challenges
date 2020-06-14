package chairosoft.cryptopals;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static chairosoft.cryptopals.Common.toHex;

/**
 * https://cryptopals.com/sets/1/challenges/5
 */
public class Set01Challenge05 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] data = args[0].getBytes(COMMON_CHARSET);
        byte[] key = args[1].getBytes(COMMON_CHARSET);
        byte[] output = applyRepeatingKeyXor(data, key);
        String outputText = toHex(output);
        System.out.print(outputText);
    }
    
    
    ////// Static Methods //////
    public static byte[] applyRepeatingKeyXor(byte[] data, byte[] key) {
        byte[] result = new byte[data.length];
        for (int i = 0, j = 0; i < result.length; ++i, ++j) {
            while (j >= key.length) {
                j -= key.length;
            }
            byte dataChar = data[i];
            byte xorChar = key[j];
            result[i] = Set01Challenge02.xor(dataChar, xorChar);
        }
        return result;
    }
    
}
