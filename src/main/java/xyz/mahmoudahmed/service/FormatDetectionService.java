package xyz.mahmoudahmed.service;

import xyz.mahmoudahmed.exception.FileProcessingException;
import xyz.mahmoudahmed.format.FormatDetector;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Service for detecting file formats.
 */
public class FormatDetectionService {
    private final List<FormatDetector> detectors;

    /**
     * Constructor.
     *
     * @param detectors The list of format detectors to use
     */
    public FormatDetectionService(List<FormatDetector> detectors) {
        this.detectors = Objects.requireNonNull(detectors, "Detectors cannot be null");
    }

    /**
     * Detects the format of a file.
     *
     * @param file The file to detect the format of
     * @return The detected format
     * @throws FileProcessingException If an error occurs during detection
     */
    public String detectFormat(File file) throws FileProcessingException {
        if (file == null || !file.exists() || !file.isFile() || !file.canRead()) {
            throw new FileProcessingException("Invalid or unreadable file: " +
                    (file != null ? file.getPath() : "null"));
        }

        // Try each detector in order
        for (FormatDetector detector : detectors) {
            if (detector.canDetect(file)) {
                String format = detector.detectFormat(file);
                if (!"UNKNOWN".equals(format)) {
                    return format;
                }
            }
        }

        // If no detector could identify the format, try content-based detection
        for (FormatDetector detector : detectors) {
            String format = detector.detectFormat(file);
            if (!"UNKNOWN".equals(format)) {
                return format;
            }
        }

        return "UNKNOWN";
    }
}