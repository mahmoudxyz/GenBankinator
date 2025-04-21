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
import static org.mockito.Mockito.mock;

class GffFormatDetectorTest {

    private GffFormatDetector detector;
    private FormatConfiguration mockConfig;

    @BeforeEach
    void setUp() {
        mockConfig = mock(FormatConfiguration.class);
        detector = new GffFormatDetector(mockConfig);
    }

    @Test
    void canDetect_validExtension_returnsTrue(@TempDir Path tempDir) throws IOException {
        // Create a test file with a valid GFF extension
        File testFile = tempDir.resolve("test.gff").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("##gff-version 3\n");
        }

        assertTrue(detector.canDetect(testFile));
    }

    @Test
    void canDetect_invalidExtension_returnsFalse(@TempDir Path tempDir) throws IOException {
        // Create a test file with an invalid extension
        File testFile = tempDir.resolve("test.txt").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("##gff-version 3\n");
        }

        assertFalse(detector.canDetect(testFile));
    }

    @Test
    void detectFormat_validGffContent_returnsGff(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a test file with valid GFF content
        File testFile = tempDir.resolve("test.gff").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("##gff-version 3\nchromosome1\t.\tgene\t1\t1000\t.\t+\t.\tID=gene1\n");
        }

        assertEquals("GFF", detector.detectFormat(testFile));
    }

    @Test
    void detectFormat_invalidContent_returnsUnknown(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a test file with invalid content
        File testFile = tempDir.resolve("test.gff").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is not a GFF file\n");
        }

        assertEquals("UNKNOWN", detector.detectFormat(testFile));
    }
}
