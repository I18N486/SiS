package com.zst.msclibrary.transfermsc.openapi.utils;

/*
 * Auth: DELL-5490
 * Date: 2018/11/2
 */
public class StringUtils {

    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String LF = "\n";
    public static final String CR = "\r";
    public static final int INDEX_NOT_FOUND = -1;
    private static final int PAD_LIMIT = 8192;

    public static boolean isEmpty(CharSequence cs)
    {
        return (cs == null) || (cs.length() == 0);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen = 0;
        if ((cs == null) || ((strLen = cs.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix)
    {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase)
    {
        if ((str == null) || (suffix == null)) {
            return (str == null) && (suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return CharSequenceUtils.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }
}