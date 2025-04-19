package xyz.mahmoudahmed.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for format detection.
 */
public interface FormatDetector {
    /**
     * Detect the format of a file.
     *
     * @param file The file to check
     * @return The detected format
     * @throws IOException If an I/O error occurs
     */
    String detectFormat(File file) throws IOException;

    /**
     * Detect the format of data from an input stream.
     *
     * @param inputStream The input stream to check
     * @return The detected format
     * @throws IOException If an I/O error occurs
     */
    String detectFormat(InputStream inputStream) throws IOException;
}