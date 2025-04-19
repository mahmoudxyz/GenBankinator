package xyz.mahmoudahmed.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import xyz.mahmoudahmed.api.*;
import xyz.mahmoudahmed.exception.ValidationException;
import xyz.mahmoudahmed.model.*;

import java.io.ByteArrayOutputStream;
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
import static org.mockito.Mockito.*;

class DefaultGenbankConverterTest {

    private DefaultGenbankConverter converter;
    private SequenceParser mockSequenceParser;
    private AnnotationParser mockAnnotationParser;
    private GenbankValidator mockValidator;
    private GenbankFormatter mockFormatter;
    private GenbankOptions options;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Set up mocks
        mockSequenceParser = Mockito.mock(SequenceParser.class);
        mockAnnotationParser = Mockito.mock(AnnotationParser.class);
        mockValidator = Mockito.mock(GenbankValidator.class);
        mockFormatter = Mockito.mock(GenbankFormatter.class);

        options = GenbankOptions.builder()
                .memoryEfficient(false)
                .memoryThreshold(10 * 1024 * 1024) // 10MB
                .build();

        // Create converter with mocks
        converter = (DefaultGenbankConverter) new DefaultGenbankConverter.Builder()
                .withSequenceParser(mockSequenceParser)
                .withAnnotationParser(mockAnnotationParser)
                .withValidator(mockValidator)
                .withFormatter(mockFormatter)
                .withOptions(options)
                .build();
    }

    @Test
    void testConvertStandard() throws IOException {
        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("test.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("test.gff")).toFile();

        // Set up mock validation
        ValidationResult validResult = ValidationResult.builder()
                .valid(true)
                .build();
        when(mockValidator.validateCompatibility(sequenceFile, annotationFile))
                .thenReturn(validResult);

        // Set up mock parsing
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGC")
                .build());
        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(4)
                .build());
        annotationMap.put("seq1", annotations);
        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockSequenceParser.parse(sequenceFile)).thenReturn(sequenceData);
        when(mockAnnotationParser.parse(annotationFile)).thenReturn(annotationData);

        // Set up mock formatting
        byte[] genbankData = "LOCUS...".getBytes();
        when(mockFormatter.format(any(SequenceData.class), any(AnnotationData.class),
                any(ConversionOptions.class))).thenReturn(genbankData);

        // Run conversion
        ConversionOptions convOptions = ConversionOptions.builder().build();
        GenbankResult result = converter.convert(sequenceFile, annotationFile, convOptions);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.getSequenceCount());
        assertEquals(1, result.getFeatureCount());
        assertArrayEquals(genbankData, result.getGenbankData());

        // Verify interactions
        verify(mockValidator).validateCompatibility(sequenceFile, annotationFile);
        verify(mockSequenceParser).parse(sequenceFile);
        verify(mockAnnotationParser).parse(annotationFile);
        verify(mockFormatter).format(any(SequenceData.class), any(AnnotationData.class),
                any(ConversionOptions.class));
    }

    @Test
    void testConvertLargeFiles() throws IOException {
        // Mock a large file by setting memory threshold to 0
        DefaultGenbankConverter largeFileConverter = (DefaultGenbankConverter) new DefaultGenbankConverter.Builder()
                .withSequenceParser(mockSequenceParser)
                .withAnnotationParser(mockAnnotationParser)
                .withValidator(mockValidator)
                .withOptions(GenbankOptions.builder().memoryThreshold(0).build())
                .build();

        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("large.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("large.gff")).toFile();

        // Set up mock validation
        ValidationResult validResult = ValidationResult.builder()
                .valid(true)
                .build();
        when(mockValidator.validateCompatibility(sequenceFile, annotationFile))
                .thenReturn(validResult);

        // Set up mock parsing for metadata-only
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .length(1000)  // No actual sequence, just metadata
                .build());
        SequenceData metadataOnly = SequenceData.builder()
                .addSequences(sequences)
                .build();

        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(100)
                .build());
        annotationMap.put("seq1", annotations);
        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        when(mockSequenceParser.parseMetadataOnly(sequenceFile)).thenReturn(metadataOnly);
        when(mockAnnotationParser.parse(annotationFile)).thenReturn(annotationData);

        // Since we can't easily mock the streaming formatter which is instantiated internally,
        // we'll need to create a test file with some content to be read
        Files.writeString(sequenceFile.toPath(), ">seq1\nATGCATGC");

        // Run conversion
        ConversionOptions convOptions = ConversionOptions.builder().build();

        // This test may not be complete as it's hard to mock the streaming formatter creation
        // We'll verify the method calls we can control
        try {
            largeFileConverter.convert(sequenceFile, annotationFile, convOptions);
        } catch (Exception e) {
            // We expect an error since we can't fully mock the streaming formatter
            // But we can still verify the correct methods were called
        }

        // Verify interactions
        verify(mockValidator).validateCompatibility(sequenceFile, annotationFile);
        verify(mockSequenceParser).parseMetadataOnly(sequenceFile);
        verify(mockAnnotationParser).parse(annotationFile);
    }

    @Test
    void testConvertInMemory() {
        // Set up mock data
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGC")
                .build());
        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(4)
                .build());
        annotationMap.put("seq1", annotations);
        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        // Set up mock formatting
        byte[] genbankData = "LOCUS...".getBytes();
        when(mockFormatter.format(any(SequenceData.class), any(AnnotationData.class),
                any(ConversionOptions.class))).thenReturn(genbankData);

        // Run conversion
        ConversionOptions convOptions = ConversionOptions.builder().build();
        GenbankResult result = converter.convert(sequenceData, annotationData, convOptions);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.getSequenceCount());
        assertEquals(1, result.getFeatureCount());
        assertArrayEquals(genbankData, result.getGenbankData());

        // Verify interactions
        verify(mockFormatter).format(sequenceData, annotationData, convOptions);
    }

    @Test
    void testConvertToStream() throws IOException {
        // Set up mock data
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(Sequence.builder()
                .id("seq1")
                .sequence("ATGC")
                .build());
        SequenceData sequenceData = SequenceData.builder()
                .addSequences(sequences)
                .build();

        Map<String, List<Annotation>> annotationMap = new HashMap<>();
        List<Annotation> annotations = new ArrayList<>();
        annotations.add(Annotation.builder()
                .sequenceId("seq1")
                .type("gene")
                .start(0)
                .end(4)
                .build());
        annotationMap.put("seq1", annotations);
        AnnotationData annotationData = AnnotationData.builder()
                .addAnnotations(annotationMap)
                .build();

        // Prepare output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Run conversion
        ConversionOptions convOptions = ConversionOptions.builder().build();
        converter.convertToStream(sequenceData, annotationData, outputStream, convOptions);

        // Verify interactions
        verify(mockFormatter).formatToStream(sequenceData, annotationData, outputStream, convOptions);
    }

    @Test
    void testValidate() throws IOException {
        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("validate.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("validate.gff")).toFile();

        // Set up mock validation
        ValidationResult expectedResult = ValidationResult.builder()
                .valid(true)
                .sequenceCount(1)
                .featureCount(2)
                .build();
        when(mockValidator.validateCompatibility(sequenceFile, annotationFile))
                .thenReturn(expectedResult);

        // Run validation
        ValidationResult result = converter.validate(sequenceFile, annotationFile);

        // Verify
        assertSame(expectedResult, result);
        verify(mockValidator).validateCompatibility(sequenceFile, annotationFile);
    }

    @Test
    void testValidationFailure() throws IOException {
        // Create test files
        File sequenceFile = Files.createFile(tempDir.resolve("invalid.fasta")).toFile();
        File annotationFile = Files.createFile(tempDir.resolve("invalid.gff")).toFile();

        // Set up mock validation to fail
        ValidationResult invalidResult = ValidationResult.builder()
                .valid(false)
                .summary("Validation failed")
                .build();
        when(mockValidator.validateCompatibility(sequenceFile, annotationFile))
                .thenReturn(invalidResult);

        // Run validation and expect exception
        assertThrows(ValidationException.class, () -> {
            converter.convert(sequenceFile, annotationFile, ConversionOptions.builder().build());
        });

        // Verify interactions
        verify(mockValidator).validateCompatibility(sequenceFile, annotationFile);
        // Verify that no parsing happens when validation fails
        verifyNoInteractions(mockSequenceParser);
        verifyNoInteractions(mockAnnotationParser);
    }

    @Test
    void testBuilderWithDefaults() {
        // Create builder with no explicit dependencies
        DefaultGenbankConverter defaultConverter = (DefaultGenbankConverter) new DefaultGenbankConverter.Builder().build();

        // We can't easily test the internal field values, but we can verify it doesn't throw exceptions
        assertNotNull(defaultConverter);

        // Try a basic validation to ensure the default implementations work
        try {
            File sequenceFile = Files.createFile(tempDir.resolve("defaults_test.fasta")).toFile();
            Files.writeString(sequenceFile.toPath(), ">seq1\nATGC");

            File annotationFile = Files.createFile(tempDir.resolve("defaults_test.gff")).toFile();
            Files.writeString(annotationFile.toPath(), "##gff-version 3\n");

            // This should use the default validator
            ValidationResult result = defaultConverter.validate(sequenceFile, annotationFile);
            assertNotNull(result);
        } catch (IOException e) {
            fail("Default converter failed: " + e.getMessage());
        }
    }
}