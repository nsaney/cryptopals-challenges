package chairosoft.cryptopals.set03;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.function.Predicate;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/3/challenges/17
 */
public class Challenge17 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromBase64Text(args[0]);
        byte[] iv = fromBase64Text(args[1]);
        byte[] encrypted = fromBase64Text(args[2]);
        Predicate<byte[]> paddingCheckFn = enc -> checkPadding(enc, key, iv);
        byte[] data = decryptUsingPaddingOracle(encrypted, paddingCheckFn);
        System.out.println(toDisplayableText(data));
    }
    
    
    ////// Static Methods //////
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        int blockSize = iv.length;
        IvParameterSpec params = new IvParameterSpec(iv);
        byte[] padded = withPkcs7Padding(blockSize, data);
        return applyCipher(
            "AES",
            "CBC",
            "NoPadding",
            Cipher.ENCRYPT_MODE,
            key,
            params,
            padded
        );
    }
    
    public static boolean checkPadding(byte[] encrypted, byte[] key, byte[] iv) {
        int blockSize = iv.length;
        try {
            IvParameterSpec params = new IvParameterSpec(iv);
            byte[] decrypted = applyCipher(
                "AES",
                "CBC",
                "NoPadding",
                Cipher.DECRYPT_MODE,
                key,
                params,
                encrypted
            );
            int padding = getPkcs7PaddingLength(blockSize, decrypted);
            return padding > 0;
        }
        catch (GeneralSecurityException gsx) {
            throw new RuntimeException(gsx);
        }
    }
    
    public static byte[] decryptUsingPaddingOracle(
        byte[] encrypted,
        Predicate<byte[]> paddingOracleFn
    )
        throws GeneralSecurityException
    {
        throw new UnsupportedOperationException();
    }
    
    
}
