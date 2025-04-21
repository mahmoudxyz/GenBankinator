package xyz.mahmoudahmed.parsers;

import xyz.mahmoudahmed.exception.FileProcessingException;
import xyz.mahmoudahmed.exception.InvalidFileFormatException;
import xyz.mahmoudahmed.model.Sequence;
import xyz.mahmoudahmed.model.SequenceData;
import xyz.mahmoudahmed.service.FormatDetectionService;
import xyz.mahmoudahmed.util.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of a sequence parser that supports FASTA format.
 */
public class DefaultSequenceParser implements SequenceParser {
    private static final Pattern FASTA_HEADER_PATTERN = Pattern.compile(">(.*)");

    private FormatDetectionService formatDetectionService;

    /**
     * Default constructor
     */
    public DefaultSequenceParser() {
        // Default constructor with no format detection service
    }

    /**
     * Constructor with format detection service
     *
     * @param formatDetectionService The format detection service to use
     */
    public DefaultSequenceParser(FormatDetectionService formatDetectionService) {
        this.formatDetectionService = formatDetectionService;
    }

    /**
     * Set the format detection service
     *
     * @param formatDetectionService The format detection service to use
     */
    public void setFormatDetectionService(FormatDetectionService formatDetectionService) {
        this.formatDetectionService = formatDetectionService;
    }

    @Override
    public boolean supportsFormat(String format) {
        return "FASTA".equalsIgnoreCase(format) || "FA".equalsIgnoreCase(format) ||
                "FNA".equalsIgnoreCase(format) || "FAA".equalsIgnoreCase(format);
    }

