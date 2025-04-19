package xyz.mahmoudahmed.api;

import xyz.mahmoudahmed.core.DefaultGenbankConverterBuilder;
import xyz.mahmoudahmed.model.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Main interface for GenBank conversion operations.
 */
public interface GenbankConverter {
    /**
     * Convert sequence and annotation files to GenBank format.
     *
     * @param sequenceFile File containing the sequence data
     * @param annotationFile File containing the annotation data
     * @param options Conversion options
     * @return The conversion result
     * @throws IOException If an I/O error occurs
     */
    GenbankResult convert(File sequenceFile, File annotationFile, ConversionOptions options) throws IOException;

    /**
     * Convert sequence and annotation data to GenBank format.
     *
     * @param sequenceData The sequence data
     * @param annotationData The annotation data
     * @param options Conversion options
     * @return The conversion result
     */
    GenbankResult convert(SequenceData sequenceData, AnnotationData annotationData, ConversionOptions options);

    /**
     * Write GenBank data directly to an output stream.
     *
     * @param sequenceData The sequence data
     * @param annotationData The annotation data
     * @param outputStream The output stream to write to
     * @param options Conversion options
     * @throws IOException If an I/O error occurs
     */
    void convertToStream(SequenceData sequenceData, AnnotationData annotationData,
                         OutputStream outputStream, ConversionOptions options) throws IOException;

    /**
     * Validate a sequence file.
     *
     * @param sequenceFile File containing the sequence data
     * @return Validation result
     * @throws IOException If an I/O error occurs
     */
    ValidationResult validateSequence(File sequenceFile) throws IOException;

    /**
     * Validate an annotation file.
     *
     * @param annotationFile File containing the annotation data
     * @param format The expected format of the annotation file
     * @return Validation result
     * @throws IOException If an I/O error occurs
     */
    ValidationResult validateAnnotation(File annotationFile, String format) throws IOException;

    /**
     * Validate compatibility between sequence and annotation files.
     *
     * @param sequenceFile File containing the sequence data
     * @param annotationFile File containing the annotation data
     * @return Validation result
     * @throws IOException If an I/O error occurs
     */
    ValidationResult validate(File sequenceFile, File annotationFile) throws IOException;

    /**
     * Create a new GenbankConverter builder.
     *
     * @return A new builder instance
     */
    static GenbankConverterBuilder builder() {
        return new DefaultGenbankConverterBuilder();
    }

    /**
     * Create a standard GenbankConverter with default settings.
     *
     * @return A new GenbankConverter instance
     */
    static GenbankConverter standard() {
        return builder().build();
    }

    /**
     * Create a memory-efficient GenbankConverter for large files.
     *
     * @return A new GenbankConverter optimized for memory efficiency
     */
    static GenbankConverter memoryEfficient() {
        return builder()
                .withOptions(GenbankOptions.builder()
                        .memoryEfficient(true)
                        .build())
                .build();
    }
}