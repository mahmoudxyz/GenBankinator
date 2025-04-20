package xyz.mahmoudahmed.model;

import org.junit.jupiter.api.Test;
import xyz.mahmoudahmed.translator.GeneticCodeTable;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationOptionsTest {

    @Test
    public void testDefaultBuilder() {
        TranslationOptions options = TranslationOptions.builder().build();

        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options.getGeneticCodeTable());
        assertEquals(5, options.getTranslTableNumber());
        assertFalse(options.isIncludeStopCodon());
        assertTrue(options.isTranslateCDS());
    }

    @Test
    public void testCustomBuilder() {
        TranslationOptions options = TranslationOptions.builder()
                .geneticCodeTable(GeneticCodeTable.STANDARD)
                .includeStopCodon(true)
                .translateCDS(false)
                .build();

        assertEquals(GeneticCodeTable.STANDARD, options.getGeneticCodeTable());
        assertEquals(1, options.getTranslTableNumber());
        assertTrue(options.isIncludeStopCodon());
        assertFalse(options.isTranslateCDS());
    }

    @Test
    public void testTableNumberBuilder() {
        TranslationOptions options = TranslationOptions.builder()
                .translTableNumber(2)
                .build();

        assertEquals(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL, options.getGeneticCodeTable());
        assertEquals(2, options.getTranslTableNumber());
    }

    @Test
    public void testGeneticCodeStringBuilder() {
        // Test with enum name
        TranslationOptions options1 = TranslationOptions.builder()
                .geneticCode("STANDARD")
                .build();
        assertEquals(GeneticCodeTable.STANDARD, options1.getGeneticCodeTable());

        // Test with description
        TranslationOptions options2 = TranslationOptions.builder()
                .geneticCode("Vertebrate Mitochondrial Code")
                .build();
        assertEquals(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL, options2.getGeneticCodeTable());

        // Test with table number as string
        TranslationOptions options3 = TranslationOptions.builder()
                .geneticCode("5")
                .build();
        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options3.getGeneticCodeTable());

        // Test with invalid string (should default to invertebrate mitochondrial)
        TranslationOptions options4 = TranslationOptions.builder()
                .geneticCode("invalid")
                .build();
        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options4.getGeneticCodeTable());
    }

    @Test
    public void testNullGeneticCodeTable() {
        // Create builder with null value
        TranslationOptions.Builder builder = TranslationOptions.builder();
        builder.geneticCodeTable(null);
        TranslationOptions options = builder.build();

        // Even with null input, should not have null output
        assertNotNull(options.getGeneticCodeTable());
        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options.getGeneticCodeTable());
        assertEquals(5, options.getTranslTableNumber());
    }
}