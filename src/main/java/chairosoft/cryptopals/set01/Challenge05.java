package chairosoft.cryptopals.set01;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/5
 */
public class Challenge05 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] data = fromUtf8(args[0]);
        byte[] key = fromUtf8(args[1]);
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
            result[i] = xor(dataChar, xorChar);
        }
        return result;
    }
    
}
