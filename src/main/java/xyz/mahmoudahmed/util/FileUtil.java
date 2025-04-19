package xyz.mahmoudahmed.util;

import xyz.mahmoudahmed.exception.FileProcessingException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for file and format operations.
 */
public class FileUtil {
    // Common file extensions
    private static final Map<String, String> EXTENSION_FORMAT_MAP = new HashMap<>();

    static {
        // Initialize extension to format mapping
        EXTENSION_FORMAT_MAP.put("fa", "FASTA");
        EXTENSION_FORMAT_MAP.put("fasta", "FASTA");
        EXTENSION_FORMAT_MAP.put("fna", "FASTA");
        EXTENSION_FORMAT_MAP.put("faa", "FASTA");
        EXTENSION_FORMAT_MAP.put("ffn", "FASTA");
        EXTENSION_FORMAT_MAP.put("gff", "GFF");
        EXTENSION_FORMAT_MAP.put("gff3", "GFF");
        EXTENSION_FORMAT_MAP.put("gtf", "GTF");
        EXTENSION_FORMAT_MAP.put("bed", "BED");
        EXTENSION_FORMAT_MAP.put("gb", "GENBANK");
        EXTENSION_FORMAT_MAP.put("gbk", "GENBANK");
        EXTENSION_FORMAT_MAP.put("genbank", "GENBANK");
    }

    private FileUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Detect the format of a file based on its content and extension.
     *
     * @param file The file to detect the format of
     * @return The detected format
     * @throws FileProcessingException If an error occurs reading the file
     */
    public static String detectFileFormat(File file) throws FileProcessingException {
        // Check file extension first
        String extension = getFileExtension(file.getName()).toLowerCase();
        String formatByExtension = EXTENSION_FORMAT_MAP.get(extension);

        if (formatByExtension != null) {
            return formatByExtension;
        }

        // Try to detect by content
        return detectFormatByContent(file);
    }

    /**
     * Get the extension of a file.
     *
     * @param fileName The file name
     * @return The file extension
     */
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Detect the format of a file based on its content.
     *
     * @param file The file to detect the format of
     * @return The detected format
     * @throws FileProcessingException If an error occurs reading the file
     */
    private static String detectFormatByContent(File file) throws FileProcessingException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            int fastaHeaderCount = 0;
            boolean potentialFastaAnnotation = false;

            while ((line = reader.readLine()) != null && lineCount < 20) {
                line = line.trim();
                lineCount++;

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith(">")) {
                    fastaHeaderCount++;
                    // Check if it looks like a FASTA annotation header
                    if (line.contains(";") && line.contains("-") &&
                            (line.contains("+;") || line.contains("-;") ||
                                    line.matches(".*[;]\\s*[+\\-]\\s*[;].*"))) {
                        potentialFastaAnnotation = true;
                    }
                }

                if (line.startsWith("##gff-version")) {
                    return "GFF";
                }
                if (line.startsWith("##fileformat=VCF")) {
                    return "VCF";
                }
                if (line.startsWith("browser") || line.startsWith("track") ||
                        (line.split("\\t").length >= 3 && line.matches("^\\S+\\t\\d+\\t\\d+.*"))) {
                    return "BED";
                }
                if (line.startsWith("LOCUS") || (line.contains("FEATURES") && line.contains("Location/Qualifiers"))) {
                    return "GENBANK";
                }
            }

            if (fastaHeaderCount > 0) {
                return "FASTA";
            }

            return "UNKNOWN";

        } catch (IOException e) {
            throw new FileProcessingException("Error reading file: " + file.getName(), e);
        }
    }

    /**
     * Check if a FASTA file likely contains annotation information.
     *
     * @param file The file to check
     * @return true if the file appears to be a FASTA annotation file
     * @throws FileProcessingException If an error occurs reading the file
     */
    public static boolean isFastaAnnotationFile(File file) throws FileProcessingException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int checkedHeaders = 0;

            while ((line = reader.readLine()) != null && checkedHeaders < 5) {
                line = line.trim();

                if (line.startsWith(">")) {
                    checkedHeaders++;
                    // Check for patterns typical in FASTA annotation headers: ID; START-END; STRAND; TYPE
                    if (line.contains(";") &&
                            line.matches(".*[0-9]+-[0-9]+.*") &&
                            (line.contains("+") || line.contains("-")) &&
                            line.split(";").length >= 3) {
                        return true;
                    }
                }
            }

            return false;
        } catch (IOException e) {
            throw new FileProcessingException("Error checking FASTA annotation file: " + file.getName(), e);
        }
    }

    /**
     * Create a temporary file.
     *
     * @param prefix The prefix for the file name
     * @param suffix The suffix for the file name
     * @return The created temporary file
     * @throws IOException If an error occurs creating the file
     */
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }

    /**
     * Read a file into a string.
     *
     * @param file The file to read
     * @return The file content as a string
     * @throws IOException If an error occurs reading the file
     */
    public static String readFileToString(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    /**
     * Write a string to a file.
     *
     * @param content The content to write
     * @param file The file to write to
     * @throws IOException If an error occurs writing to the file
     */
    public static void writeStringToFile(String content, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }
}