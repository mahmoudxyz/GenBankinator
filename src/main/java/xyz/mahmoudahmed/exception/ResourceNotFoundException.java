package xyz.mahmoudahmed.exception;

/**
 * Exception for resource not found errors.
 */
public class ResourceNotFoundException extends GenbankConverterException {
    /**
     * Create a new exception with a message.
     *
     * @param message The exception message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and cause.
     *
     * @param message The exception message
     * @param cause The exception cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}