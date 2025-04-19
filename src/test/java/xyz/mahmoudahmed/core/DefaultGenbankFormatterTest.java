package xyz.mahmoudahmed.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGenbankFormatterTest {

    private DefaultGenbankFormatter formatter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        formatter = new DefaultGenbankFormatter();
    }

    @Test
    void testFormatBasicSequence() {
        // Create a simple sequence with no features
        Sequence sequence = Sequence.builder()
                .id("TestSeq")
                .name("TestSeq")
                .description("Test sequence")
                .sequence("ATGCATGCATGC")
                .organism("Test organism")
                .moleculeType("DNA")
                .topology("linear")
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence)
                .build();

        // Empty annotations
        AnnotationData annotationData = AnnotationData.builder().build();

        // Format
        ConversionOptions options = ConversionOptions.builder().build();
        byte[] result = formatter.format(sequenceData, annotationData, options);

        // Convert to string for easier testing
        String genbankString = new String(result);

        // Verify basic structure
        assertTrue(genbankString.contains("LOCUS       TestSeq"));
        assertTrue(genbankString.contains("DEFINITION  Test sequence"));
        assertTrue(genbankString.contains("ACCESSION   TestSeq"));
        assertTrue(genbankString.contains("SOURCE      Test organism"));
        assertTrue(genbankString.contains("ORIGIN"));
        assertTrue(genbankString.contains("//"));

        // Verify the sequence is included
        assertTrue(genbankString.contains("ATGCATGCATGC"));
    }

    @Test
    void testFormatWithFeatures() {
        // Create a sequence with features
        Sequence sequence = Sequence.builder()
                .id("TestSeq")
                .name("TestSeq")
                .description("Test sequence with features")
                .sequence("ATGCATGCATGCATGCATGCATGCATGC")
                .organism("Test organism")
                .moleculeType("DNA")
                .topology("linear")
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence)
                .build();

        // Create annotations
        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();

        // Gene annotation
        Map<String, List<String>> geneQualifiers = new HashMap<>();
        geneQualifiers.put("gene", List.of("test_gene"));

        Annotation geneAnnotation = Annotation.builder()
                .sequenceId("TestSeq")
                .type("gene")
                .start(0)
                .end(12)
                .strand(1)
                .featureId("gene1")
                .qualifiers(geneQualifiers)
                .build();

        // CDS annotation
        Map<String, List<String>> cdsQualifiers = new HashMap<>();
        cdsQualifiers.put("gene", List.of("test_gene"));
        cdsQualifiers.put("product", List.of("Test protein"));
        cdsQualifiers.put("translation", List.of("MHAC"));

        Annotation cdsAnnotation = Annotation.builder()
                .sequenceId("TestSeq")
                .type("CDS")
                .start(0)
                .end(12)
                .strand(1)
                .featureId("cds1")
                .qualifiers(cdsQualifiers)
                .build();

        annotations.add(geneAnnotation);
        annotations.add(cdsAnnotation);
        annotationMap.put("TestSeq", annotations);

        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        // Format
        ConversionOptions options = ConversionOptions.builder().build();
        byte[] result = formatter.format(sequenceData, annotationData, options);

        // Convert to string for easier testing
        String genbankString = new String(result);

        // Verify features section
        assertTrue(genbankString.contains("FEATURES"));
        assertTrue(genbankString.contains("gene            1..12"));
        assertTrue(genbankString.contains("/gene=\"test_gene\""));
        assertTrue(genbankString.contains("CDS             1..12"));
        assertTrue(genbankString.contains("/product=\"Test protein\""));
        assertTrue(genbankString.contains("/translation=\"MHAC\""));
    }

    @Test
    void testFormatWithCustomOptions() {
        // Create a sequence
        Sequence sequence = Sequence.builder()
                .id("TestSeq")
                .name("TestSeq")
                .description("Test sequence")
                .sequence("ATGCATGCATGC")
                .organism("Test organism")
                .moleculeType("DNA")
                .topology("linear")
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence)
                .build();

        // Empty annotations
        AnnotationData annotationData = AnnotationData.builder().build();

        // Custom options
        ConversionOptions options = ConversionOptions.builder()
                .organism("Custom organism")
                .moleculeType("RNA")
                .topology("circular")
                .division("VRL")
                .outputFormattingOptions(OutputFormattingOptions.builder()
                        .sequenceLineWidth(30)
                        .lowercaseSequence(true)
                        .build())
                .build();

        // Format
        byte[] result = formatter.format(sequenceData, annotationData, options);

        // Convert to string for easier testing
        String genbankString = new String(result);

        System.out.println();
        // Verify custom options were applied
        assertTrue(genbankString.contains("SOURCE      Custom organism"));
        assertTrue(genbankString.contains("RNA"));
        assertTrue(genbankString.contains("circular"));

        // Verify lowercase sequence
        System.out.println();
        assertTrue(genbankString.contains("atgcatgcatgc"));
    }

    @Test
    void testFormatMultipleSequences() {
        // Create multiple sequences
        Sequence sequence1 = Sequence.builder()
                .id("Seq1")
                .name("Seq1")
                .description("First sequence")
                .sequence("ATGCATGC")
                .organism("Test organism")
                .build();

        Sequence sequence2 = Sequence.builder()
                .id("Seq2")
                .name("Seq2")
                .description("Second sequence")
                .sequence("GTATATATAT")
                .organism("Test organism")
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence1)
                .addSequence(sequence2)
                .build();

        // Empty annotations
        AnnotationData annotationData = AnnotationData.builder().build();

        // Format
        ConversionOptions options = ConversionOptions.builder().build();
        byte[] result = formatter.format(sequenceData, annotationData, options);

        // Convert to string for easier testing
        String genbankString = new String(result);

        // Verify both sequences are included
        assertTrue(genbankString.contains("LOCUS       Seq1"));
        assertTrue(genbankString.contains("LOCUS       Seq2"));
        assertTrue(genbankString.contains("DEFINITION  First sequence"));
        assertTrue(genbankString.contains("DEFINITION  Second sequence"));

        // Verify there are two record separators
        assertEquals(2, genbankString.split("//").length - 1);
    }

    @Test
    void testFormatToStream() throws IOException {
        // Create a sequence
        Sequence sequence = Sequence.builder()
                .id("TestSeq")
                .name("TestSeq")
                .description("Test sequence")
                .sequence("ATGCATGC")
                .organism("Test organism")
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence)
                .build();

        // Empty annotations
        AnnotationData annotationData = AnnotationData.builder().build();

        // Format to stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ConversionOptions options = ConversionOptions.builder().build();

        formatter.formatToStream(sequenceData, annotationData, outputStream, options);

        // Get result as string
        String genbankString = outputStream.toString();

        // Verify basic structure
        assertTrue(genbankString.contains("LOCUS       TestSeq"));
        assertTrue(genbankString.contains("DEFINITION  Test sequence"));
        assertTrue(genbankString.contains("//"));
    }

    @Test
    void testFormatWithHeaderInfo() {
        // Create a sequence with detailed header information
        Sequence sequence = Sequence.builder()
                .id("TestSeq")
                .name("TestSeq")
                .description("Test sequence")
                .sequence("ATGCATGC")
                .organism("Test organism")
                .headerInfo(HeaderInfo.builder()
                        .accessionNumber("XYZ12345")
                        .version("XYZ12345.1")
                        .definition("Test sequence with detailed header")
                        .keywords("test, example, demo")
                        .taxonomy(List.of("Bacteria", "Proteobacteria", "Testaceae"))
                        .comment("This is a test sequence for formatting")
                        .build())
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence)
                .build();

        // Empty annotations
        AnnotationData annotationData = AnnotationData.builder().build();

        // Format
        ConversionOptions options = ConversionOptions.builder().build();
        byte[] result = formatter.format(sequenceData, annotationData, options);

        // Convert to string for easier testing
        String genbankString = new String(result);

        // Verify header information
        assertTrue(genbankString.contains("ACCESSION   XYZ12345"));
        assertTrue(genbankString.contains("VERSION     XYZ12345.1"));
        assertTrue(genbankString.contains("KEYWORDS    test, example, demo"));
        assertTrue(genbankString.contains("DEFINITION  Test sequence with detailed header"));
        assertTrue(genbankString.contains("Bacteria; Proteobacteria; Testaceae"));
        assertTrue(genbankString.contains("COMMENT     This is a test sequence for formatting"));
    }

    @Test
    void testFormatSkipSequence() {
        // Create a sequence
        Sequence sequence = Sequence.builder()
                .id("TestSeq")
                .name("TestSeq")
                .description("Test sequence")
                .sequence("ATGCATGCATGC")
                .organism("Test organism")
                .build();

        SequenceData sequenceData = SequenceData.builder()
                .addSequence(sequence)
                .build();

        // Empty annotations
        AnnotationData annotationData = AnnotationData.builder().build();

        // Options to skip sequence
        ConversionOptions options = ConversionOptions.builder()
                .outputFormattingOptions(OutputFormattingOptions.builder()
                        .includeSequence(false)
                        .build())
                .build();

        // Format
        byte[] result = formatter.format(sequenceData, annotationData, options);

        // Convert to string for easier testing
        String genbankString = new String(result);

        // Verify the ORIGIN section exists but no sequence data
        assertTrue(genbankString.contains("ORIGIN"));
        assertTrue(genbankString.contains("//"));
        assertFalse(genbankString.contains("ATGCATGCATGC"));
    }
}