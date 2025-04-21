package xyz.mahmoudahmed.parsers;

import xyz.mahmoudahmed.model.SequenceData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for parsing sequence files.
 */
public interface SequenceParser {
    /**
     * Check if this parser supports the given format.
     *
     * @param format The format to check
     * @return true if supported, false otherwise
     */
    boolean supportsFormat(String format);

    /**
     * Parse a sequence file into sequence data.
     *
     * @param file The file to parse
     * @return The parsed sequence data
     * @throws IOException If an I/O error occurs
     */
    SequenceData parse(File file) throws IOException;

    /**
     * Parse a sequence stream into sequence data.
     *
     * @param inputStream The input stream to parse
     * @return The parsed sequence data
     * @throws IOException If an I/O error occurs
     */
    SequenceData parse(InputStream inputStream) throws IOException;

    /**
     * Parse only metadata from a sequence file.
     * Useful for large files where loading the full sequence data would be memory-intensive.
     *
     * @param file The file to parse
     * @return The parsed sequence metadata
     * @throws IOException If an I/O error occurs
     */
    SequenceData parseMetadataOnly(File file) throws IOException;
}
