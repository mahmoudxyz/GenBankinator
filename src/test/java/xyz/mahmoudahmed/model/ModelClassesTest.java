package xyz.mahmoudahmed.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ModelClassesTest {

    @Test
    void testSequenceBuilder() {
        Sequence sequence = Sequence.builder()
                .id("test1")
                .name("Test Sequence")
                .description("A test sequence")
                .sequence("ATGCATGC")
                .moleculeType("DNA")
                .topology("linear")
                .division("VRL")
                .organism("Test Organism")
                .build();

        assertEquals("test1", sequence.getId());
        assertEquals("Test Sequence", sequence.getName());
        assertEquals("A test sequence", sequence.getDescription());
        assertEquals("ATGCATGC", sequence.getSequence());
        assertEquals(8, sequence.getLength());
        assertEquals("DNA", sequence.getMoleculeType());
        assertEquals("linear", sequence.getTopology());
        assertEquals("VRL", sequence.getDivision());
        assertEquals("Test Organism", sequence.getOrganism());
    }

    @Test
    void testSequenceDataBuilder() {
        Sequence sequence1 = Sequence.builder()
                .id("test1")
                .sequence("ATGC")
                .build();

        Sequence sequence2 = Sequence.builder()
                .id("test2")
                .sequence("GTATC")
                .build();

        SequenceData data = SequenceData.builder()
                .addSequence(sequence1)
                .addSequence(sequence2)
                .build();

        assertEquals(2, data.getCount());
        assertEquals("test1", data.getSequence("test1").getId());
        assertEquals("test2", data.getSequence("test2").getId());
        assertEquals(sequence1, data.getSequences().get(0));
        assertEquals(sequence2, data.getSequences().get(1));
    }

    @Test
    void testAnnotationBuilder() {
        Map<String, List<String>> qualifiers = new HashMap<>();
        qualifiers.put("gene", List.of("test_gene"));
        qualifiers.put("product", List.of("Test protein"));

        Annotation annotation = Annotation.builder()
                .type("gene")
                .start(0)
                .end(100)
                .strand(1)
                .phase(0)
                .sequenceId("test1")
                .featureId("feature1")
                .qualifiers(qualifiers)
                .build();

        assertEquals("gene", annotation.getType());
        assertEquals(0, annotation.getStart());
        assertEquals(100, annotation.getEnd());
        assertEquals(1, annotation.getStrand());
        assertEquals(0, annotation.getPhase().intValue());
        assertEquals("test1", annotation.getSequenceId());
        assertEquals("feature1", annotation.getFeatureId());
        assertEquals(qualifiers, annotation.getQualifiers());
        assertEquals("test_gene", annotation.getQualifiers().get("gene").get(0));
    }

    @Test
    void testAnnotationDataBuilder() {
        Annotation annotation1 = Annotation.builder()
                .sequenceId("test1")
                .type("gene")
                .featureId("feature1")
                .build();

        Annotation annotation2 = Annotation.builder()
                .sequenceId("test1")
                .type("CDS")
                .featureId("feature2")
                .build();

        Annotation annotation3 = Annotation.builder()
                .sequenceId("test2")
                .type("gene")
                .featureId("feature3")
                .build();

        AnnotationData data = AnnotationData.builder()
                .addAnnotation(annotation1)
                .addAnnotation(annotation2)
                .addAnnotation(annotation3)
                .build();

        assertEquals(2, data.getAnnotationsBySequence().size());
        assertEquals(3, data.getTotalCount());
        assertEquals(2, data.getAnnotationsForSequence("test1").size());
        assertEquals(1, data.getAnnotationsForSequence("test2").size());
        assertEquals(0, data.getAnnotationsForSequence("test3").size()); // Non-existent
    }

    @Test
    void testValidationResultBuilder() {
        ValidationIssue issue1 = ValidationIssue.builder()
                .type("WARNING")
                .message("Test warning")
                .location("Line 10")
                .build();

        ValidationIssue issue2 = ValidationIssue.builder()
                .type("ERROR")
                .message("Test error")
                .build();

        ValidationResult result = ValidationResult.builder()
                .valid(false)
                .detectedFormat("FASTA")
                .sequenceCount(2)
                .featureCount(5)
                .addIssue(issue1)
                .addIssue(issue2)
                .summary("Validation failed with 1 error and 1 warning")
                .build();

        assertFalse(result.isValid());
        assertEquals("FASTA", result.getDetectedFormat());
        assertEquals(2, result.getSequenceCount());
        assertEquals(5, result.getFeatureCount());
        assertEquals(2, result.getIssues().size());
        assertEquals("WARNING", result.getIssues().get(0).getType());
        assertEquals("ERROR", result.getIssues().get(1).getType());
        assertEquals("Line 10", result.getIssues().get(0).getLocation());
        assertEquals("Validation failed with 1 error and 1 warning", result.getSummary());
    }

    @Test
    void testGenbankResultBuilder() throws IOException {
        byte[] genbankData = "LOCUS test 100 bp DNA".getBytes();

        GenbankResult result = GenbankResult.builder()
                .genbankData(genbankData)
                .sequenceCount(1)
                .featureCount(2)
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        assertArrayEquals(genbankData, result.getGenbankData());
        assertEquals(1, result.getSequenceCount());
        assertEquals(2, result.getFeatureCount());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), result.getTimestamp());

        // Test file writing
        Path tempFile = Files.createTempFile("test_genbank", ".gb");
        result.writeToFile(tempFile.toFile());

        byte[] readBytes = Files.readAllBytes(tempFile);
        assertArrayEquals(genbankData, readBytes);

        Files.delete(tempFile);
    }

    @Test
    void testConversionOptionsBuilder() {
        // Create sub-option objects
        TranslationOptions translationOptions = TranslationOptions.builder()
                .geneticCode("vertebrate_mitochondrial")
                .build();

        FeatureFormattingOptions featureFormattingOptions = FeatureFormattingOptions.builder()
                .standardizeFeatureTypes(true)
                .build();

        OutputFormattingOptions outputFormattingOptions = OutputFormattingOptions.builder()
                .sequenceLineWidth(60)
                .lowercaseSequence(false)
                .build();

        FeatureFilterOptions filterOptions = FeatureFilterOptions.builder()
                .includeFeatureTypes(List.of("gene", "CDS"))
                .build();

        Map<String, String> customMetadata = new HashMap<>();
        customMetadata.put("custom1", "value1");

        // Create conversion options
        ConversionOptions options = ConversionOptions.builder()
                .organism("Test Organism")
                .moleculeType("DNA")
                .topology("circular")
                .division("VRL")
                .annotationFormat("GFF")
                .mergeSequences(false)
                .translationOptions(translationOptions)
                .featureFormattingOptions(featureFormattingOptions)
                .outputFormattingOptions(outputFormattingOptions)
                .filterOptions(filterOptions)
                .customMetadata(customMetadata)
                .build();

        assertEquals("Test Organism", options.getOrganism());
        assertEquals("DNA", options.getMoleculeType());
        assertEquals("circular", options.getTopology());
        assertEquals("VRL", options.getDivision());
        assertEquals("GFF", options.getAnnotationFormat());
        assertFalse(options.isMergeSequences());
        assertSame(translationOptions, options.getTranslationOptions());
        assertSame(featureFormattingOptions, options.getFeatureFormattingOptions());
        assertSame(outputFormattingOptions, options.getOutputFormattingOptions());
        assertSame(filterOptions, options.getFilterOptions());
        assertEquals(customMetadata, options.getCustomMetadata());
    }

    @Test
    void testGenbankOptionsBuilder() {
        GenbankOptions options = GenbankOptions.builder()
                .defaultOrganism("Default Organism")
                .defaultMoleculeType("DNA")
                .defaultTopology("linear")
                .defaultDivision("UNC")
                .memoryEfficient(true)
                .memoryThreshold(50 * 1024 * 1024) // 50MB
                .tempDirectory("/tmp/genbank")
                .build();

        assertEquals("Default Organism", options.getDefaultOrganism());
        assertEquals("DNA", options.getDefaultMoleculeType());
        assertEquals("linear", options.getDefaultTopology());
        assertEquals("UNC", options.getDefaultDivision());
        assertTrue(options.isMemoryEfficient());
        assertEquals(50 * 1024 * 1024, options.getMemoryThreshold());
        assertEquals("/tmp/genbank", options.getTempDirectory());
    }
}