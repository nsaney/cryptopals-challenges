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
        int firstBlockBarrier = countBytesUntilNewBlock(oracleFn, 0);
        int blockSize = countBytesUntilNewBlock(oracleFn, firstBlockBarrier);
        // 02: detect ecb
        byte[] repeaterBlock = randomBytes(blockSize);
        byte[] doubledBlocks = extendRepeat(repeaterBlock, blockSize * 2);
        byte[] encryptedDataFromDoubledBlocks = oracleFn.apply(doubledBlocks);
        boolean isEcb = Challenge08.hasRepeatBlocks(encryptedDataFromDoubledBlocks, blockSize);
        // 03: TODO
        return new DecryptionDetails12(
            blockSize,
            isEcb,
            fromUtf8("----TODO----")
        );
    }
    
    public static int countBytesUntilNewBlock(OracleFunction12 oracleFn, int initialInputSize) throws Exception {
        byte[] knownData = new byte[initialInputSize];
        byte[] base = oracleFn.apply(knownData);
        byte[] curr = base;
        int count = 1;
        for (int in = initialInputSize + 1; curr.length == base.length; ++in, ++count) {
            knownData = new byte[in];
            curr = oracleFn.apply(knownData);
        }
        return count;
    }
    
    
    ////// Static Inner Classes //////
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
