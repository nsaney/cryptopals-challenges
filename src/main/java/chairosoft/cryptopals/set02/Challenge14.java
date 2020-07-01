package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.set01.Challenge08;
import chairosoft.cryptopals.set02.Challenge12.DecryptionDetails12;
import chairosoft.cryptopals.set02.Challenge12.OracleFunction12;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/14
 */
public class Challenge14 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] prefixData = fromBase64Text(args[0]);
        byte[] unknownData = fromBase64Text(args[1]);
        byte[] key = fromBase64Text(args[2]);
        OracleFunction12 oracleFn = d -> encryptAes128EcbWithPrefix(prefixData, d, unknownData, key);
        DecryptionDetails12 decryptionDetails = getDecryptionDetailsConsideringPrefix(oracleFn);
        System.out.println(decryptionDetails);
    }
    
    
    ////// Static Methods //////
    public static byte[] encryptAes128EcbWithPrefix(byte[] prefix, byte[] knownData, byte[] unknownData, byte[] key) throws GeneralSecurityException {
        int combinedLength = prefix.length + knownData.length + unknownData.length;
        byte[] combinedInput = new byte[combinedLength];
        System.arraycopy(prefix, 0, combinedInput, 0, prefix.length);
        System.arraycopy(knownData, 0, combinedInput, prefix.length, knownData.length);
        System.arraycopy(unknownData, 0, combinedInput, prefix.length + knownData.length, unknownData.length);
        return applyCipher("AES", "ECB", "PKCS5Padding", Cipher.ENCRYPT_MODE, key, combinedInput);
    }
    
    public static DecryptionDetails12 getDecryptionDetailsConsideringPrefix(OracleFunction12 oracleFn) throws Exception {
        // 01: block size
        int firstBlockBarrier = Challenge12.countBytesUntilNewBlock(oracleFn, 0);
        int blockSize = Challenge12.countBytesUntilNewBlock(oracleFn, firstBlockBarrier);
        System.err.println("firstBlockBarrier: " + firstBlockBarrier);
        // 02: detect ecb
        byte[] repeaterBlock = randomBytes(blockSize);
        byte[] doubledBlocks = extendRepeat(repeaterBlock, blockSize * 3);
        byte[] encryptedDataFromDoubledBlocks = oracleFn.apply(doubledBlocks);
        boolean isEcb = Challenge08.hasRepeatBlocks(encryptedDataFromDoubledBlocks, blockSize);
        // 03, 04, 05, 06: spacer and cycler
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] encryptedBaseline = oracleFn.apply(new byte[0]);
        int lastBlockGap = firstBlockBarrier - 1;
        int targetSize = encryptedBaseline.length - lastBlockGap;
        while (baos.size() < targetSize) {
            byte b = breakAndGetNextByte(oracleFn, blockSize, baos.toByteArray());
            baos.write(b);
        }
        byte[] result = baos.toByteArray();
        return new DecryptionDetails12(
            blockSize,
            isEcb,
            result
        );
    }
    
    public static byte breakAndGetNextByte(OracleFunction12 oracleFn, int blockSize, byte[] known) throws Exception {
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
                return b;
            }
        }
        return 0;
    }
    
}
