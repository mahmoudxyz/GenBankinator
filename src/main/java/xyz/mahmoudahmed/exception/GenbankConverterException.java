package xyz.mahmoudahmed.exception;
/**
 * Base exception for all GenBank converter exceptions.
 */
public class GenbankConverterException extends RuntimeException {
    /**
     * Create a new exception with a message.
     *
     * @param message The exception message
     */
    public GenbankConverterException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and cause.
     *
     * @param message The exception message
     * @param cause The exception cause
     */
    public GenbankConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}