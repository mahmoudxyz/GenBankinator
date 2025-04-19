package xyz.mahmoudahmed.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import xyz.mahmoudahmed.api.AnnotationParser;
import xyz.mahmoudahmed.api.FormatDetector;
import xyz.mahmoudahmed.api.SequenceParser;
import xyz.mahmoudahmed.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DefaultGenbankValidatorTest {

    private DefaultGenbankValidator validator;
    private SequenceParser mockSequenceParser;
    private AnnotationParser mockAnnotationParser;
    private FormatDetector mockFormatDetector;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mockSequenceParser = Mockito.mock(SequenceParser.class);
        mockAnnotationParser = Mockito.mock(AnnotationParser.class);
        mockFormatDetector = Mockito.mock(FormatDetector.class);

        // Set up the validator with mocked dependencies
        validator = new DefaultGenbankValidator(
                List.of(mockSequenceParser),
                List.of(mockAnnotationParser),
                mockFormatDetector
        );
    }

    @Test
    void testValidateSequenceValid() throws IOException {
        // Create a test file
        File sequenceFile = Files.createFile(tempDir.resolve("sequence.fasta")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(sequenceFile)).thenReturn("FASTA");
        when(mockSequenceParser.supportsFormat("FASTA")).thenReturn(true);

        // Create sample sequence data
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGCATGC")
                .build());

        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        when(mockSequenceParser.parse(sequenceFile)).thenReturn(sequenceData);

        // Run the validation
        ValidationResult result = validator.validateSequence(sequenceFile);

        System.out.println(result.toString());
        System.out.println();
        // Verify results
        assertTrue(result.isValid());
        assertEquals("FASTA", result.getDetectedFormat());
        assertEquals(1, result.getSequenceCount());
        assertEquals(0, result.getIssues().size());
    }

    @Test
    void testValidateSequenceInvalidFormat() throws IOException {
        // Create a test file
        File sequenceFile = Files.createFile(tempDir.resolve("invalid.txt")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(sequenceFile)).thenReturn("TXT");
        when(mockSequenceParser.supportsFormat("TXT")).thenReturn(false);

        // Run the validation
        ValidationResult result = validator.validateSequence(sequenceFile);

        // Verify results
        assertFalse(result.isValid());
        assertEquals("TXT", result.getDetectedFormat());
        assertEquals(1, result.getIssues().size());
        assertEquals("ERROR", result.getIssues().get(0).getType());
        assertTrue(result.getIssues().get(0).getMessage().contains("Unsupported sequence format"));
    }

    @Test
    void testValidateSequenceEmptySequence() throws IOException {
        // Create a test file
        File sequenceFile = Files.createFile(tempDir.resolve("empty.fasta")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(sequenceFile)).thenReturn("FASTA");
        when(mockSequenceParser.supportsFormat("FASTA")).thenReturn(true);

        // Create sample sequence data with empty sequence
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("")
                .build());

        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        when(mockSequenceParser.parse(sequenceFile)).thenReturn(sequenceData);

        // Run the validation
        ValidationResult result = validator.validateSequence(sequenceFile);

        // Verify results
        assertTrue(result.isValid()); // Still valid even with empty sequence
        assertEquals(1, result.getSequenceCount());
        assertEquals(1, result.getIssues().size());
        assertEquals("WARNING", result.getIssues().get(0).getType());
        assertTrue(result.getIssues().get(0).getMessage().contains("Empty sequence"));
    }

    @Test
    void testValidateAnnotationValid() throws IOException {
        // Create a test file
        File annotationFile = Files.createFile(tempDir.resolve("annotation.gff")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(annotationFile)).thenReturn("GFF");
        when(mockAnnotationParser.supportsFormat("GFF")).thenReturn(true);

        // Create sample annotation data
        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(100)
                .featureId("gene1")
                .build());
        annotationMap.put("seq1", annotations);

        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockAnnotationParser.parse(annotationFile)).thenReturn(annotationData);

        // Run the validation
        ValidationResult result = validator.validateAnnotation(annotationFile, "GFF");

        // Verify results
        assertTrue(result.isValid());
        assertEquals("GFF", result.getDetectedFormat());
        assertEquals(1, result.getSequenceCount());
        assertEquals(1, result.getFeatureCount());
        assertEquals(0, result.getIssues().size());
    }

    @Test
    void testValidateAnnotationInvalidCoordinates() throws IOException {
        // Create a test file
        File annotationFile = Files.createFile(tempDir.resolve("invalid_coords.gff")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(annotationFile)).thenReturn("GFF");
        when(mockAnnotationParser.supportsFormat("GFF")).thenReturn(true);

        // Create sample annotation data with invalid coordinates
        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(100)  // End before start is invalid
                .end(50)
                .featureId("gene1")
                .build());
        annotationMap.put("seq1", annotations);

        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockAnnotationParser.parse(annotationFile)).thenReturn(annotationData);

        // Run the validation
        ValidationResult result = validator.validateAnnotation(annotationFile, "GFF");

        // Verify results
        assertFalse(result.isValid());
        assertEquals(1, result.getFeatureCount());
        assertEquals(1, result.getIssues().size());
        assertEquals("WARNING", result.getIssues().get(0).getType());
        assertTrue(result.getIssues().get(0).getMessage().contains("Invalid coordinates"));
    }

    @Test
    void testValidateCompatibilityValid() throws IOException {
        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("seq.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("ann.gff")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(sequenceFile)).thenReturn("FASTA");
        when(mockFormatDetector.detectFormat(annotationFile)).thenReturn("GFF");
        when(mockSequenceParser.supportsFormat("FASTA")).thenReturn(true);
        when(mockAnnotationParser.supportsFormat("GFF")).thenReturn(true);

        // Create sample sequence data
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGCATGCATGCATGCATGC")
                .length(20)
                .build());

        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        // Create sample annotation data
        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(10)
                .featureId("gene1")
                .build());
        annotationMap.put("seq1", annotations);

        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockSequenceParser.parse(any(File.class))).thenReturn(sequenceData);
        when(mockAnnotationParser.parse(any(File.class))).thenReturn(annotationData);

        // Run the validation
        ValidationResult result = validator.validateCompatibility(sequenceFile, annotationFile);

        // Verify results
        assertTrue(result.isValid());
        assertEquals(1, result.getSequenceCount());
        assertEquals(1, result.getFeatureCount());
        assertEquals(0, result.getIssues().size());
    }

    @Test
    void testValidateCompatibilityUnmatchedSequence() throws IOException {
        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("seq.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("ann.gff")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(sequenceFile)).thenReturn("FASTA");
        when(mockFormatDetector.detectFormat(annotationFile)).thenReturn("GFF");
        when(mockSequenceParser.supportsFormat("FASTA")).thenReturn(true);
        when(mockAnnotationParser.supportsFormat("GFF")).thenReturn(true);

        // Create sample sequence data
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGCATGC")
                .length(8)
                .build());

        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        // Create sample annotation data with reference to non-existent sequence
        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq2")  // This ID doesn't exist in sequences
                .type("gene")
                .start(0)
                .end(10)
                .featureId("gene1")
                .build());
        annotationMap.put("seq2", annotations);

        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockSequenceParser.parse(any(File.class))).thenReturn(sequenceData);
        when(mockAnnotationParser.parse(any(File.class))).thenReturn(annotationData);

        // Run the validation
        ValidationResult result = validator.validateCompatibility(sequenceFile, annotationFile);

        // Verify results
        assertFalse(result.isValid());
        assertEquals(1, result.getIssues().size());
        assertTrue(result.getIssues().get(0).getMessage().contains("Annotation references sequence that doesn't exist"));
    }

    @Test
    void testValidateCompatibilityFeatureOutOfBounds() throws IOException {
        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("seq.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("ann.gff")).toFile();

        // Set up mocks
        when(mockFormatDetector.detectFormat(sequenceFile)).thenReturn("FASTA");
        when(mockFormatDetector.detectFormat(annotationFile)).thenReturn("GFF");
        when(mockSequenceParser.supportsFormat("FASTA")).thenReturn(true);
        when(mockAnnotationParser.supportsFormat("GFF")).thenReturn(true);

        // Create sample sequence data
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGCATGC")
                .length(8)  // Length is 8
                .build());

        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        // Create sample annotation data with feature extending beyond sequence end
        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(20)  // End is beyond sequence length
                .featureId("gene1")
                .build());
        annotationMap.put("seq1", annotations);

        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockSequenceParser.parse(any(File.class))).thenReturn(sequenceData);
        when(mockAnnotationParser.parse(any(File.class))).thenReturn(annotationData);

        // Run the validation
        ValidationResult result = validator.validateCompatibility(sequenceFile, annotationFile);

        // Verify results
        assertFalse(result.isValid());
        assertEquals(1, result.getIssues().size());
        assertTrue(result.getIssues().get(0).getMessage().contains("Feature gene1 extends beyond sequence"));
    }
}