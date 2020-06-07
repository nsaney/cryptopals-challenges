package chairosoft.cryptopals;

import java.util.Arrays;

import static chairosoft.cryptopals.Common.fromHex;
import static chairosoft.cryptopals.Common.toHex;

/**
 * https://cryptopals.com/sets/1/challenges/2
 */
public class Set01Challenge02 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] x = fromHex(args[0]);
        byte[] y = fromHex(args[1]);
        byte[] data = xor(x, y);
        String dataHex = toHex(data);
        System.out.print(dataHex);
    }
    
    
    ////// Static Methods //////
    public static byte xor(byte xVal, byte yVal) {
        return (byte)((xVal ^ yVal) & 0xff);
    }
    
    public static byte[] xor(byte[] x, byte[] y) {
        int maxLen = Math.max(x.length, y.length);
        byte[] xAdjusted = x.length == maxLen ? x : Arrays.copyOfRange(x, 0, maxLen);
        byte[] yAdjusted = y.length == maxLen ? y : Arrays.copyOfRange(y, 0, maxLen);
        byte[] result = new byte[maxLen];
        for (int i = 0; i < maxLen; ++i) {
            byte xVal = xAdjusted[i];
            byte yVal = yAdjusted[i];
            result[i] = xor(xVal, yVal);
        }
        return result;
    }
    
}
