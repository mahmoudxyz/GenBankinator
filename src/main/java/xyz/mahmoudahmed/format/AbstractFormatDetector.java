package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Abstract base class for format detectors.
 */
public abstract class AbstractFormatDetector implements FormatDetector {
    protected final FormatConfiguration config;
    protected final Set<String> supportedExtensions;

    /**
     * Constructor.
     *
     * @param config The format configuration
     * @param supportedExtensions The file extensions supported by this detector
     */
    protected AbstractFormatDetector(FormatConfiguration config, Set<String> supportedExtensions) {
        this.config = config;
        this.supportedExtensions = supportedExtensions;
    }

    @Override
    public boolean canDetect(File file) {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            return false;
        }

        String extension = getFileExtension(file.getName()).toLowerCase();
        return supportedExtensions.contains(extension);
    }

    /**
     * Gets the extension of a file.
     *
     * @param fileName The file name
     * @return The file extension
     */
    protected String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Reads the first N lines of a file.
     *
     * @param file The file to read
     * @param maxLines The maximum number of lines to read
     * @return The lines read
     * @throws FileProcessingException If an error occurs reading the file
     */
    protected String[] readFileLines(File file, int maxLines) throws FileProcessingException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String[] lines = new String[maxLines];
            int lineCount = 0;
            String line;

            while ((line = reader.readLine()) != null && lineCount < maxLines) {
                lines[lineCount++] = line.trim();
            }

            // If we read fewer lines than maxLines, create a new array with the actual size
            if (lineCount < maxLines) {
                String[] actualLines = new String[lineCount];
                System.arraycopy(lines, 0, actualLines, 0, lineCount);
                return actualLines;
            }

            return lines;
        } catch (IOException e) {
            throw new FileProcessingException("Error reading file: " + file.getName(), e);
        }
    }
}