    @Override
    public SequenceData parse(File file) throws IOException {
        List<Sequence> sequences = new ArrayList<>();

        // Check file extension first - if it's a recognized FASTA extension, accept it even if empty
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".fasta") || fileName.endsWith(".fa") ||
                fileName.endsWith(".fna") || fileName.endsWith(".faa")) {

            // File has FASTA extension - parse as FASTA
            sequences = parseFasta(file);

        } else {
            // For non-FASTA extensions, rely on format detection
            String format;

            try {
                // Use format detection service if available
                if (formatDetectionService != null) {
                    format = formatDetectionService.detectFormat(file);
                } else {
                    // Fall back to extension-based detection
                    format = detectFormatByExtension(file);
                }

                if ("FASTA".equalsIgnoreCase(format)) {
                    sequences = parseFasta(file);
                } else {
                    throw new InvalidFileFormatException("Unsupported format: " + format, format);
                }
            } catch (FileProcessingException e) {
                throw new IOException("Error detecting file format: " + e.getMessage(), e);
            }
        }

        return SequenceData.builder()
                .addSequences(sequences)
                .build();
    }

    /**
     * Detect format by file extension.
     * This is a fallback method when no format detection service is available
     */
    private String detectFormatByExtension(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".fa") || name.endsWith(".fasta") ||
                name.endsWith(".fna") || name.endsWith(".faa")) {
            return "FASTA";
        }

        // Try to detect by checking first few lines
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith(">")) {
                    return "FASTA";
                }
                break;
            }
        } catch (IOException e) {
            // Fall back to unknown on error
        }

        return "UNKNOWN";
    }

    @Override
    public SequenceData parse(InputStream inputStream) throws IOException {
        List<Sequence> sequences = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            sequences = parseFastaFromReader(reader);
        }

        return SequenceData.builder()
                .addSequences(sequences)
                .build();
    }

    @Override
    public SequenceData parseMetadataOnly(File file) throws IOException {
        List<Sequence> sequences = new ArrayList<>();

        // Check file extension first, similar to parse method
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".fasta") || fileName.endsWith(".fa") ||
                fileName.endsWith(".fna") || fileName.endsWith(".faa")) {

            // File has FASTA extension - parse metadata only as FASTA
            sequences = parseFastaMetadataOnly(file);

        } else {
            // For non-FASTA extensions, rely on format detection
            String format;

            try {
                // Use format detection service if available
                if (formatDetectionService != null) {
                    format = formatDetectionService.detectFormat(file);
                } else {
                    // Fall back to extension-based detection
                    format = detectFormatByExtension(file);
                }

                if ("FASTA".equalsIgnoreCase(format)) {
                    sequences = parseFastaMetadataOnly(file);
                } else {
                    throw new InvalidFileFormatException("Unsupported format: " + format, format);
                }
            } catch (FileProcessingException e) {
                throw new IOException("Error detecting file format: " + e.getMessage(), e);
            }
        }

        return SequenceData.builder()
                .addSequences(sequences)
                .build();
    }

    /**
     * Parse a FASTA file into a list of sequences.
     */
    private List<Sequence> parseFasta(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return parseFastaFromReader(reader);
        }
    }

    /**
     * Parse FASTA format from a reader.
     */
    private List<Sequence> parseFastaFromReader(BufferedReader reader) throws IOException {
        List<Sequence> sequences = new ArrayList<>();
        String line;
        StringBuilder sequenceBuilder = null;
        String header = null;
        String id = null;
        String description = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith(">")) {
                // If we have a sequence in progress, add it to the list
                if (sequenceBuilder != null && id != null) {
                    Sequence sequence = Sequence.builder()
                            .id(id)
                            .name(StringUtil.truncate(id, 16))
                            .description(description)
                            .sequence(sequenceBuilder.toString())
                            .moleculeType("DNA")
                            .topology("linear")
                            .organism("Unknown organism")
                            .build();
                    sequences.add(sequence);
                }

                // Start a new sequence
                header = line.substring(1);
                id = parseId(header);
                description = header;
                sequenceBuilder = new StringBuilder();
            } else if (sequenceBuilder != null) {
                // Append sequence data
                sequenceBuilder.append(line);
            }
        }

        // Add the last sequence if there is one
        if (sequenceBuilder != null && id != null) {
            Sequence sequence = Sequence.builder()
                    .id(id)
                    .name(StringUtil.truncate(id, 16))
                    .description(description)
                    .sequence(sequenceBuilder.toString())
                    .moleculeType("DNA")
                    .topology("linear")
                    .organism("Unknown organism")
                    .build();
            sequences.add(sequence);
        }

        return sequences;
    }

    /**
     * Parse only metadata from a FASTA file, without loading full sequences.
     */
    private List<Sequence> parseFastaMetadataOnly(File file) throws IOException {
        List<Sequence> sequences = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            long sequenceLength = 0;
            String header = null;
            String id = null;
            String description = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith(">")) {
                    // If we have a sequence in progress, add it to the list
                    if (id != null) {
                        Sequence sequence = Sequence.builder()
                                .id(id)
                                .name(StringUtil.truncate(id, 16))
                                .description(description)
                                .length(sequenceLength)
                                .moleculeType("DNA")
                                .topology("linear")
                                .organism("Unknown organism")
                                .build();
                        sequences.add(sequence);
                    }

                    // Start a new sequence
                    header = line.substring(1);
                    id = parseId(header);
                    description = header;
                    sequenceLength = 0;
                } else {
                    // Count sequence length without storing the actual sequence
                    sequenceLength += line.length();
                }
            }

            // Add the last sequence if there is one
            if (id != null) {
                Sequence sequence = Sequence.builder()
                        .id(id)
                        .name(StringUtil.truncate(id, 16))
                        .description(description)
                        .length(sequenceLength)
                        .moleculeType("DNA")
                        .topology("linear")
                        .organism("Unknown organism")
                        .build();
                sequences.add(sequence);
            }
        }

        return sequences;
    }

    /**
     * Parse the ID from a FASTA header.
     */
    private String parseId(String header) {
        if (header == null || header.isEmpty()) {
            return "unknown";
        }

        // Split on first whitespace
        int spaceIndex = header.indexOf(' ');
        if (spaceIndex > 0) {
            return header.substring(0, spaceIndex).trim();
        }

        // No space found, use the whole header
        return header.trim();
    }
}