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
            if (valueData < 16) {
                sb.append("0");
            }
            sb.append(valueText);
        }
        return sb.toString();
    }
    
    public static String parseFromHex(String hexText) {
        byte[] data = fromHex(hexText);
        return toDisplayableText(data);
    }
    
    public static boolean isDisplayableChar(int code) {
        return ' ' <= code && code < 127;
    }
    
    public static boolean isSpecialChar(int code) {
        return !isDisplayableChar(code);
    }
    
    public static String toDisplayableText(byte... data) {
        StringBuilder sb = new StringBuilder(data.length);
        boolean inSpecialMode = false;
        for (byte b : data) {
            int code = b & 0xff;
            boolean isSpecial = isSpecialChar(code);
            boolean changeMode = isSpecial != inSpecialMode;
            inSpecialMode = isSpecial;
            if (inSpecialMode) {
                if (changeMode) {
                    sb.append("\\x[");
                }
                String s = String.format("%02x", code);
                sb.append(s);
            }
            else {
                if (changeMode) {
                    sb.append("]");
                }
                char c = (char) code;
                if (c == '\\') {
                    sb.append("\\\\");
                }
                else {
                    sb.append(c);
                }
            }
        }
        if (inSpecialMode) {
            sb.append("]");
        }
        return sb.toString();
    }
    
}
