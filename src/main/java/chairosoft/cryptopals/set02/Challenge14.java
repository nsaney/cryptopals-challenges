package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.set02.Challenge12.DecryptionDetails12;
import chairosoft.cryptopals.set02.Challenge12.OracleFunction12;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

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
        // 02: detect ecb
        byte[] repeaterBlock = randomBytes(blockSize);
        byte[] tripledBlocks = extendRepeat(repeaterBlock, blockSize * 3);
        byte[] encryptedDataFromTripledBlocks = oracleFn.apply(tripledBlocks);
        boolean isEcb = null != getRepeatBlockIndices(blockSize, encryptedDataFromTripledBlocks);
        // 02.b: detect prefix size
        byte[] encryptedBaseline = oracleFn.apply(new byte[0]);
        int prefixSize = 0;
        if (isEcb) {
            byte[] doubledBlocks = Arrays.copyOf(tripledBlocks, blockSize * 2);
            for (int i = 0; i < encryptedBaseline.length; ++i) {
                byte[] spacer = new byte[i];
                byte[] doubleAfterSpacer = appendBlocks(blockSize, spacer, doubledBlocks, 0, 2);
                byte[] encryptedDataFromSpacedDouble = oracleFn.apply(doubleAfterSpacer);
                int[] repeatBlockIndices = getRepeatBlockIndices(blockSize, encryptedDataFromSpacedDouble);
                if (repeatBlockIndices != null && repeatBlockIndices.length == 2) {
                    // [ppppp|ppsss|ddddd|ddddd]
                    int spacerLen = spacer.length;
                    int repeatOff = repeatBlockIndices[0];
                    prefixSize = repeatOff - spacerLen;
                    break;
                }
            }
        }
        // 03, 04, 05, 06: spacer and cycler
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int lastBlockPadding = firstBlockBarrier - 1;
        int targetSize = encryptedBaseline.length - prefixSize - lastBlockPadding;
        while (baos.size() < targetSize) {
            byte b = breakAndGetNextByte(oracleFn, blockSize, prefixSize, baos.toByteArray());
            baos.write(b);
        }
        byte[] result = baos.toByteArray();
        System.err.println("prefixSize:       " + prefixSize);
        System.err.println("lastBlockPadding: " + lastBlockPadding);
        System.err.println("result.length:    " + result.length);
        return new DecryptionDetails12(
            blockSize,
            isEcb,
            result
        );
    }
    
    public static int[] getRepeatBlockIndices(int blockSize, byte[] data) {
        int remainder = data.length % blockSize;
        int maxLen = data.length - remainder;
        for (int i = 0; i < maxLen; i += blockSize) {
            for (int j = i + blockSize; j < maxLen; j += blockSize) {
                if (areEqual(data, i, j, blockSize)) {
                    return new int[] { i, j };
                }
            }
        }
        return null;
    }
    
    public static byte breakAndGetNextByte(OracleFunction12 oracleFn, int blockSize, int prefixSize, byte[] known) throws Exception {
        int n = known.length;
        int pn = n + prefixSize;
        int matchBlockIndex = pn / blockSize;
        int matchBlockOffset = blockSize * matchBlockIndex;
        int spacerOverlap = (pn + 1) % blockSize;
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
        throw new IllegalStateException(String.format(
            "No matches after known[%s] = %s; prefixSize = %s ; spacerSize = %s ; cycler = %s",
            n,
            prefixSize,
            toDisplayableText(known),
            spacerSize,
            toDisplayableText(cycler)
        ));
    }
    
}
