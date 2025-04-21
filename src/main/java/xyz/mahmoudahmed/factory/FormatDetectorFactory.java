package xyz.mahmoudahmed.factory;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.format.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating format detectors.
 */
public class FormatDetectorFactory {
    private final FormatConfiguration config;

    /**
     * Constructor.
     *
     * @param config The format configuration
     */
    public FormatDetectorFactory(FormatConfiguration config) {
        this.config = config;
    }

    /**
     * Creates a list of all available format detectors.
     *
     * @return The list of format detectors
     */
    public List<FormatDetector> createDetectors() {
        List<FormatDetector> detectors = new ArrayList<>();

        // Add all available format detectors
        // IMPORTANT: Order matters for similar formats! More specific formats should come first
        detectors.add(new FastaFormatDetector(config));
        detectors.add(new VcfFormatDetector(config));
        detectors.add(new GtfFormatDetector(config)); // GTF must come before GFF (more specific)
        detectors.add(new GffFormatDetector(config));
        detectors.add(new BedFormatDetector(config));
        detectors.add(new GenbankFormatDetector(config));

        return detectors;
    }
}
