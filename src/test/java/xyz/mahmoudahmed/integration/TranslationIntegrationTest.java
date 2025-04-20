package xyz.mahmoudahmed.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.api.GenbankConverter;
import xyz.mahmoudahmed.model.*;
import xyz.mahmoudahmed.parsers.FastaAnnotationParser;
import xyz.mahmoudahmed.parsers.NCBICompatibleSequenceParser;
import xyz.mahmoudahmed.translator.GeneticCodeTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests focusing on translation table customization.
 */
public class TranslationIntegrationTest {

    @TempDir
    Path tempDir;

    private File sequenceFile;
    private File annotationFile;
    private GenbankConverter converter;

    @BeforeEach
    public void setUp() throws IOException {
        // Create test sequence file with ATA and AGA codons that differ between tables
        sequenceFile = tempDir.resolve("test_sequence.fasta").toFile();
        try (FileWriter writer = new FileWriter(sequenceFile)) {
            writer.write(">seq1 Test Sequence\n");
            writer.write("ATAAGAGATGA"); // ATA + AGA + GAT + GA
            // ATA: Ile(I) in std, Met(M) in mito
            // AGA: Arg(R) in std, Ser(S) in invert mito, Stop(*) in vert mito
        }

        // Create test annotation file with a CDS feature
        annotationFile = tempDir.resolve("test_annotation.fasta").toFile();
        try (FileWriter writer = new FileWriter(annotationFile)) {
            writer.write(">seq1; 1-11; +; COX1(Test gene)\n");
            writer.write("ATAAGAGATGA\n");
        }

        // Create converter with specific parsers
        converter = GenbankConverter.builder()
                .withSequenceParser(new NCBICompatibleSequenceParser())
                .withAnnotationParser(new FastaAnnotationParser())
                .build();
    }

    @Test
    public void testTranslateWithDifferentTables() throws IOException {
        // Create header info for GenBank
        HeaderInfo headerInfo = createHeaderInfo();

        // Generate GenBank with Standard table (table 1)
        ConversionOptions stdOptions = ConversionOptions.builder()
                .organism("Test Organism")
                .moleculeType("DNA")
                .topology("linear")
                .annotationFormat("FASTA")
                .headerInfo(headerInfo)
                .translationOptions(TranslationOptions.builder()
                        .translTableNumber(1) // Standard Code
                        .build())
                .build();

        GenbankResult stdResult = converter.convert(sequenceFile, annotationFile, stdOptions);
        File stdOutputFile = tempDir.resolve("output_standard.gb").toFile();
        stdResult.writeToFile(stdOutputFile);

        // Generate GenBank with Invertebrate Mitochondrial table (table 5)
        ConversionOptions invertOptions = ConversionOptions.builder()
                .organism("Test Organism")
                .moleculeType("DNA")
                .topology("linear")
                .annotationFormat("FASTA")
                .headerInfo(headerInfo)
                .translationOptions(TranslationOptions.builder()
                        .translTableNumber(5) // Invertebrate Mitochondrial Code
                        .build())
                .build();

        GenbankResult invertResult = converter.convert(sequenceFile, annotationFile, invertOptions);
        File invertOutputFile = tempDir.resolve("output_invertebrate.gb").toFile();
        invertResult.writeToFile(invertOutputFile);

        // Generate GenBank with Vertebrate Mitochondrial table (table 2)
        ConversionOptions vertOptions = ConversionOptions.builder()
                .organism("Test Organism")
                .moleculeType("DNA")
                .topology("linear")
                .annotationFormat("FASTA")
                .headerInfo(headerInfo)
                .translationOptions(TranslationOptions.builder()
                        .translTableNumber(2) // Vertebrate Mitochondrial Code
                        .build())
                .build();

        GenbankResult vertResult = converter.convert(sequenceFile, annotationFile, vertOptions);
        File vertOutputFile = tempDir.resolve("output_vertebrate.gb").toFile();
        vertResult.writeToFile(vertOutputFile);

        // Verify GenBank files were created
        assertTrue(stdOutputFile.exists());
        assertTrue(invertOutputFile.exists());
        assertTrue(vertOutputFile.exists());

        // Read the GenBank file contents
        String stdGenbank = Files.readString(stdOutputFile.toPath());
        String invertGenbank = Files.readString(invertOutputFile.toPath());
        String vertGenbank = Files.readString(vertOutputFile.toPath());

        // Verify translation table qualifier in the files
        assertTrue(stdGenbank.contains("/transl_table=1"));
        assertTrue(invertGenbank.contains("/transl_table=5"));
        assertTrue(vertGenbank.contains("/transl_table=2"));

        // Verify translation results for ATA+AGA codons:
        // - In standard code: Ile(I) + Arg(R) = "IR"
        // - In invertebrate mito: Met(M) + Ser(S) = "MS"
        // - In vertebrate mito: Met(M) + <stop> = "M"

        // 1. Extract translations
        String stdTranslation = extractTranslation(stdGenbank);
        String invertTranslation = extractTranslation(invertGenbank);
        String vertTranslation = extractTranslation(vertGenbank);

        // 2. Verify expected translations
        assertTrue(stdTranslation.startsWith("IR"), "Standard code should translate ATA+AGA as IR");
        assertTrue(invertTranslation.startsWith("MS"), "Invertebrate mito code should translate ATA+AGA as MS");
        assertEquals("M", vertTranslation, "Vertebrate mito code should translate ATA+AGA as M (stop at AGA)");

        // Verify presence of /translation qualifier
        assertTrue(stdGenbank.contains("/translation="));
        assertTrue(invertGenbank.contains("/translation="));
        assertTrue(vertGenbank.contains("/translation="));
    }

