package chairosoft.cryptopals;

import javax.crypto.Cipher;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/7
 */
public class Set01Challenge07 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] data = readFileBase64(args[0]);
        byte[] key = args[1].getBytes(COMMON_CHARSET);
        byte[] output = applyCipher("AES", "ECB", "NoPadding", Cipher.DECRYPT_MODE, key, data);
        String outputText = toDisplayableText(output);
        System.out.println(outputText);
    }
    
}
