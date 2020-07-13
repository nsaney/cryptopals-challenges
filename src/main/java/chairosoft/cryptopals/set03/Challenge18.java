package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.Common;

import java.nio.ByteOrder;
import java.security.GeneralSecurityException;

import static chairosoft.cryptopals.Common.fromBase64Text;
import static chairosoft.cryptopals.Common.toDisplayableText;

/**
 * https://cryptopals.com/sets/3/challenges/18
 */
public class Challenge18 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromBase64Text(args[0]);
        byte[] nonce = fromBase64Text(args[1]);
        boolean littleEndianBlockCount = Boolean.parseBoolean(args[2]);
        ByteOrder byteOrder = littleEndianBlockCount ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        byte[] data = fromBase64Text(args[3]);
        byte[] output = applyCounterCipher(key, nonce, byteOrder, data);
        System.out.println(toDisplayableText(output));
    }
    
    
    ////// Static Methods //////
    public static byte[] applyCounterCipher(
        byte[] key,
        byte[] nonce,
        ByteOrder byteOrder,
        byte[] input
    )
        throws GeneralSecurityException
    {
        return Common.applyCounterCipher(
            "AES",
            "ECB",
            "NoPadding",
            key,
            null,
            nonce,
            byteOrder,
            input
        );
    }
    
}
