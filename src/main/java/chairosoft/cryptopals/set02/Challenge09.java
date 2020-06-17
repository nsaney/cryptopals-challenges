package chairosoft.cryptopals.set02;

import java.util.Arrays;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/9
 */
public class Challenge09 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] input = fromUtf8(args[0]);
        int blockSize = Integer.parseInt(args[1]);
        byte[] output = applyPkcs7(blockSize, input);
        String outputText = toDisplayableText(output);
        System.out.println(outputText);
    }
    
    
    ////// Static Methods //////
    public static byte[] applyPkcs7(int blockSize, byte... data) {
        int remainder = data.length % blockSize;
        int paddingLength = blockSize - remainder;
        byte paddingByte = (byte)(paddingLength & 0xff);
        int updatedLength = data.length + paddingLength;
        byte[] result = Arrays.copyOf(data, updatedLength);
        for (int i = data.length; i < updatedLength; ++i) {
            result[i] = paddingByte;
        }
        return result;
    }
    
}
