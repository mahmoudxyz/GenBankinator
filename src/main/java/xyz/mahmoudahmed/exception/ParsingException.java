package xyz.mahmoudahmed.exception;

/**
 * Exception for parsing errors.
 */
public class ParsingException extends GenbankConverterException {
    private final String format;
    private final String file;
    private final int line;

    /**
     * Create a new exception with a message.
     *
     * @param message The exception message
     */
    public ParsingException(String message) {
        super(message);
        this.format = null;
        this.file = null;
        this.line = -1;
    }

    /**
     * Create a new exception with a message and cause.
     *
     * @param message The exception message
     * @param cause The exception cause
     */
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
        this.format = null;
        this.file = null;
        this.line = -1;
    }

    /**
     * Create a new exception with detailed information.
     *
     * @param message The exception message
     * @param format The file format being parsed
     * @param file The file being parsed
     * @param line The line number where the error occurred
     */
    public ParsingException(String message, String format, String file, int line) {
        super(String.format("%s (Format: %s, File: %s, Line: %d)", message, format, file, line));
        this.format = format;
        this.file = file;
        this.line = line;
    }

    /**
     * Create a new exception with detailed information and cause.
     *
     * @param message The exception message
     * @param format The file format being parsed
     * @param file The file being parsed
     * @param line The line number where the error occurred
     * @param cause The exception cause
     */
    public ParsingException(String message, String format, String file, int line, Throwable cause) {
        super(String.format("%s (Format: %s, File: %s, Line: %d)", message, format, file, line), cause);
        this.format = format;
        this.file = file;
        this.line = line;
    }

    /**
     * Get the file format being parsed.
     *
     * @return The file format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Get the file being parsed.
     *
     * @return The file
     */
    public String getFile() {
        return file;
    }

    /**
     * Get the line number where the error occurred.
     *
     * @return The line number
     */
    public int getLine() {
        return line;
    }
}