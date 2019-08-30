package com.quicsolv.qsspoofer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int getDecimalForHex(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }


    public static byte[] hexToByte(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static List<ExtractedDataResponse> extractData(String hexString) {

        Log.d("Hex", hexString);
        int count = 0;
        String hexCount = "";
        List<ExtractedDataResponse> columns = new ArrayList<>();
        for (int i = 0; i < hexString.length() / 2;) {
            String s = String.valueOf(hexString.charAt(i*2)) + String.valueOf(hexString.charAt(i*2 + 1));
            int t = getDecimalForHex(s);
            if (count == 0) {
                count = t;
                hexCount = s;
                i++;
                continue;
            }
            ExtractedDataResponse column = new ExtractedDataResponse();
            column.setLength(hexCount);
            int counter = 0;
            String col = "";
            while (counter < count && i*2 != hexString.length()) {
                s = String.valueOf(hexString.charAt(i*2)) + String.valueOf(hexString.charAt(i++*2 + 1));
                col = col.concat(s);
                counter++;
            }
            column.setType(col.substring(0, 2));
            column.setValue(col.substring(2));
            count = 0;
            columns.add(column);
        }
        return columns;
    }

}
