package chairosoft.cryptopals.set02;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/15
 */
public class Challenge15 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        int blockSize = Integer.parseInt(args[0]);
        byte[] data = fromBase64Text(args[1]);
        byte[] dataWithoutPadding = withoutPkcs7Padding(blockSize, data);
        System.out.print(toDisplayableText(dataWithoutPadding));
        System.out.flush();
    }
    
}
