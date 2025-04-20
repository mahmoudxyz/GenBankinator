package xyz.mahmoudahmed.translator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GeneticCodeTest {

    @Test
    public void testStandardGeneticCode() {
        GeneticCode code = new StandardGeneticCode();

        assertEquals("Standard Code", code.getName());

        // Test some standard translations
        assertEquals('M', code.translate("AUG"));
        assertEquals('L', code.translate("UUA"));
        assertEquals('V', code.translate("GUG"));
        assertEquals('*', code.translate("UAA")); // Stop codon

        // Test start codon behavior
        assertTrue(code.isStartCodon("AUG"));
        assertEquals('M', code.translateStartCodon("AUG"));

        // Test stop codon detection
        assertTrue(code.isStopCodon("UAA"));
        assertTrue(code.isStopCodon("UAG"));
        assertTrue(code.isStopCodon("UGA"));

        // Test lowercase handling
        assertEquals('M', code.translate("aug"));

        // Test unknown codon
        assertEquals('?', code.translate("XYZ"));
    }

    @Test
    public void testInvertebrateMitochondrialCode() {
        GeneticCode code = new InvertebrateMitochondrialCode();

        assertEquals("Invertebrate Mitochondrial Code", code.getName());

        // Test invertebrate mitochondrial specific translations
        assertEquals('M', code.translate("AUA")); // In standard code, this is 'I'
        assertEquals('W', code.translate("UGA")); // In standard code, this is a stop codon
        assertEquals('S', code.translate("AGA")); // In standard code, this is 'R'
        assertEquals('S', code.translate("AGG")); // In standard code, this is 'R'

        // Test start codon behavior
        assertTrue(code.isStartCodon("AUG"));
        assertTrue(code.isStartCodon("AUA"));
        assertTrue(code.isStartCodon("AUU"));
        assertEquals('M', code.translateStartCodon("AUA"));

        // Test stop codon detection
        assertTrue(code.isStopCodon("UAA"));
        assertTrue(code.isStopCodon("UAG"));
        assertFalse(code.isStopCodon("UGA")); // Not a stop codon in invertebrate mitochondrial
    }

    @Test
    public void testVertebrateMitochondrialCode() {
        GeneticCode code = new VertebrateMitochondrialCode();

        assertEquals("Vertebrate Mitochondrial Code", code.getName());

        // Test vertebrate mitochondrial specific translations
        assertEquals('M', code.translate("AUA")); // In standard code, this is 'I'
        assertEquals('W', code.translate("UGA")); // In standard code, this is a stop codon
        assertEquals('*', code.translate("AGA")); // In standard code, this is 'R', but stop in vertebrate mito
        assertEquals('*', code.translate("AGG")); // In standard code, this is 'R', but stop in vertebrate mito

        // Test start codon behavior
        assertTrue(code.isStartCodon("AUG"));
        assertTrue(code.isStartCodon("AUA"));
        assertTrue(code.isStartCodon("AUU"));
        assertEquals('M', code.translateStartCodon("AUA"));

        // Test stop codon detection
        assertTrue(code.isStopCodon("UAA"));
        assertTrue(code.isStopCodon("UAG"));
        assertFalse(code.isStopCodon("UGA")); // Not a stop codon in vertebrate mitochondrial
        assertTrue(code.isStopCodon("AGA")); // Stop codon in vertebrate mitochondrial
        assertTrue(code.isStopCodon("AGG")); // Stop codon in vertebrate mitochondrial
    }

    @Test
    public void testAbstractGeneticCodeImplementation() {
        AbstractGeneticCode code = new InvertebrateMitochondrialCode();

        // Test the table reference
        assertEquals(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, code.getTable());
        assertEquals(5, code.getTable().getTableNumber());
    }

    @ParameterizedTest
    @MethodSource("codonTranslationProvider")
    public void testDifferencesBetweenCodes(String codon, char standardResult, char invertResult, char vertebrateResult) {
        GeneticCode standard = new StandardGeneticCode();
        GeneticCode invert = new InvertebrateMitochondrialCode();
        GeneticCode vertebrate = new VertebrateMitochondrialCode();

        assertEquals(standardResult, standard.translate(codon));
        assertEquals(invertResult, invert.translate(codon));
        assertEquals(vertebrateResult, vertebrate.translate(codon));
    }

    private static Stream<Arguments> codonTranslationProvider() {
        return Stream.of(
                // codon, standard, invertebrate, vertebrate
                Arguments.of("AUA", 'I', 'M', 'M'),  // Isoleucine in standard, Methionine in mito codes
                Arguments.of("UGA", '*', 'W', 'W'),  // Stop in standard, Tryptophan in mito codes
                Arguments.of("AGA", 'R', 'S', '*'),  // Arginine in standard, Serine in invert, Stop in vertebrate
                Arguments.of("AGG", 'R', 'S', '*')   // Arginine in standard, Serine in invert, Stop in vertebrate
        );
    }
}