package chairosoft.cryptopals.set01;

import javax.crypto.Cipher;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/7
 */
public class Challenge07 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] data = readFileBase64(args[0]);
        byte[] key = fromUtf8(args[1]);
        byte[] output = applyCipher("AES", "ECB", "NoPadding", Cipher.DECRYPT_MODE, key, data);
        String outputText = toDisplayableText(output);
        System.out.println(outputText);
    }
    
}
