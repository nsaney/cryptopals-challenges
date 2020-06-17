package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.set01.Challenge08;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/12
 */
public class Challenge12 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] unknownData = fromBase64Text(args[0]);
        byte[] key = fromBase64Text(args[1]);
        OracleFunction12 oracleFn = d -> encryptAes128Ecb(d, unknownData, key);
        DecryptionDetails12 decryptionDetails = getDecryptionDetails(oracleFn);
        System.out.println(decryptionDetails);
    }
    
    
    ////// Static Methods //////
    public static byte[] encryptAes128Ecb(byte[] knownData, byte[] unknownData, byte[] key) throws GeneralSecurityException {
        int combinedLength = knownData.length + unknownData.length;
        byte[] combinedInput = new byte[combinedLength];
        System.arraycopy(knownData, 0, combinedInput, 0, knownData.length);
        System.arraycopy(unknownData, 0, combinedInput, knownData.length, unknownData.length);
        return applyCipher("AES", "ECB", "PKCS5Padding", Cipher.ENCRYPT_MODE, key, combinedInput);
    }
    
    public static DecryptionDetails12 getDecryptionDetails(OracleFunction12 oracleFn) throws Exception {
        // 01: block size
        byte[] baseline = oracleFn.apply(new byte[0]);
        int inputSize_01 = 0;
        int outputSize_01a = baseline.length;
        for ( ; outputSize_01a == baseline.length; ++inputSize_01) {
            byte[] knownData_01a = new byte[inputSize_01];
            byte[] output_01a = oracleFn.apply(knownData_01a);
            outputSize_01a = output_01a.length;
        }
        int blockSize = 0;
        int outputSize_01b = outputSize_01a;
        for ( ; outputSize_01b == outputSize_01a; ++inputSize_01, ++blockSize) {
            byte[] knownData_01b = new byte[inputSize_01];
            byte[] output_01b = oracleFn.apply(knownData_01b);
            outputSize_01b = output_01b.length;
        }
        // 02: detect ecb
        byte[] knownData_02 = new byte[blockSize * 2];
        for (int i = 0; i < knownData_02.length; ++i) {
            int m = i % blockSize;
            byte b = (byte)(m & 0xff);
            knownData_02[i] = b;
        }
        byte[] output_02 = oracleFn.apply(knownData_02);
        boolean isEcb = Challenge08.hasRepeatBlocks(output_02, blockSize);
        // 03: TODO
        return new DecryptionDetails12(
            blockSize,
            isEcb,
            fromUtf8("----TODO----")
        );
    }
    
    
    ////// Static Methods //////
    public interface OracleFunction12 {
        byte[] apply(byte[] knownData) throws GeneralSecurityException;
    }
    public static class DecryptionDetails12 {
        public final int blockSize;
        public final boolean isEcb;
        public final byte[] decryptedData;
        public DecryptionDetails12(int _blockSize, boolean _isEcb, byte[] _decryptedData) {
            this.blockSize = _blockSize;
            this.isEcb = _isEcb;
            this.decryptedData = _decryptedData;
        }
        public String toString() {
            return String.format(
                "[blockSize=%s][ECB=%s]: %s",
                this.blockSize,
                this.isEcb,
                toDisplayableText(this.decryptedData)
            );
        }
    }
    
}
