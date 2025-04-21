package xyz.mahmoudahmed.format;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FastaFormatDetectorTest {

    private FastaFormatDetector detector;
    private FormatConfiguration mockConfig;

    @BeforeEach
    void setUp() {
        mockConfig = mock(FormatConfiguration.class);
        detector = new FastaFormatDetector(mockConfig);
    }

    @Test
    void canDetect_validExtension_returnsTrue(@TempDir Path tempDir) throws IOException {
        // Create a test file with a valid FASTA extension
        File testFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(">Sequence1\nACGT\n");
        }

        assertTrue(detector.canDetect(testFile));
    }

    @Test
    void canDetect_invalidExtension_returnsFalse(@TempDir Path tempDir) throws IOException {
        // Create a test file with an invalid extension
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(">Sequence1\nACGT\n");
        }

        assertFalse(detector.canDetect(testFile));
    }

    @Test
    void detectFormat_validFastaContent_returnsFasta(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a test file with valid FASTA content
        File testFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(">Sequence1\nACGT\n>Sequence2\nTGCA\n");
        }

        assertEquals("FASTA", detector.detectFormat(testFile));
    }

    @Test
    void detectFormat_invalidContent_returnsUnknown(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a test file with invalid content
        File testFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is not a FASTA file\n");
        }

        assertEquals("UNKNOWN", detector.detectFormat(testFile));
    }

    @Test
    void isFastaAnnotationFile_validAnnotation_returnsTrue(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a test file with valid FASTA annotation content
        File testFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(">gene1;1000-2000;+;CDS\nACGT\n>gene2;3000-4000;-;CDS\nTGCA\n");
        }

        assertTrue(detector.isFastaAnnotationFile(testFile));
    }

    @Test
    void isFastaAnnotationFile_regularFasta_returnsFalse(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a test file with regular FASTA content (no annotations)
        File testFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(">Sequence1\nACGT\n>Sequence2\nTGCA\n");
        }

        assertFalse(detector.isFastaAnnotationFile(testFile));
    }
}
