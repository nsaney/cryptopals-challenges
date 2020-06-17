package chairosoft.cryptopals.set02;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.util.Random;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/11
 */
public class Challenge11 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] input = fromBase64Text(args[0], true);
        RandomEncryptionResult result = randomlyEncrypt(input);
        System.err.println(result.algorithmMode);
        System.out.println(toBase64Text(result.encryptedData));
    }
    
    
    ////// Static Methods //////
    public static byte[] generateRandomAesKey() {
        byte[] result = new byte[16];
        randomBytes(result);
        return result;
    }
    
    public static RandomEncryptionResult randomlyEncrypt(byte[] input) throws GeneralSecurityException {
        Random random = THREAD_LOCAL_RANDOM.get();
        int prependCount = 5 + random.nextInt(6);
        int appendCount = 5 + random.nextInt(6);
        int modifiedLength = prependCount + input.length + appendCount;
        byte[] modifiedInput = new byte[modifiedLength];
        randomBytes(modifiedInput, 0, prependCount);
        randomBytes(modifiedInput, (prependCount + input.length), appendCount);
        System.arraycopy(input, 0, modifiedInput, prependCount, input.length);
        boolean useEcb = random.nextBoolean();
        String algorithmMode = useEcb ? "ECB" : "CBC";
        byte[] key = generateRandomAesKey();
        byte[] result;
        if (useEcb) {
            result = applyCipher("AES", algorithmMode, "PKCS5Padding", Cipher.ENCRYPT_MODE, key, modifiedInput);
        }
        else {
            byte[] iv = generateRandomAesKey();
            result = Challenge10.encryptAesCbc(modifiedInput, key, iv);
        }
        return new RandomEncryptionResult(algorithmMode, result);
    }
    
    
    ////// Static Inner Classes //////
    public static class RandomEncryptionResult {
        public final String algorithmMode;
        public final byte[] encryptedData;
        public RandomEncryptionResult(String _algorithmUsed, byte[] _encryptedData) {
            this.algorithmMode = _algorithmUsed;
            this.encryptedData = _encryptedData;
        }
    }
}
