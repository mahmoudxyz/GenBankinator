package xyz.mahmoudahmed.exception;

/**
 * Exception for validation errors.
 */
public class ValidationException extends GenbankConverterException {
    /**
     * Create a new exception with a message.
     *
     * @param message The exception message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and cause.
     *
     * @param message The exception message
     * @param cause The exception cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}