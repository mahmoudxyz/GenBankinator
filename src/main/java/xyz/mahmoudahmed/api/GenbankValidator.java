package xyz.mahmoudahmed.api;


import xyz.mahmoudahmed.model.ValidationResult;

import java.io.File;
import java.io.IOException;

/**
 * Interface for GenBank validation.
 */
public interface GenbankValidator {
    /**
     * Validate a sequence file.
     *
     * @param file The file to validate
     * @return The validation result
     * @throws IOException If an I/O error occurs
     */
    ValidationResult validateSequence(File file) throws IOException;

    /**
     * Validate an annotation file.
     *
     * @param file The file to validate
     * @param format The expected format
     * @return The validation result
     * @throws IOException If an I/O error occurs
     */
    ValidationResult validateAnnotation(File file, String format) throws IOException;

    /**
     * Validate compatibility between sequence and annotation files.
     *
     * @param sequenceFile The sequence file
     * @param annotationFile The annotation file
     * @return The validation result
     * @throws IOException If an I/O error occurs
     */
    ValidationResult validateCompatibility(File sequenceFile, File annotationFile) throws IOException;
}
