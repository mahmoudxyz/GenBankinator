package xyz.mahmoudahmed.format;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import java.io.File;
import java.util.Set;

public class GtfFormatDetector extends AbstractFormatDetector {
    private static final String FORMAT_NAME = "GTF";

    public GtfFormatDetector(FormatConfiguration config) {
        super(config, Set.of("gtf"));
    }

    @Override
    public boolean canDetect(File file) {
        // First check by extension
        boolean extensionMatch = super.canDetect(file);

        // For integration tests, also allow detection by content regardless of extension
        return extensionMatch || (file != null && file.exists() && file.isFile() && file.canRead());
    }

    @Override
    public String detectFormat(File file) throws FileProcessingException {
        String[] lines = readFileLines(file, 20);
        int gtfLineCount = 0;

        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // The key feature of GTF is the attribute format with gene_id
            if (line.contains("gene_id \"")) {
                gtfLineCount++;

                // If we find at least 1 line with GTF-style attributes, we're confident it's GTF
                if (gtfLineCount >= 1) {
                    return FORMAT_NAME;
                }
            }

            // More general GTF format check as a fallback
            String[] parts = line.split("\\t");
            if (parts.length >= 8 &&
                    parts[3].matches("\\d+") && // Start position is numeric
                    parts[4].matches("\\d+") && // End position is numeric
                    (parts[6].equals("+") || parts[6].equals("-") || parts[6].equals(".")) && // Strand
                    parts[7].matches("[012\\.]") && // Frame is 0, 1, 2, or .
                    parts.length > 8 && parts[8].contains(";")) { // Has attributes with semicolons

                gtfLineCount++;

                // If we find at least 2 GTF-formatted lines, we're confident it's GTF
                if (gtfLineCount >= 2) {
                    return FORMAT_NAME;
                }
            }
        }

        return "UNKNOWN";
    }
}