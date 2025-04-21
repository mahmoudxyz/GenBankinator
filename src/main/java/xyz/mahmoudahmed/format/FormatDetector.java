package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.File;

/**
 * Interface for file format detectors.
 */
public interface FormatDetector {
    /**
     * Checks if this detector can handle the given file.
     *
     * @param file The file to check
     * @return true if this detector can handle the file
     */
    boolean canDetect(File file);

    /**
     * Detects the format of the given file.
     *
     * @param file The file to detect the format of
     * @return The detected format
     * @throws FileProcessingException If an error occurs during detection
     */
    String detectFormat(File file) throws FileProcessingException;
}