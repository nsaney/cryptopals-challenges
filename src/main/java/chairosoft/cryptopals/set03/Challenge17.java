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
        byte[] data = decryptUsingPaddingOracle(iv, encrypted, paddingCheckFn);
        System.out.println(toDisplayableText(data));
        debug.println(toBlockedHex(iv.length, data));
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
        byte[] iv,
        byte[] encrypted,
        Predicate<byte[]> paddingCheckFn
    )
        throws Exception
    {
        int blockSize = iv.length;
        byte[] decrypted = new byte[encrypted.length];
        int modLength = blockSize * 2;
        byte[] mod = new byte[modLength];
        debug.println("IV        : " + toBlockedHex(blockSize, iv));
        debug.println("Encrypted : " + toBlockedHex(blockSize, encrypted));
        for (int i = encrypted.length, n = 0; (i -= blockSize) >= 0; ++n) {
            if (i > 0) {
                System.arraycopy(encrypted, i - blockSize, mod, 0, modLength);
            }
            else {
                System.arraycopy(iv, 0, mod, 0, blockSize);
                System.arraycopy(encrypted, i, mod, blockSize, blockSize);
            }
            debug.printf("Mod[%2s]   : %s\n", i / blockSize, toBlockedHex(blockSize, mod));
            for (byte padByte = 1; padByte <= blockSize; ++padByte) {
                int j = blockSize - padByte;
                for (int k = j + 1; k < blockSize; ++k) {
                    mod[k] ^= padByte - 1;
                    mod[k] ^= padByte;
                }
                byte orig = mod[j];
                for (int x = 256; x --> 0; ) {
                    mod[j] = (byte)x;
                    mod[j] ^= orig;
                    boolean goodPadding = paddingCheckFn.test(mod);
                    if (goodPadding) {
                        byte d = mod[j];
                        d ^= orig;
                        d ^= padByte;
                        decrypted[i + j] = d;
                        break;
                    }
                }
                debug.printf(
                    "Mod[%2s;%2s]: %s --> %s\n",
                    i / blockSize,
                    padByte,
                    toBlockedHex(blockSize, mod),
                    toBlockedHex(blockSize, decrypted)
                );
            }
        }
        return withoutPkcs7Padding(blockSize, decrypted);
    }
    
    
}
