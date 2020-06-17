package chairosoft.cryptopals.set01;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/1/challenges/2
 */
public class Challenge02 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        byte[] x = fromHex(args[0]);
        byte[] y = fromHex(args[1]);
        byte[] data = xor(x, y);
        String dataHex = toHex(data);
        System.out.print(dataHex);
    }
    
}
