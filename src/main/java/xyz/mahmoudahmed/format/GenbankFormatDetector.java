package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Detector for GENBANK format files.
 */
public class GenbankFormatDetector extends AbstractFormatDetector {
    private static final String FORMAT_NAME = "GENBANK";
    private static final Pattern GENBANK_LOCUS_PATTERN = Pattern.compile("^LOCUS\\s+.*");

    /**
     * Constructor.
     *
     * @param config The format configuration
     */
    public GenbankFormatDetector(FormatConfiguration config) {
        super(config, Set.of("gb", "gbk", "genbank"));
    }

    @Override
    public String detectFormat(File file) throws FileProcessingException {
        String[] lines = readFileLines(file, 20);
        boolean hasLocusLine = false;
        boolean hasFeaturesLine = false;

        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            if (GENBANK_LOCUS_PATTERN.matcher(line).matches()) {
                hasLocusLine = true;
            }

            if (line.contains("FEATURES") && line.contains("Location/Qualifiers")) {
                hasFeaturesLine = true;
            }

            // If we find both a LOCUS line and a FEATURES line, it's definitely GENBANK
            if (hasLocusLine && hasFeaturesLine) {
                return FORMAT_NAME;
            }
        }

        // If we only find a LOCUS line, it's probably GENBANK but we're less confident
        return hasLocusLine ? FORMAT_NAME : "UNKNOWN";
    }
}