package chairosoft.cryptopals;

import chairosoft.cryptopals.Set01Challenge03.SingleCharXorCipherResult;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.fromBase64;

/**
 * https://cryptopals.com/sets/1/challenges/4
 */
public class Set01Challenge06 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        File inputFile = new File(args[0]);
        byte[] dataBase64 = Files.readAllBytes(inputFile.toPath());
        byte[] data = fromBase64(dataBase64, true);
        String output = breakRepeatingKeyXor(data);
        System.out.print(output);
    }
    
    
    ////// Static Methods //////
    public static String breakRepeatingKeyXor(byte[] data) {
        return "tbd";
    }
    
    public static int hammingDistance(byte[] x, byte[] y) {
        boolean isXShorter = x.length < y.length;
        int minLength = isXShorter ? x.length : y.length;
        int maxLength = isXShorter ? y.length : x.length;
        int overflowBytes = maxLength - minLength;
        int overflowBits = 8 * overflowBytes;
        int distanceBits = 0;
        for (int i = 0; i < minLength; ++i) {
            byte xi = x[i];
            byte yi = y[i];
            int currenXor = (xi ^ yi) & 0xff;
            int currentDistanceBits = Integer.bitCount(currenXor);
            distanceBits += currentDistanceBits;
        }
        return distanceBits + overflowBits;
    }
    
}
