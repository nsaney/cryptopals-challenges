package chairosoft.cryptopals;

import javax.crypto.Cipher;

import java.security.GeneralSecurityException;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/10
 */
public class Set02Challenge10 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] data = readFileBase64(args[0]);
        byte[] key = fromUtf8(args[1]);
        byte[] iv = fromUtf8(args[2]);
        byte[] output = decryptAesCbc(data, key, iv);
        System.out.write(output);
        System.out.println();
    }
    
    
    ////// Static Methods //////
    public static byte[] encryptAesCbc(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        int blockSize = iv.length;
        byte[] paddedData = Set02Challenge09.applyPkcs7(blockSize, data);
        byte[] currentCombinedBlock = new byte[blockSize];
        byte[] result = new byte[paddedData.length];
        for (int i = 0; i < result.length; i += blockSize) {
            byte[] previousResult = (i == 0) ? iv : result;
            int previousOffset = (i == 0) ? 0 : (i - blockSize);
            xor(paddedData, i, previousResult, previousOffset, currentCombinedBlock, 0, blockSize);
            applyCipher(
                "AES",
                "ECB",
                "NoPadding",
                Cipher.ENCRYPT_MODE,
                key,
                currentCombinedBlock,
                0,
                result,
                i,
                blockSize
            );
        }
        return result;
    }
    
    public static byte[] decryptAesCbc(byte[] data, byte[] key, byte[] iv) throws GeneralSecurityException {
        int blockSize = iv.length;
        byte[] currentDecryptedBlock = new byte[blockSize];
        byte[] result = new byte[data.length];
        for (int i = data.length - blockSize; i >= 0; i -= blockSize) {
            applyCipher(
                "AES",
                "ECB",
                "NoPadding",
                Cipher.DECRYPT_MODE,
                key,
                data,
                i,
                currentDecryptedBlock,
                0,
                blockSize
            );
            byte[] previousData = (i == 0) ? iv : data;
            int previousOffset = (i == 0) ? 0 : (i - blockSize);
            xor(currentDecryptedBlock, 0, previousData, previousOffset, result, i, blockSize);
        }
        return result;
    }
    
}
