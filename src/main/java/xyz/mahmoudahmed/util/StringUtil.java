package xyz.mahmoudahmed.util;

/**
 * Utility for string and text operations.
 */
public class StringUtil {
    private StringUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Truncates a string to a specified length.
     *
     * @param str    The string to truncate
     * @param length The desired length
     * @return The truncated string
     */
    public static String truncate(String str, int length) {
        if (str == null) {
            return null;
        }

        if (str.length() <= length) {
            return str;
        }

        return str.substring(0, length);
    }

    /**
     * Pads a string on the right with spaces to a specified length.
     *
     * @param str    The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String rightPad(String str, int length) {
        if (str == null) {
            return repeat(" ", length);
        }

        int padLength = length - str.length();
        if (padLength <= 0) {
            return str;
        }

        return str + repeat(" ", padLength);
    }

    /**
     * Pads a string on the left with spaces to a specified length.
     *
     * @param str    The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String leftPad(String str, int length) {
        if (str == null) {
            return repeat(" ", length);
        }

        int padLength = length - str.length();
        if (padLength <= 0) {
            return str;
        }

        return repeat(" ", padLength) + str;
    }

    /**
     * Check if a string is null, empty, or contains only whitespace.
     *
     * @param text The string to check
     * @return true if the string is blank
     */
    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Check if a string is not null, empty, or contains only whitespace.
     *
     * @param text The string to check
     * @return true if the string is not blank
     */
    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    /**
     * Repeats a string a specified number of times.
     *
     * @param str   The string to repeat
     * @param times The number of times to repeat
     * @return The repeated string
     */
    public static String repeat(String str, int times) {
        if (str == null) {
            return null;
        }

        if (times <= 0) {
            return "";
        }

        StringBuilder result = new StringBuilder(str.length() * times);
        for (int i = 0; i < times; i++) {
            result.append(str);
        }

        return result.toString();
    }
}
