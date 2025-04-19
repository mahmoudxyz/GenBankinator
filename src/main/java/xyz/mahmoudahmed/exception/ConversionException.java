package xyz.mahmoudahmed.exception;

/**
 * Exception for conversion errors.
 */
public class ConversionException extends GenbankConverterException {
    /**
     * Create a new exception with a message.
     *
     * @param message The exception message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and cause.
     *
     * @param message The exception message
     * @param cause The exception cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}