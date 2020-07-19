package chairosoft.cryptopals.set03;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/3/challenges/19
 */
public class Challenge19 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] key = fromBase64Text(args[0]);
        byte[] nonce = fromBase64Text(args[1]);
        boolean littleEndianBlockCount = Boolean.parseBoolean(args[2]);
        ByteOrder byteOrder = littleEndianBlockCount ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        File inputsFile = new File(args[3]);
        List<String> inputLines = Files.readAllLines(inputsFile.toPath(), COMMON_CHARSET);
        int inputCount = inputLines.size();
        List<byte[]> inputs = new ArrayList<>(inputCount);
        for (String inputLine : inputLines) { inputs.add(fromBase64Text(inputLine)); }
        EncryptFunction encryptFn = data -> Challenge18.applyCounterCipher(key, nonce, byteOrder, data);
        List<byte[]> encryptedItems = new ArrayList<>(inputCount);
        for (byte[] input : inputs) { encryptedItems.add(encryptFn.encrypt(input)); }
        List<byte[]> decryptedItems = breakFixedNonceCtr(encryptedItems);
        for (int i = 0; i < decryptedItems.size(); ++i) {
            byte[] decrypted = decryptedItems.get(i);
            System.out.printf("%2s ==>> %s\n", (i + 1), toDisplayableText(decrypted));
        }
    }
    
    
    ////// Static Methods //////
    public static List<byte[]> breakFixedNonceCtr(List<byte[]> encryptedItems) throws Exception {
        encryptedItems.forEach(enc -> System.err.println(toBlockedHex(16, enc)));
        return encryptedItems;
    }
    
}
