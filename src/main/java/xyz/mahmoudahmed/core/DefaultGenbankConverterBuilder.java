package xyz.mahmoudahmed.core;


import xyz.mahmoudahmed.api.*;
import xyz.mahmoudahmed.model.GenbankOptions;

/**
 * Default implementation of GenbankConverterBuilder.
 */
public class DefaultGenbankConverterBuilder implements GenbankConverterBuilder {
    SequenceParser sequenceParser;
    AnnotationParser annotationParser;
    GenbankValidator validator;
    GenbankFormatter formatter;
    GenbankOptions options;

    @Override
    public GenbankConverterBuilder withSequenceParser(SequenceParser parser) {
        this.sequenceParser = parser;
        return this;
    }

    @Override
    public GenbankConverterBuilder withAnnotationParser(AnnotationParser parser) {
        this.annotationParser = parser;
        return this;
    }

    @Override
    public GenbankConverterBuilder withValidator(GenbankValidator validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public GenbankConverterBuilder withFormatter(GenbankFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public GenbankConverterBuilder withOptions(GenbankOptions options) {
        this.options = options;
        return this;
    }

    @Override
    public GenbankConverter build() {
        // Create a DefaultGenbankConverter.Builder and delegate to it
        DefaultGenbankConverter.Builder builder = new DefaultGenbankConverter.Builder();

        if (sequenceParser != null) {
            builder.withSequenceParser(sequenceParser);
        }

        if (annotationParser != null) {
            builder.withAnnotationParser(annotationParser);
        }

        if (validator != null) {
            builder.withValidator(validator);
        }

        if (formatter != null) {
            builder.withFormatter(formatter);
        }

        if (options != null) {
            builder.withOptions(options);
        }

        return builder.build();
    }
}