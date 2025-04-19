package xyz.mahmoudahmed.translator;


/**
 * Exception for translation errors
 */
public class TranslationException extends RuntimeException {
    public TranslationException(String message) {
        super(message);
    }

    public TranslationException(String message, Throwable cause) {
        super(message, cause);
    }
}
