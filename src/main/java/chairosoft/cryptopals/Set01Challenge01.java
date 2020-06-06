package chairosoft.cryptopals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * https://cryptopals.com/sets/1/challenges/1
 */
public class Set01Challenge01 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        String input = args[0];
        byte[] data = parseHex(input);
        byte[] dataBase64 = Base64.getEncoder().encode(data);
        System.out.write(dataBase64);
    }
    
    public static byte[] parseHex(String hexText) {
        int textLen = hexText.length();
        int dataLen = textLen / 2;
        byte[] data = new byte[dataLen];
        for (int i = 0, j = 0; j < dataLen; i += 2, ++j) {
            String valueText = hexText.substring(i, i + 2);
            int valueDataInt = Integer.parseInt(valueText, 16);
            byte valueData = (byte)valueDataInt;
            data[j] = valueData;
        }
        return data;
    }
    
}
