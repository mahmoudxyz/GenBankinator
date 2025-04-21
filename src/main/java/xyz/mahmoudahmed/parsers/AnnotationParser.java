package xyz.mahmoudahmed.parsers;


import xyz.mahmoudahmed.model.AnnotationData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for parsing annotation files.
 */
public interface AnnotationParser {
    /**
     * Check if this parser supports the given format.
     *
     * @param format The format to check
     * @return true if supported, false otherwise
     */
    boolean supportsFormat(String format);

    /**
     * Parse an annotation file into annotation data.
     *
     * @param file The file to parse
     * @return The parsed annotation data
     * @throws IOException If an I/O error occurs
     */
    AnnotationData parse(File file) throws IOException;

    /**
     * Parse an annotation stream into annotation data.
     *
     * @param inputStream The input stream to parse
     * @return The parsed annotation data
     * @throws IOException If an I/O error occurs
     */
    AnnotationData parse(InputStream inputStream) throws IOException;
}