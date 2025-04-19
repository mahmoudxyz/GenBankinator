package xyz.mahmoudahmed.util;

/**
 * Utility for string and text operations.
 */
public class StringUtil {
    private StringUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Truncate a string to a maximum length.
     *
     * @param text The string to truncate
     * @param maxLength The maximum length
     * @return The truncated string
     */
    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }

    /**
     * Pad a string with spaces to a fixed length, right-aligned.
     *
     * @param text The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String rightPad(String text, int length) {
        if (text == null) {
            text = "";
        }
        int padding = length - text.length();
        if (padding <= 0) {
            return text;
        }
        return text + " ".repeat(padding);
    }

    /**
     * Pad a string with spaces to a fixed length, left-aligned.
     *
     * @param text The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String leftPad(String text, int length) {
        if (text == null) {
            text = "";
        }
        int padding = length - text.length();
        if (padding <= 0) {
            return text;
        }
        return " ".repeat(padding) + text;
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
     * Repeat a string a specified number of times.
     *
     * @param text The string to repeat
     * @param count The number of times to repeat
     * @return The repeated string
     */
    public static String repeat(String text, int count) {
        if (text == null || count <= 0) {
            return "";
        }
        return text.repeat(count);
    }
}
