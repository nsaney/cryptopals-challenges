package chairosoft.cryptopals;

import java.util.Base64;

import static chairosoft.cryptopals.Common.fromHex;

/**
 * https://cryptopals.com/sets/1/challenges/1
 */
public class Set01Challenge01 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        String input = args[0];
        byte[] data = fromHex(input);
        byte[] dataBase64 = Base64.getEncoder().encode(data);
        System.out.write(dataBase64);
    }
    
}
