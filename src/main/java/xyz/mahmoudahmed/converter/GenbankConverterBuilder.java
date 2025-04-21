package xyz.mahmoudahmed.converter;

import xyz.mahmoudahmed.formatters.GenbankFormatter;
import xyz.mahmoudahmed.parsers.SequenceParser;
import xyz.mahmoudahmed.model.GenbankOptions;
import xyz.mahmoudahmed.parsers.AnnotationParser;
import xyz.mahmoudahmed.validators.GenbankValidator;

/**
 * Builder for creating GenbankConverter instances.
 */
public interface GenbankConverterBuilder {
    /**
     * Set the sequence parser.
     *
     * @param parser The sequence parser to use
     * @return This builder
     */
    GenbankConverterBuilder withSequenceParser(SequenceParser parser);

    /**
     * Set the annotation parser.
     *
     * @param parser The annotation parser to use
     * @return This builder
     */
    GenbankConverterBuilder withAnnotationParser(AnnotationParser parser);

    /**
     * Set the validator.
     *
     * @param validator The validator to use
     * @return This builder
     */
    GenbankConverterBuilder withValidator(GenbankValidator validator);

    /**
     * Set the formatter.
     *
     * @param formatter The formatter to use
     * @return This builder
     */
    GenbankConverterBuilder withFormatter(GenbankFormatter formatter);

    /**
     * Set the options.
     *
     * @param options The options to use
     * @return This builder
     */
    GenbankConverterBuilder withOptions(GenbankOptions options);

    /**
     * Build a new GenbankConverter.
     *
     * @return A new GenbankConverter instance
     */
    GenbankConverter build();
}
