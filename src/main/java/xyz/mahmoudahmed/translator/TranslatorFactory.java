package xyz.mahmoudahmed.translator;

/**
 * Factory for creating translators (Factory Pattern)
 */
public class TranslatorFactory {
    public static Translator createInvertebrateMitochondrialTranslator() {
        return new StandardTranslator(
                new DefaultSequenceHandler(),
                new InvertebrateMitochondrialCode()
        );
    }
}