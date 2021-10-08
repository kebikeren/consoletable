package com.kebikeren.consoletable.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @Classname StringUtils
 * @Description 支持中文字符串的各种方法
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class MyStringUtils {
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int length = getByteLength(str);
            int pads = size - length;
            if (pads <= 0) {
                return str;
            } else {
                return str + StringUtils.repeat(padChar, pads);
            }
        }
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int length = getByteLength(str);
            int pads = size - length;
            if (pads <= 0) {
                return str;
            } else {
                return StringUtils.repeat(padChar, pads) + str;
            }
        }
    }

    public static String center(String str, int size, char padChar) {
        if (str != null && size > 0) {
            int strLen = getByteLength(str);
            int pads = size - strLen;
            if (pads <= 0) {
                return str;
            } else {
                str = leftPad(str, strLen + pads / 2, padChar);
                str = rightPad(str, size, padChar);
                return str;
            }
        } else {
            return str;
        }
    }

    public static int getByteLength(String text) {
        int sum = 0;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c > 255)
                sum += 2;
            else
                sum += 1;
        }

        return sum;
    }

    public static void breakString(String text, int length, List<String> list) {
        if (text == null || text.length() == 0)
            return;

        String[] items = text.split("[\\r\\n]+");
        for (String item : items)
            breakLine(item, length, list);
    }

    private static void breakLine(String line, int length, List<String> list) {
        int lineLength = line.length();
        if (lineLength == 0) {
            list.add("");
            return;
        }

        int counter = 0, start = 0, end = 0;
        for (int i = 0; i < lineLength; i++) {
            end = i;
            char c = line.charAt(i);
            int temp = 0;
            if (c > 255) {
                temp = 2;
            } else {
                temp = 1;
            }
            counter += temp;

            if (counter > length) {
                list.add(line.substring(start, end));
                start = end;
                counter = temp;
            }
        }

        if (end >= start)
            list.add(line.substring(start, end + 1));
    }

    public static void breakString(String text, int length, StringBuilder sb) {
        List<String> lines = new LinkedList<>();
        breakString(text, length, lines);

        int i = 0;
        for (String line : lines) {
            if (i != 0)
                sb.append(System.lineSeparator());
            sb.append(line);
            i++;
        }
    }

    public static String breakString(String text, int length) {
        StringBuilder sb = new StringBuilder();
        breakString(text, length, sb);

        return sb.toString();
    }
}
