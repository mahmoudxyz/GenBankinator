package xyz.mahmoudahmed.service;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Service for file operations.
 */
public class FileService {

    /**
     * Reads a file into a string.
     *
     * @param file The file to read
     * @return The file content as a string
     * @throws IOException If an error occurs reading the file
     */
    public String readFileToString(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File does not exist or is null");
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n"); // Always use '\n' for consistency
            }
        }
        return content.toString();
    }

    /**
     * Writes a string to a file.
     *
     * @param content The content to write
     * @param file The file to write to
     * @throws IOException If an error occurs writing to the file
     */
    public void writeStringToFile(String content, File file) throws IOException {
        if (file == null) {
            throw new IOException("Target file cannot be null");
        }

        // Ensure the parent directory exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
        }

        // Normalize line endings to \n before writing
        String normalizedContent = content;
        if (content != null) {
            normalizedContent = content.replace("\r\n", "\n");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            if (normalizedContent != null) {
                writer.write(normalizedContent);
            }
        }
    }
    /**
     * Appends a string to a file.
     *
     * @param content The content to append
     * @param file    The file to append to
     * @throws IOException If an error occurs writing to the file
     */
    public void appendStringToFile(String content, File file) throws IOException {
        if (file == null) {
            throw new IOException("Target file cannot be null");
        }

        // Ensure the parent directory exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            if (content != null) {
                writer.write(content);
            }
        }
    }

    /**
     * Creates a temporary file.
     *
     * @param prefix The prefix for the file name
     * @param suffix The suffix for the file name
     * @return The created temporary file
     * @throws IOException If an error occurs creating the file
     */
    public File createTempFile(String prefix, String suffix) throws IOException {
        if (prefix == null) {
            prefix = "temp";
        }
        if (suffix == null) {
            suffix = ".tmp";
        }
        return File.createTempFile(prefix, suffix);
    }

    /**
     * Checks if a file is empty.
     *
     * @param file The file to check
     * @return true if the file is empty or doesn't exist
     */
    public boolean isFileEmpty(File file) {
        return file == null || !file.exists() || file.length() == 0;
    }
}
