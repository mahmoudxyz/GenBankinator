package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Detector for GFF format files.
 */
public class GffFormatDetector extends AbstractFormatDetector {
    private static final String FORMAT_NAME = "GFF";
    private static final Pattern GFF_HEADER_PATTERN = Pattern.compile("^##gff-version");

    // Detection mode flag
    private boolean strictMode = true;

    /**
     * Constructor.
     *
     * @param config The format configuration
     */
    public GffFormatDetector(FormatConfiguration config) {
        super(config, Set.of("gff", "gff3"));
    }

    /**
     * Sets the detection mode.
     *
     * @param strict If true, only files with matching extensions will be detected.
     *              If false, files with any extension but matching content will also be detected.
     */
    public void setStrictMode(boolean strict) {
        this.strictMode = strict;
    }

    /**
     * Checks if this detector can handle the given file.
     * In strict mode, only files with matching extensions will return true.
     * In non-strict mode, any readable file will return true to allow content-based detection.
     *
     * @param file The file to check
     * @return true if this detector can handle the file
     */
    @Override
    public boolean canDetect(File file) {
        // Check by extension first
        boolean extensionMatch = super.canDetect(file);

        // In strict mode, only return true for matching extensions
        if (strictMode) {
            return extensionMatch;
        }

        // In non-strict mode, also allow detection by content regardless of extension
        return extensionMatch || (file != null && file.exists() && file.isFile() && file.canRead());
    }

    @Override
    public String detectFormat(File file) throws FileProcessingException {
        String[] lines = readFileLines(file, 30);

        boolean hasGffHeader = false;
        boolean isGtfFormat = false;

        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            // Check for GTF-specific attribute format (with quotes and semicolons)
            if (line.matches(".*gene_id \"[^\"]+\";.*")) {
                isGtfFormat = true;
            }

            // Check for GFF header
            if (GFF_HEADER_PATTERN.matcher(line).matches() || line.startsWith("##gff")) {
                hasGffHeader = true;
            }
        }

        // If it has a GTF-style attribute format, don't claim it as GFF
        if (isGtfFormat) {
            return "UNKNOWN";
        }

        // If it has a GFF header, it's definitely GFF
        if (hasGffHeader) {
            return FORMAT_NAME;
        }

        // Additional check for GFF-like content without header
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split("\\t");
            if (parts.length >= 8 &&
                    parts[3].matches("\\d+") && // Start position is numeric
                    parts[4].matches("\\d+") && // End position is numeric
                    !line.contains("gene_id \"")) { // Not GTF-style attributes

                return FORMAT_NAME;
            }
        }

        return "UNKNOWN";
    }
}