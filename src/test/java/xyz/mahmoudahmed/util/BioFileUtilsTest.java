package xyz.mahmoudahmed.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import xyz.mahmoudahmed.service.FileService;
import xyz.mahmoudahmed.service.FormatDetectionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BioFileUtilsTest {

    private BioFileUtils bioFileUtils;
    private FormatDetectionService mockFormatService;
    private FileService mockFileService;

    @BeforeEach
    void setUp() {
        mockFormatService = mock(FormatDetectionService.class);
        mockFileService = mock(FileService.class);
        FormatConfiguration mockConfig = mock(FormatConfiguration.class);
        bioFileUtils = new BioFileUtils(mockFormatService, mockFileService, mockConfig);
    }

    @Test
    void detectFormat_delegatesToFormatService(@TempDir Path tempDir) throws FileProcessingException {
        // Prepare
        File testFile = tempDir.resolve("test.fa").toFile();
        when(mockFormatService.detectFormat(testFile)).thenReturn("FASTA");

        // Test
        String format = bioFileUtils.detectFormat(testFile);

        // Verify
        assertEquals("FASTA", format);
        verify(mockFormatService).detectFormat(testFile);
    }


    @Test
    void isFastaAnnotationFile_nonFastaFile_returnsFalse(@TempDir Path tempDir) throws FileProcessingException {
        // Prepare
        File testFile = tempDir.resolve("test.txt").toFile();
        when(mockFormatService.detectFormat(testFile)).thenReturn("UNKNOWN");

        // Test
        boolean result = bioFileUtils.isFastaAnnotationFile(testFile);

        // Verify
        assertFalse(result);
        verify(mockFormatService).detectFormat(testFile);
    }

    @Test
    void readFileToString_delegatesToFileService(@TempDir Path tempDir) throws IOException {
        // Prepare
        File testFile = tempDir.resolve("test.txt").toFile();
        String expectedContent = "File content";
        when(mockFileService.readFileToString(testFile)).thenReturn(expectedContent);

        // Test
        String content = bioFileUtils.readFileToString(testFile);

        // Verify
        assertEquals(expectedContent, content);
        verify(mockFileService).readFileToString(testFile);
    }

    @Test
    void writeStringToFile_delegatesToFileService(@TempDir Path tempDir) throws IOException {
        // Prepare
        File testFile = tempDir.resolve("test.txt").toFile();
        String content = "Content to write";

        // Test
        bioFileUtils.writeStringToFile(content, testFile);

        // Verify
        verify(mockFileService).writeStringToFile(content, testFile);
    }

    @Test
    void appendStringToFile_delegatesToFileService(@TempDir Path tempDir) throws IOException {
        // Prepare
        File testFile = tempDir.resolve("test.txt").toFile();
        String content = "Content to append";

        // Test
        bioFileUtils.appendStringToFile(content, testFile);

        // Verify
        verify(mockFileService).appendStringToFile(content, testFile);
    }

    @Test
    void createTempFile_delegatesToFileService() throws IOException {
        // Prepare
        String prefix = "prefix";
        String suffix = ".tmp";
        File expectedFile = new File("/tmp/mock");
        when(mockFileService.createTempFile(prefix, suffix)).thenReturn(expectedFile);

        // Test
        File tempFile = bioFileUtils.createTempFile(prefix, suffix);

        // Verify
        assertEquals(expectedFile, tempFile);
        verify(mockFileService).createTempFile(prefix, suffix);
    }

    @Test
    void create_staticFactory_returnsInstance() {
        // Test static factory method
        BioFileUtils utils = BioFileUtils.create();

        // Verify
        assertNotNull(utils);
    }
}
