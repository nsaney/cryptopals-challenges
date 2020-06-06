package chairosoft.cryptopals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Common {
    
    ////// Constructor //////
    private Common() { throw new UnsupportedOperationException(); }
    
    
    ////// Constants //////
    public static final Charset COMMON_CHARSET = StandardCharsets.UTF_8;
    
    
    ////// Static Methods //////
    public static byte[] fromHex(String hexText) {
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
    
    public static String toHex(byte[] data) {
        int dataLen = data.length;
        int textLen = dataLen * 2;
        StringBuilder sb = new StringBuilder(textLen);
        for (byte valueData : data) {
            String valueText = Integer.toString(valueData, 16);
            sb.append(valueText);
        }
        return sb.toString();
    }
    
}
