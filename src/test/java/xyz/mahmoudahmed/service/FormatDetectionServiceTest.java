package xyz.mahmoudahmed.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xyz.mahmoudahmed.exception.FileProcessingException;
import xyz.mahmoudahmed.format.FormatDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class FormatDetectionServiceTest {

    @Mock
    private FormatDetector mockDetector1;

    @Mock
    private FormatDetector mockDetector2;

    private FormatDetectionService service;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create service with mocks
        service = new FormatDetectionService(Arrays.asList(mockDetector1, mockDetector2));
    }

    @Test
    void detectFormat_firstDetectorSucceeds_returnsFormat(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create test file - use Files API instead of FileWriter
        Path testFilePath = tempDir.resolve("test.txt");
        Files.writeString(testFilePath, "Test content\n");
        File testFile = testFilePath.toFile();

        // Verify file exists
        assertTrue(testFile.exists(), "Test file should exist");
        assertTrue(testFile.canRead(), "Test file should be readable");

        // Reset mocks to clear any previous interactions
        reset(mockDetector1, mockDetector2);

        // Configure mocks - use lenient stubbing
        lenient().when(mockDetector1.canDetect(any(File.class))).thenReturn(true);
        lenient().when(mockDetector1.detectFormat(any(File.class))).thenReturn("FORMAT1");
        lenient().when(mockDetector2.canDetect(any(File.class))).thenReturn(false);

        // Execute test
        String result = service.detectFormat(testFile);

        // Verify result
        assertEquals("FORMAT1", result);

        // Verify interactions
        verify(mockDetector1, times(1)).canDetect(any(File.class));
        verify(mockDetector1, times(1)).detectFormat(any(File.class));
        verify(mockDetector2, never()).canDetect(any(File.class));
        verify(mockDetector2, never()).detectFormat(any(File.class));
    }

    @Test
    void detectFormat_secondDetectorSucceeds_returnsFormat(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create test file
        Path testFilePath = tempDir.resolve("test.txt");
        Files.writeString(testFilePath, "Test content\n");
        File testFile = testFilePath.toFile();

        // Reset mocks
        reset(mockDetector1, mockDetector2);

        // Configure mocks
        lenient().when(mockDetector1.canDetect(any(File.class))).thenReturn(false);
        lenient().when(mockDetector2.canDetect(any(File.class))).thenReturn(true);
        lenient().when(mockDetector2.detectFormat(any(File.class))).thenReturn("FORMAT2");

        // Execute test
        String result = service.detectFormat(testFile);

        // Verify result
        assertEquals("FORMAT2", result);

        // Verify interactions
        verify(mockDetector1, times(1)).canDetect(any(File.class));
        verify(mockDetector1, never()).detectFormat(any(File.class));
        verify(mockDetector2, times(1)).canDetect(any(File.class));
        verify(mockDetector2, times(1)).detectFormat(any(File.class));
    }

    @Test
    void detectFormat_contentDetection_returnsFormat(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create test file
        Path testFilePath = tempDir.resolve("test.txt");
        Files.writeString(testFilePath, "Test content\n");
        File testFile = testFilePath.toFile();

        // Reset mocks
        reset(mockDetector1, mockDetector2);

        // Configure mocks - no detector matches by extension, but second detects by content
        lenient().when(mockDetector1.canDetect(any(File.class))).thenReturn(false);
        lenient().when(mockDetector2.canDetect(any(File.class))).thenReturn(false);
        lenient().when(mockDetector1.detectFormat(any(File.class))).thenReturn("UNKNOWN");
        lenient().when(mockDetector2.detectFormat(any(File.class))).thenReturn("FORMAT2");

        // Execute test
        String result = service.detectFormat(testFile);

        // Verify result
        assertEquals("FORMAT2", result);

        // Verify interactions - both canDetect and detectFormat should be called for both detectors
        verify(mockDetector1, times(1)).canDetect(any(File.class));
        verify(mockDetector2, times(1)).canDetect(any(File.class));
        verify(mockDetector1, times(1)).detectFormat(any(File.class));
        verify(mockDetector2, times(1)).detectFormat(any(File.class));
    }

    @Test
    void detectFormat_noMatch_returnsUnknown(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create test file
        Path testFilePath = tempDir.resolve("test.txt");
        Files.writeString(testFilePath, "Test content\n");
        File testFile = testFilePath.toFile();

        // Reset mocks
        reset(mockDetector1, mockDetector2);

        // Configure mocks - no detector matches
        lenient().when(mockDetector1.canDetect(any(File.class))).thenReturn(false);
        lenient().when(mockDetector2.canDetect(any(File.class))).thenReturn(false);
        lenient().when(mockDetector1.detectFormat(any(File.class))).thenReturn("UNKNOWN");
        lenient().when(mockDetector2.detectFormat(any(File.class))).thenReturn("UNKNOWN");

        // Execute test
        String result = service.detectFormat(testFile);

        // Verify result
        assertEquals("UNKNOWN", result);

        // Verify interactions
        verify(mockDetector1, times(1)).canDetect(any(File.class));
        verify(mockDetector2, times(1)).canDetect(any(File.class));
        verify(mockDetector1, times(1)).detectFormat(any(File.class));
        verify(mockDetector2, times(1)).detectFormat(any(File.class));
    }

    @Test
    void detectFormat_nullFile_throwsException() {
        // Execute test and verify exception
        assertThrows(FileProcessingException.class, () -> service.detectFormat(null));
    }

    @Test
    void constructor_nullDetectors_throwsException() {
        // Execute test and verify exception
        assertThrows(NullPointerException.class, () -> new FormatDetectionService(null));
    }

    @Test
    void constructor_emptyDetectorList_acceptsEmptyList() {
        // Create service with empty list
        FormatDetectionService emptyService = new FormatDetectionService(Collections.emptyList());

        // Verify service was created
        assertNotNull(emptyService);
    }

    // Let's add a debugging test that prints what's happening
    @Test
    void debuggingTest(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create test file
        Path testFilePath = tempDir.resolve("debug.txt");
        Files.writeString(testFilePath, "Debug content\n");
        File testFile = testFilePath.toFile();

        System.out.println("DEBUG: File exists? " + testFile.exists());
        System.out.println("DEBUG: File path: " + testFile.getAbsolutePath());

        // Create a real implementation for debugging
        FormatDetector debugDetector = new FormatDetector() {
            @Override
            public boolean canDetect(File file) {
                System.out.println("DEBUG: canDetect called with file: " + file.getAbsolutePath());
                return true;
            }

            @Override
            public String detectFormat(File file) throws FileProcessingException {
                System.out.println("DEBUG: detectFormat called with file: " + file.getAbsolutePath());
                return "DEBUG_FORMAT";
            }
        };

        // Create service with real detector
        FormatDetectionService debugService = new FormatDetectionService(Collections.singletonList(debugDetector));

        // Execute test
        String result = debugService.detectFormat(testFile);

        // Print result
        System.out.println("DEBUG: Result: " + result);

        // Basic assertion to make test pass
        assertEquals("DEBUG_FORMAT", result);
    }
}