package com.d6.android.app.widget.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * author : jinjiarui
 * time   : 2018/12/07
 * desc   :
 * version:
 */
public class ConvertUtils {

    public static String toString(InputStream is, String charset) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    sb.append(line).append("\n");
                }
            }
            reader.close();
            is.close();
        } catch (IOException e) {
        }
        return sb.toString();
    }

    public static String toString(InputStream is) {
        return toString(is, "utf-8");
    }
}
