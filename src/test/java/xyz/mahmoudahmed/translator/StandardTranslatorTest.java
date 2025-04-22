package xyz.mahmoudahmed.translator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import xyz.mahmoudahmed.model.TranslationOptions;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

public class StandardTranslatorTest {

    private SequenceHandler mockSequenceHandler;
    private GeneticCode mockGeneticCode;
    private AbstractGeneticCode mockAbstractGeneticCode;
    private StandardTranslator translator;
    private StandardTranslator translatorWithInternalStops;

    @BeforeEach
    public void setUp() {
        mockSequenceHandler = mock(SequenceHandler.class);
        mockGeneticCode = mock(GeneticCode.class);

        // Standard mock translator (stops at first stop codon)
        translator = new StandardTranslator(mockSequenceHandler, mockGeneticCode);

        // For tests requiring AbstractGeneticCode with table checking
        mockAbstractGeneticCode = mock(AbstractGeneticCode.class);
        when(mockAbstractGeneticCode.getTable()).thenReturn(GeneticCodeTable.STANDARD);

        // Create translator with options allowing internal stop codons
        TranslationOptions options = TranslationOptions.builder()
                .allowInternalStopCodons(true)
                .build();

        translatorWithInternalStops = new StandardTranslator(
                mockSequenceHandler, mockAbstractGeneticCode, options
        );
    }

    @Test
    public void testGetGeneticCode() {
        assertEquals(mockGeneticCode, translator.getGeneticCode());
    }

    @Test
    public void testTranslate_StopAtFirstStopCodon() {
        // Setup mocks
        String inputSequence = "ATGCGATAATAG";
        String validatedSequence = "ATGCGATAATAG";
        String rnaSequence = "AUGCGAUAAUAG";

        when(mockSequenceHandler.validateSequence(inputSequence)).thenReturn(validatedSequence);
        when(mockSequenceHandler.toRNA(validatedSequence)).thenReturn(rnaSequence);
        when(mockSequenceHandler.splitIntoCodons(rnaSequence)).thenReturn(List.of("AUG", "CGA", "UAA", "UAG"));

        // Setup codon translations
        when(mockGeneticCode.translateStartCodon("AUG")).thenReturn('M');
        when(mockGeneticCode.translate("CGA")).thenReturn('R');
        when(mockGeneticCode.translate("UAA")).thenReturn('*');
        when(mockGeneticCode.translate("UAG")).thenReturn('*');

        // Test DNA translation
        String result = translator.translate(inputSequence, false);
        assertEquals("MR", result);

        // Verify methods were called correctly
        verify(mockSequenceHandler).validateSequence(inputSequence);
        verify(mockSequenceHandler).toRNA(validatedSequence);
        verify(mockSequenceHandler).splitIntoCodons(rnaSequence);
        verify(mockGeneticCode).translateStartCodon("AUG");
        verify(mockGeneticCode).translate("CGA");
        verify(mockGeneticCode, atLeastOnce()).translate("UAA"); // Called for UAA
    }

    @Test
    public void testTranslateRNA_StopAtFirstStopCodon() {
        // Setup mocks
        String inputSequence = "AUGCGAUAAUAG";

        when(mockSequenceHandler.validateSequence(inputSequence)).thenReturn(inputSequence);
        when(mockSequenceHandler.splitIntoCodons(inputSequence)).thenReturn(List.of("AUG", "CGA", "UAA", "UAG"));

        // Setup codon translations
        when(mockGeneticCode.translateStartCodon("AUG")).thenReturn('M');
        when(mockGeneticCode.translate("CGA")).thenReturn('R');
        when(mockGeneticCode.translate("UAA")).thenReturn('*');
        when(mockGeneticCode.translate("UAG")).thenReturn('*');

        // Test RNA translation
        String result = translator.translate(inputSequence, true);
        assertEquals("MR", result);

        // Verify methods were called correctly
        verify(mockSequenceHandler).validateSequence(inputSequence);
        verify(mockSequenceHandler, never()).toRNA(anyString());
        verify(mockSequenceHandler).splitIntoCodons(inputSequence);
        verify(mockGeneticCode).translateStartCodon("AUG");
        verify(mockGeneticCode).translate("CGA");
        verify(mockGeneticCode, atLeastOnce()).translate("UAA");
    }

