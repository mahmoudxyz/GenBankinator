package xyz.mahmoudahmed.exception;

/**
 * Exception for invalid file formats.
 */
public class InvalidFileFormatException extends GenbankConverterException {
    private final String format;

    /**
     * Create a new exception with a message and format.
     *
     * @param message The exception message
     * @param format The invalid file format
     */
    public InvalidFileFormatException(String message, String format) {
        super(message);
        this.format = format;
    }

    /**
     * Create a new exception with a message, format, and cause.
     *
     * @param message The exception message
     * @param format The invalid file format
     * @param cause The exception cause
     */
    public InvalidFileFormatException(String message, String format, Throwable cause) {
        super(message, cause);
        this.format = format;
    }

    /**
     * Get the invalid file format.
     *
     * @return The invalid file format
     */
    public String getFormat() {
        return format;
    }
}
