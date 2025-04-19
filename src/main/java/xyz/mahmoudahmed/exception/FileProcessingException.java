package xyz.mahmoudahmed.exception;

/**
 * Exception for file processing errors.
 */
public class FileProcessingException extends GenbankConverterException {
    /**
     * Create a new exception with a message.
     *
     * @param message The exception message
     */
    public FileProcessingException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and cause.
     *
     * @param message The exception message
     * @param cause The exception cause
     */
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}