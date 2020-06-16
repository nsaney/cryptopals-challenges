package chairosoft.cryptopals;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/7
 */
public class Set01Challenge07 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        File inputFile = new File(args[0]);
        byte[] dataBase64 = Files.readAllBytes(inputFile.toPath());
        byte[] data = fromBase64(dataBase64, true);
        byte[] key = args[1].getBytes(COMMON_CHARSET);
        byte[] output = applyCipher("AES", "ECB", "NoPadding", Cipher.DECRYPT_MODE, key, data);
        String outputText = toDisplayableText(output);
        System.out.println(outputText);
    }
    
}
