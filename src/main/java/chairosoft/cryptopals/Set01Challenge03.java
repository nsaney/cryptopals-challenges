package chairosoft.cryptopals;

import java.util.Arrays;

import static chairosoft.cryptopals.Common.fromHex;
import static chairosoft.cryptopals.Common.toDisplayableText;
import static chairosoft.cryptopals.Set01Challenge02.xor;

/**
 * https://cryptopals.com/sets/1/challenges/3
 */
public class Set01Challenge03 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] input = fromHex(args[0]);
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; ++b) {
            SingleCharXorCipherResult xorCipherResult = new SingleCharXorCipherResult(input, b);
        }
    }
    
    
    ////// Static Methods //////
    
    
    ////// Static Inner Classes //////
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
    }
    
}
