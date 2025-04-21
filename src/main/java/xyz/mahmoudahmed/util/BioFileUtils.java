package xyz.mahmoudahmed.util;

import xyz.mahmoudahmed.config.FormatConfiguration;
import xyz.mahmoudahmed.exception.FileProcessingException;
import xyz.mahmoudahmed.factory.FormatDetectorFactory;
import xyz.mahmoudahmed.format.FastaFormatDetector;
import xyz.mahmoudahmed.format.FormatDetector;
import xyz.mahmoudahmed.service.FileService;
import xyz.mahmoudahmed.service.FormatDetectionService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Facade for bioinformatics file operations.
 */
public class BioFileUtils {
    private final FormatDetectionService formatService;
    private final FileService fileService;
    private final FormatConfiguration config;

    /**
     * Constructor.
     *
     * @param formatService The format detection service
     * @param fileService   The file service
     * @param config        The format configuration
     */
    public BioFileUtils(FormatDetectionService formatService, FileService fileService, FormatConfiguration config) {
        this.formatService = formatService;
        this.fileService = fileService;
        this.config = config;
    }

    /**
     * Detects the format of a file.
     *
     * @param file The file to detect the format of
     * @return The detected format
     * @throws FileProcessingException If an error occurs during detection
     */
    public String detectFormat(File file) throws FileProcessingException {
        return formatService.detectFormat(file);
    }

    /**
     * Checks if a FASTA file likely contains annotation information.
     *
     * @param file The file to check
     * @return true if the file appears to be a FASTA annotation file
     * @throws FileProcessingException If an error occurs checking the file
     */
    public boolean isFastaAnnotationFile(File file) throws FileProcessingException {
        String format = detectFormat(file);
        if (!"FASTA".equals(format)) {
            return false;
        }

        FastaFormatDetector detector = new FastaFormatDetector(config);
        return detector.isFastaAnnotationFile(file);
    }

    /**
     * Reads a file into a string.
     *
     * @param file The file to read
     * @return The file content as a string
     * @throws IOException If an error occurs reading the file
     */
    public String readFileToString(File file) throws IOException {
        return fileService.readFileToString(file);
    }

    /**
     * Writes a string to a file.
     *
     * @param content The content to write
     * @param file    The file to write to
     * @throws IOException If an error occurs writing to the file
     */
    public void writeStringToFile(String content, File file) throws IOException {
        fileService.writeStringToFile(content, file);
    }

    /**
     * Appends a string to a file.
     *
     * @param content The content to append
     * @param file    The file to append to
     * @throws IOException If an error occurs writing to the file
     */
    public void appendStringToFile(String content, File file) throws IOException {
        fileService.appendStringToFile(content, file);
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
        return fileService.createTempFile(prefix, suffix);
    }

    /**
     * Factory method to create a BioFileUtils instance.
     *
     * @return A new BioFileUtils instance
     */
    public static BioFileUtils create() {
        FormatConfiguration config = new FormatConfiguration();
        FormatDetectorFactory factory = new FormatDetectorFactory(config);
        List<FormatDetector> detectors = factory.createDetectors();
        FormatDetectionService formatService = new FormatDetectionService(detectors);
        FileService fileService = new FileService();

        return new BioFileUtils(formatService, fileService, config);
    }
}