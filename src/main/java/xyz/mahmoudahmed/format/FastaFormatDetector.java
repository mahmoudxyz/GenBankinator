package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.File;
import java.util.Set;

/**
 * Detector for FASTA format files.
 */
public class FastaFormatDetector extends AbstractFormatDetector {
    private static final String FORMAT_NAME = "FASTA";

    /**
     * Constructor.
     *
     * @param config The format configuration
     */
    public FastaFormatDetector(FormatConfiguration config) {
        super(config, Set.of("fa", "fasta", "fna", "faa", "ffn"));
    }

    @Override
    public String detectFormat(File file) throws FileProcessingException {
        String[] lines = readFileLines(file, 50);

        if (lines.length == 0) {
            return "UNKNOWN";
        }

        int fastaHeaderCount = 0;
        boolean hasSequenceData = false;

        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith(">")) {
                fastaHeaderCount++;
            } else if (fastaHeaderCount > 0 && line.matches("^[A-Za-z*-]+$")) {
                // Line contains only valid sequence characters
                hasSequenceData = true;
            }
        }

        return (fastaHeaderCount > 0 && hasSequenceData) ? FORMAT_NAME : "UNKNOWN";
    }

    /**
     * Checks if a FASTA file contains annotation information.
     *
     * @param file The file to check
     * @return true if the file appears to be a FASTA annotation file
     * @throws FileProcessingException If an error occurs checking the file
     */
    public boolean isFastaAnnotationFile(File file) throws FileProcessingException {
        if (!FORMAT_NAME.equals(detectFormat(file))) {
            return false;
        }

        String[] lines = readFileLines(file, 20);
        int checkedHeaders = 0;
        int annotationHeadersFound = 0;

        for (String line : lines) {
            if (line.startsWith(">")) {
                checkedHeaders++;
                // Check for patterns typical in FASTA annotation headers
                if (line.contains(";") &&
                        line.matches(".*[0-9]+-[0-9]+.*") &&
                        (line.contains(" + ") || line.contains(" - ") ||
                                line.contains(";+;") || line.contains(";-;")) &&
                        line.split(";").length >= 3) {
                    annotationHeadersFound++;
                }
            }
        }

        // If at least half of the checked headers look like annotation headers
        return annotationHeadersFound > 0 &&
                (annotationHeadersFound >= checkedHeaders / 2.0);
    }
}
