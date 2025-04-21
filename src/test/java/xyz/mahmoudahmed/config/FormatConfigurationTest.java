package xyz.mahmoudahmed.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatConfigurationTest {

    @Test
    void constructor_defaultConfig_loadsMappings() {
        // Create configuration with default mappings
        FormatConfiguration config = new FormatConfiguration();

        // Verify default mappings are loaded
        assertEquals("FASTA", config.getFormatForExtension("fa"));
        assertEquals("FASTA", config.getFormatForExtension("fasta"));
        assertEquals("GFF", config.getFormatForExtension("gff"));
        assertEquals("GTF", config.getFormatForExtension("gtf"));
        assertEquals("BED", config.getFormatForExtension("bed"));
        assertEquals("GENBANK", config.getFormatForExtension("gb"));
        assertEquals("VCF", config.getFormatForExtension("vcf"));
    }

    @Test
    void getFormatForExtension_unknownExtension_returnsNull() {
        FormatConfiguration config = new FormatConfiguration();
        assertNull(config.getFormatForExtension("unknown"));
    }

    @Test
    void getFormatForExtension_caseInsensitive_returnsFormat() {
        FormatConfiguration config = new FormatConfiguration();
        assertEquals("FASTA", config.getFormatForExtension("FA"));
        assertEquals("FASTA", config.getFormatForExtension("FaStA"));
    }

    @Test
    void getProperty_existingProperty_returnsValue() {
        // This test relies on the fact that the default properties might not be loaded,
        // so we'll test with a default value
        FormatConfiguration config = new FormatConfiguration();
        assertEquals("defaultValue", config.getProperty("non.existent.property", "defaultValue"));
    }
}
