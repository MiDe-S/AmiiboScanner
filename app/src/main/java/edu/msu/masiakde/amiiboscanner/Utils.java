package edu.msu.masiakde.amiiboscanner;

public class Utils {
    public static String bytesToHexString(byte[] bytes, boolean spaces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0 && spaces) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }
}

