package xyz.mahmoudahmed.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
    }

    @Test
    void readFileToString_existingFile_returnsContent(@TempDir Path tempDir) throws IOException {
        // Create a test file with content
        Path filePath = tempDir.resolve("test.txt");
        String expectedContent = "Test content\nLine 2\n";
        Files.write(filePath, expectedContent.getBytes(StandardCharsets.UTF_8));

        // Read the file
        String actualContent = fileService.readFileToString(filePath.toFile());

        // Normalize line endings for comparison
        expectedContent = normalizeLineEndings(expectedContent);
        actualContent = normalizeLineEndings(actualContent);

        // Check the content
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void readFileToString_nonExistentFile_throwsException(@TempDir Path tempDir) {
        File nonExistentFile = tempDir.resolve("nonexistent.txt").toFile();
        assertThrows(IOException.class, () -> fileService.readFileToString(nonExistentFile));
    }

    @Test
    void readFileToString_nullFile_throwsException() {
        assertThrows(IOException.class, () -> fileService.readFileToString(null));
    }

    @Test
    void writeStringToFile_validInput_writesFile(@TempDir Path tempDir) throws IOException {
        // Prepare
        File outputFile = tempDir.resolve("output.txt").toFile();
        String content = "Test content to write\nLine 2\n";

        // Write to file
        fileService.writeStringToFile(content, outputFile);

        // Read back and verify
        String readContent = Files.readString(outputFile.toPath());
        assertEquals(content, readContent);
    }

    @Test
    void writeStringToFile_nullContent_writesEmptyFile(@TempDir Path tempDir) throws IOException {
        // Prepare
        File outputFile = tempDir.resolve("output.txt").toFile();

        // Write null content
        fileService.writeStringToFile(null, outputFile);

        // Check file was created but is empty
        assertTrue(outputFile.exists());
        assertEquals(0, outputFile.length());
    }

    @Test
    void writeStringToFile_nullFile_throwsException() {
        assertThrows(IOException.class, () -> fileService.writeStringToFile("content", null));
    }

    @Test
    void appendStringToFile_existingFile_appendsContent(@TempDir Path tempDir) throws IOException {
        // Create a file with initial content
        File outputFile = tempDir.resolve("output.txt").toFile();
        String initialContent = "Initial content\n";
        Files.write(outputFile.toPath(), initialContent.getBytes(StandardCharsets.UTF_8));

        // Append content
        String appendedContent = "Appended content\n";
        fileService.appendStringToFile(appendedContent, outputFile);

        // Read back and verify
        String readContent = Files.readString(outputFile.toPath());
        assertEquals(initialContent + appendedContent, readContent);
    }

    @Test
    void createTempFile_defaultParameters_createsFile() throws IOException {
        // Create a temp file
        File tempFile = fileService.createTempFile(null, null);

        // Verify
        assertTrue(tempFile.exists());
        assertTrue(tempFile.isFile());
        assertTrue(tempFile.canRead());
        assertTrue(tempFile.canWrite());

        // Clean up
        tempFile.delete();
    }

    @Test
    void isFileEmpty_emptyFile_returnsTrue(@TempDir Path tempDir) throws IOException {
        // Create an empty file
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();

        assertTrue(fileService.isFileEmpty(emptyFile));
    }

    @Test
    void isFileEmpty_nonEmptyFile_returnsFalse(@TempDir Path tempDir) throws IOException {
        // Create a non-empty file
        File nonEmptyFile = tempDir.resolve("nonempty.txt").toFile();
        Files.write(nonEmptyFile.toPath(), "Content".getBytes(StandardCharsets.UTF_8));

        assertFalse(fileService.isFileEmpty(nonEmptyFile));
    }

    @Test
    void isFileEmpty_nonExistentFile_returnsTrue(@TempDir Path tempDir) {
        File nonExistentFile = tempDir.resolve("nonexistent.txt").toFile();
        assertTrue(fileService.isFileEmpty(nonExistentFile));
    }

    @Test
    void isFileEmpty_nullFile_returnsTrue() {
        assertTrue(fileService.isFileEmpty(null));
    }

    // Helper method to normalize line endings for cross-platform tests
    private String normalizeLineEndings(String input) {
        return input.replace("\r\n", "\n");
    }
}