    @Test
    public void testTranslateWithInternalStopCodon_WhenAllowed() {
        // Setup mocks
        String inputSequence = "ATGCGATAAGTC";
        String validatedSequence = "ATGCGATAAGTC";
        String rnaSequence = "AUGCGAUAAGUC";

        when(mockSequenceHandler.validateSequence(inputSequence)).thenReturn(validatedSequence);
        when(mockSequenceHandler.toRNA(validatedSequence)).thenReturn(rnaSequence);
        when(mockSequenceHandler.splitIntoCodons(rnaSequence)).thenReturn(List.of("AUG", "CGA", "UAA", "GUC"));

        // Setup codon translations
        when(mockAbstractGeneticCode.translateStartCodon("AUG")).thenReturn('M');
        when(mockAbstractGeneticCode.translate("CGA")).thenReturn('R');
        when(mockAbstractGeneticCode.translate("UAA")).thenReturn('*');
        when(mockAbstractGeneticCode.translate("GUC")).thenReturn('V');

        // Test translation with internal stop codon when allowed
        String result = translatorWithInternalStops.translate(inputSequence, false);
        assertEquals("MR-V", result);
    }

    @Test
    public void testTranslateWithInternalStopCodon_WhenNotAllowed() {
        // Setup mocks
        String inputSequence = "ATGCGATAAGTC";
        String validatedSequence = "ATGCGATAAGTC";
        String rnaSequence = "AUGCGAUAAGUC";

        when(mockSequenceHandler.validateSequence(inputSequence)).thenReturn(validatedSequence);
        when(mockSequenceHandler.toRNA(validatedSequence)).thenReturn(rnaSequence);
        when(mockSequenceHandler.splitIntoCodons(rnaSequence)).thenReturn(List.of("AUG", "CGA", "UAA", "GUC"));

        // Setup codon translations
        when(mockGeneticCode.translateStartCodon("AUG")).thenReturn('M');
        when(mockGeneticCode.translate("CGA")).thenReturn('R');
        when(mockGeneticCode.translate("UAA")).thenReturn('*');
        when(mockGeneticCode.translate("GUC")).thenReturn('V');

        // Test translation with internal stop codon when not allowed
        String result = translator.translate(inputSequence, false);
        assertEquals("MR", result);
    }

    @Test
    public void testTranslateInvertebrateMitochondrial() {
        // Setup a real translator with invertebrate mitochondrial code
        Translator invertTranslator = TranslatorFactory.createTranslator(
                GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL
        );

        // Test DNA with internal stop codon
        String dnaWithInternalStop = "ATGCGATAAGTC"; // ATG CGA TAA GTC
        String protein = invertTranslator.translate(dnaWithInternalStop, false);

        // Internal stop codons in invertebrate mitochondrial should be replaced with dashes
        assertEquals("MR-V", protein);
    }

    @Test
    public void testFindOpenReadingFrames() {
        // Setup mocks
        String inputSequence = "ATGCGATAACCCATGAAATAA";
        String validatedSequence = "ATGCGATAACCCATGAAATAA";
        String rnaSequence = "AUGCGAUAACCCAUGAAAUAA";

        when(mockSequenceHandler.validateSequence(inputSequence)).thenReturn(validatedSequence);
        when(mockSequenceHandler.toRNA(validatedSequence)).thenReturn(rnaSequence);

        // Setup start and stop codon detection
        when(mockGeneticCode.isStartCodon("AUG")).thenReturn(true);
        when(mockGeneticCode.isStartCodon(not(eq("AUG")))).thenReturn(false);
        when(mockGeneticCode.isStopCodon("UAA")).thenReturn(true);
        when(mockGeneticCode.isStopCodon(not(eq("UAA")))).thenReturn(false);

        // Setup translations for various codons
        when(mockGeneticCode.translateStartCodon("AUG")).thenReturn('M');
        when(mockGeneticCode.translate("CGA")).thenReturn('R');
        when(mockGeneticCode.translate("UAA")).thenReturn('*');
        when(mockGeneticCode.translate("CCC")).thenReturn('P');
        when(mockGeneticCode.translate("AUG")).thenReturn('M');
        when(mockGeneticCode.translate("AAA")).thenReturn('K');

        // Test finding ORFs
        List<String> orfs = translator.findOpenReadingFrames(inputSequence, false);

        // Should find at least the two ORFs starting with AUG
        assertTrue(orfs.size() >= 2);

        // Verify methods were called
        verify(mockSequenceHandler).validateSequence(inputSequence);
        verify(mockSequenceHandler).toRNA(validatedSequence);
        verify(mockGeneticCode, atLeastOnce()).isStartCodon(anyString());
        verify(mockGeneticCode, atLeastOnce()).isStopCodon(anyString());
        verify(mockGeneticCode, atLeastOnce()).translateStartCodon(anyString());
        verify(mockGeneticCode, atLeastOnce()).translate(anyString());
    }

