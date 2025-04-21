package xyz.mahmoudahmed.model;

import org.junit.jupiter.api.Test;
import xyz.mahmoudahmed.translator.GeneticCodeTable;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationOptionsTest {

    @Test
    public void testDefaultBuilder() {
        TranslationOptions options = TranslationOptions.builder().build();

        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options.getGeneticCodeTable());
        assertNull(options.getTranslTableNumber()); // Now this returns null for the default
        assertNull(options.getGeneticCode()); // This should also be null for the default
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
        assertNull(options.getTranslTableNumber()); // This should be null, not derived from the table
        assertNull(options.getGeneticCode()); // This should also be null
        assertTrue(options.isIncludeStopCodon());
        assertFalse(options.isTranslateCDS());
    }

    @Test
    public void testTableNumberBuilder() {
        TranslationOptions options = TranslationOptions.builder()
                .translTableNumber(2)
                .build();

        assertEquals(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL, options.getGeneticCodeTable());
        assertEquals(Integer.valueOf(2), options.getTranslTableNumber()); // Should return the Integer object
    }

    @Test
    public void testGeneticCodeStringBuilder() {
        // Test with enum name
        TranslationOptions options1 = TranslationOptions.builder()
                .geneticCode("STANDARD")
                .build();
        assertEquals(GeneticCodeTable.STANDARD, options1.getGeneticCodeTable());
        assertEquals("STANDARD", options1.getGeneticCode());

        // Test with description
        TranslationOptions options2 = TranslationOptions.builder()
                .geneticCode("Vertebrate Mitochondrial Code")
                .build();
        assertEquals(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL, options2.getGeneticCodeTable());
        assertEquals("Vertebrate Mitochondrial Code", options2.getGeneticCode());

        // Test with table number as string
        TranslationOptions options3 = TranslationOptions.builder()
                .geneticCode("5")
                .build();
        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options3.getGeneticCodeTable());
        assertEquals("5", options3.getGeneticCode());

        // Test with invalid string (should default to invertebrate mitochondrial)
        TranslationOptions options4 = TranslationOptions.builder()
                .geneticCode("invalid")
                .build();
        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options4.getGeneticCodeTable());
        assertEquals("invalid", options4.getGeneticCode());
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
        assertNull(options.getTranslTableNumber());
        assertNull(options.getGeneticCode());
    }

    @Test
    public void testTranslTableNumberPrecedence() {
        // Test that translTableNumber takes precedence over geneticCode
        TranslationOptions options = TranslationOptions.builder()
                .translTableNumber(1) // Standard (1)
                .geneticCode("5") // Invertebrate Mitochondrial (5)
                .build();

        // Should use table 1 (Standard), not table 5
        assertEquals(GeneticCodeTable.STANDARD, options.getGeneticCodeTable());
        assertEquals(Integer.valueOf(1), options.getTranslTableNumber());
        assertEquals("5", options.getGeneticCode());
    }

    @Test
    public void testGeneticCodePrecedence() {
        // Test that geneticCode takes precedence over geneticCodeTable
        TranslationOptions options = TranslationOptions.builder()
                .geneticCode("2") // Vertebrate Mitochondrial (2)
                .geneticCodeTable(GeneticCodeTable.STANDARD) // Standard (1)
                .build();

        // Should use table 2 (Vertebrate), not table 1
        assertEquals(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL, options.getGeneticCodeTable());
        assertNull(options.getTranslTableNumber());
        assertEquals("2", options.getGeneticCode());
    }

    @Test
    public void testFullPrecedenceOrder() {
        // Test all three methods together
        TranslationOptions options = TranslationOptions.builder()
                .translTableNumber(1) // Highest precedence - Standard (1)
                .geneticCode("2") // Middle precedence - Vertebrate (2)
                .geneticCodeTable(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL) // Lowest precedence - Invertebrate (5)
                .build();

        // Should use table 1 (Standard), not table 2 or 5
        assertEquals(GeneticCodeTable.STANDARD, options.getGeneticCodeTable());
        assertEquals(Integer.valueOf(1), options.getTranslTableNumber());
        assertEquals("2", options.getGeneticCode());
    }

    @Test
    public void testInternalStopCodons() {
        TranslationOptions options = TranslationOptions.builder()
                .allowInternalStopCodons(true)
                .build();

        assertTrue(options.isAllowInternalStopCodons());
    }
}