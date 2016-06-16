package tools;

public class Tools {
	
	//Convert bytes to hexadecimal as String output
    public static String bytesToHex(byte[] data, int length) {
        String digits = "0123456789ABCDEF";
        StringBuffer buffer = new StringBuffer();
 
        for (int i = 0; i != length; i++) {
            int v = data[i] & 0xff;
 
            buffer.append(digits.charAt(v >> 4));
            buffer.append(digits.charAt(v & 0xf));
        }
 
        return buffer.toString();
    }
 
   //Count data length
    public static String bytesToHex(byte[] data) {
        return bytesToHex(data, data.length);
    }
 
    //Convert hexadecimal to bytes as a byte array output
    public static byte[] hexToBytes(String string) {
        int length = string.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character
                    .digit(string.charAt(i + 1), 16));
        }
        return data;
    }
}
