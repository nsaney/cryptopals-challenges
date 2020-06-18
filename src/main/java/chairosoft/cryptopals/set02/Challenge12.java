package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.set01.Challenge08;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

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
        // 03, 04, 05, 06: spacer and cycler
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] encryptedBaseline = oracleFn.apply(new byte[0]);
        int lastBlockGap = firstBlockBarrier - 1;
        int targetSize = encryptedBaseline.length - lastBlockGap;
        while (baos.size() < targetSize) {
            breakNextByte(oracleFn, blockSize, baos);
        }
        byte[] result = baos.toByteArray();
        return new DecryptionDetails12(
            blockSize,
            isEcb,
            result
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
    
    public static void breakNextByte(OracleFunction12 oracleFn, int blockSize, ByteArrayOutputStream baos) throws Exception {
        byte[] known = baos.toByteArray();
        int n = known.length;
        int matchBlockIndex = n / blockSize;
        int matchBlockOffset = blockSize * matchBlockIndex;
        int spacerOverlap = (n + 1) % blockSize;
        int spacerSize = (blockSize - spacerOverlap) % blockSize;
        int cyclerSize = spacerSize + n + 1;
        byte[] spacer = new byte[spacerSize];
        byte[] encryptedFromSpacer = oracleFn.apply(spacer);
        byte[] cycler = new byte[cyclerSize];
        System.arraycopy(known, 0, cycler, spacerSize, n);
        for (int x = Byte.MIN_VALUE; x <= Byte.MAX_VALUE; ++x) {
            byte b = (byte)x;
            cycler[cyclerSize - 1] = b;
            byte[] encryptedFromCycler = oracleFn.apply(cycler);
            if (areEqual(encryptedFromSpacer, matchBlockOffset, encryptedFromCycler, matchBlockOffset, blockSize)) {
                baos.write(b);
                return;
            }
        }
        throw new IllegalStateException(String.format(
            "No matches after known[%s] = %s ; spacerSize = %s ; cycler = %s",
            n,
            toDisplayableText(known),
            spacerSize,
            toDisplayableText(cycler)
        ));
    }
    
    // blockSize 4
    // [0 0 0|z ...] spacer          n = 0
    // [0 0 0 c|...] cycler
    //
    // [0 0|z y ...] spacer          n = 1
    // [0 0 z c|...] cycler
    //
    // [0|z y x ...] spacer          n = 2
    // [0 z y c|...] cycler
    //
    // [z y x w ...] spacer          n = 3
    // [z y x c|...] cycler
    //
    // [0 0 0|z y x w v ...] spacer  n = 4
    // [0 0 0 z;y x w c|...] cycler
    
    
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