    @ParameterizedTest
    @MethodSource("standardTranslationExampleProvider")
    public void testStandardTranslation(String dnaSequence, String expectedProtein) {
        // Create a real translator with the Standard code
        Translator standardTranslator = TranslatorFactory.createTranslator(GeneticCodeTable.STANDARD);

        // Test translation
        String actualProtein = standardTranslator.translate(dnaSequence, false);
        assertEquals(expectedProtein, actualProtein);
    }

    private static Stream<Arguments> standardTranslationExampleProvider() {
        return Stream.of(
                // Standard code translation - should stop at first stop codon
                Arguments.of("ATGCGATACTAGCTAAGGCTA", "MRY"), // ATG (start) + CGA (R) + TAC (Y) + TAG (stop) + CTA (L) ...
                Arguments.of("ATGCGATAAGGATAA", "MR"),        // ATG (start) + CGA (R) + TAA (stop) + GGA (G) + TAA (stop)
                Arguments.of("ATGAGATAA", "MR")               // ATG (start) + AGA (R) + TAA (stop)
        );
    }

    @ParameterizedTest
    @MethodSource("invertebrateMitoTranslationExampleProvider")
    public void testInvertebrateMitoTranslation(String dnaSequence, String expectedProtein) {
        // Create a real translator with Invertebrate Mitochondrial code
        Translator mitoTranslator = TranslatorFactory.createTranslator(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL);

        // Test translation
        String actualProtein = mitoTranslator.translate(dnaSequence, false);
        assertEquals(expectedProtein, actualProtein);
    }

    private static Stream<Arguments> invertebrateMitoTranslationExampleProvider() {
        return Stream.of(
                // Invertebrate mitochondrial code
                Arguments.of("ATAAGACTAGGATGA", "MSLGW"),  // No stop codons in this example (ATA=M, AGA=S, CTA=L, GGA=G, TGA=W)

                // With internal TGA (stop in standard, W in mito)
                Arguments.of("ATGTGAAGATAA", "MWS"),       // ATG (start) + TGA (W in mito) + AGA (S in mito) + TAA (stop)

                // With internal stop
                Arguments.of("ATGCGATAAAGA", "MR-S")       // ATG (start) + CGA (R) + TAA (stop) + AGA (S)
        );
    }

    @ParameterizedTest
    @MethodSource("vertebrateMitoTranslationExampleProvider")
    public void testVertebrateMitoTranslation(String dnaSequence, String expectedProtein) {
        // Create a real translator with Vertebrate Mitochondrial code
        Translator vertMitoTranslator = TranslatorFactory.createTranslator(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL);

        // Test translation
        String actualProtein = vertMitoTranslator.translate(dnaSequence, false);
        assertEquals(expectedProtein, actualProtein);
    }

    private static Stream<Arguments> vertebrateMitoTranslationExampleProvider() {
        return Stream.of(
                // Vertebrate mitochondrial code
                // ATA=M in mito (I in standard)
                // AGA=stop in vert mito (R in standard, S in invert mito)
                Arguments.of("ATAAGACTAGGATGA", "M"),      // ATA (M) + AGA (stop in vert mito)
                Arguments.of("ATGTGATAA", "MW"),           // ATG (start) + TGA (W in mito) + TAA (stop)
                Arguments.of("ATGCGAAGATAA", "MR")         // ATG (start) + CGA (R) + AGA (stop in vert mito) + TAA (stop)
        );
    }

    @Test
    public void testTranslateWithCustomOptions() {
        // Create a translator with custom options allowing internal stop codons
        TranslationOptions options = TranslationOptions.builder()
                .geneticCodeTable(GeneticCodeTable.STANDARD)
                .allowInternalStopCodons(true)
                .build();

        Translator customTranslator = TranslatorFactory.createTranslator(
                GeneticCodeTable.STANDARD, options
        );

        // Test with Standard code but allowing internal stops
        String dnaWithInternalStop = "ATGCGATAGCGATAA"; // ATG CGA TAG CGA TAA
        String protein = customTranslator.translate(dnaWithInternalStop, false);

        // Should show internal stop as dash
        assertEquals("MR-R", protein);
    }
}