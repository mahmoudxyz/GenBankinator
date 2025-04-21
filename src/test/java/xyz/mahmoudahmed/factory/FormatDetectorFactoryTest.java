package xyz.mahmoudahmed.factory;

import org.junit.jupiter.api.Test;
import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.format.FormatDetector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FormatDetectorFactoryTest {

    @Test
    void createDetectors_returnsAllDetectors() {
        // Prepare
        FormatConfiguration mockConfig = mock(FormatConfiguration.class);
        FormatDetectorFactory factory = new FormatDetectorFactory(mockConfig);

        // Test
        List<FormatDetector> detectors = factory.createDetectors();

        // Verify
        assertNotNull(detectors);
        assertFalse(detectors.isEmpty());
        // We expect 6 detectors (FASTA, GFF, GTF, BED, GENBANK, VCF)
        assertEquals(6, detectors.size());
    }
}
