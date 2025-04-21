package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Detector for BED format files.
 */
public class BedFormatDetector extends AbstractFormatDetector {
    private static final String FORMAT_NAME = "BED";
    private static final Pattern BED_LINE_PATTERN = Pattern.compile("^\\S+\\t\\d+\\t\\d+.*");

    /**
     * Constructor.
     *
     * @param config The format configuration
     */
    public BedFormatDetector(FormatConfiguration config) {
        super(config, Set.of("bed"));
    }

    @Override
    public String detectFormat(File file) throws FileProcessingException {
        String[] lines = readFileLines(file, 20);
        int bedLineCount = 0;

        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            if (BED_LINE_PATTERN.matcher(line).matches() ||
                    line.startsWith("browser") ||
                    line.startsWith("track")) {
                bedLineCount++;

                // If we find at least 3 BED-formatted lines, we're confident it's BED
                if (bedLineCount >= 3) {
                    return FORMAT_NAME;
                }
            }
        }

        return "UNKNOWN";
    }
}