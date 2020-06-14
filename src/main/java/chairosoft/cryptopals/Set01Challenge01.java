package chairosoft.cryptopals;

import static chairosoft.cryptopals.Common.fromHex;
import static chairosoft.cryptopals.Common.toBase64Text;

/**
 * https://cryptopals.com/sets/1/challenges/1
 */
public class Set01Challenge01 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        String input = args[0];
        byte[] data = fromHex(input);
        String dataBase64Text = toBase64Text(data);
        System.out.print(dataBase64Text);
    }
    
}