    /**
     * Helper method to extract the translation from GenBank format
     */
    private String extractTranslation(String genbankContent) {
        Pattern pattern = Pattern.compile("/translation=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(genbankContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * Create a sample HeaderInfo for GenBank output
     */
    private HeaderInfo createHeaderInfo() {
        // Create reference information
        List<ReferenceInfo> references = Arrays.asList(
                ReferenceInfo.builder()
                        .number(1)
                        .authors(Arrays.asList("Test Author"))
                        .title("Test Reference")
                        .journal("Test Journal")
                        .pubStatus("Published")
                        .build()
        );

        // Create HeaderInfo
        return HeaderInfo.builder()
                .definition("Test sequence for translation table testing")
                .accessionNumber("TEST01")
                .version("TEST01.1")
                .keywords("Test")
                .taxonomy(Arrays.asList("Test", "Taxonomy"))
                .dbLinks(Map.of("Test", "DB001"))
                .references(references)
                .comment("This is a test sequence for translation tables")
                .build();
    }

    @Test
    public void testWithAllTranslationOptions() throws IOException {
        // Create fully-configured translation options
        TranslationOptions translationOptions = TranslationOptions.builder()
                .translTableNumber(5) // Invertebrate Mitochondrial Code
                .translateCDS(true)
                .includeStopCodon(true) // Include stop codon in translation
                .build();

        ConversionOptions options = ConversionOptions.builder()
                .organism("Test Organism")
                .moleculeType("DNA")
                .topology("linear")
                .annotationFormat("FASTA")
                .headerInfo(createHeaderInfo())
                .translationOptions(translationOptions)
                .build();

        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);
        File outputFile = tempDir.resolve("output_full_options.gb").toFile();
        result.writeToFile(outputFile);

        // Verify result
        assertTrue(outputFile.exists());
        String genbank = Files.readString(outputFile.toPath());

        // Verify translation table qualifier
        assertTrue(genbank.contains("/transl_table=5"));

        // Verify translation with stop codon (should be "MS*")
        String translation = extractTranslation(genbank);
        assertTrue(translation.endsWith("*"), "Translation should include stop codon");
    }

    @Test
    public void testPreferenceOrder() throws IOException {
        // Test that translTableNumber takes precedence over geneticCode string
        TranslationOptions mixedOptions = TranslationOptions.builder()
                .translTableNumber(1) // Should take precedence
                .geneticCode("5") // Should be ignored
                .build();

        ConversionOptions options = ConversionOptions.builder()
                .organism("Test Organism")
                .moleculeType("DNA")
                .annotationFormat("FASTA")
                .translationOptions(mixedOptions)
                .build();

        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);
        File outputFile = tempDir.resolve("output_precedence.gb").toFile();
        result.writeToFile(outputFile);

        // Verify that table 1 was used
        String genbank = Files.readString(outputFile.toPath());
        assertTrue(genbank.contains("/transl_table=1"));

        // Verify translation (should start with IR for standard code)
        String translation = extractTranslation(genbank);
        assertTrue(translation.startsWith("IR"));
    }
}