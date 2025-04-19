package xyz.mahmoudahmed.parsers;

import xyz.mahmoudahmed.api.SequenceParser;
import xyz.mahmoudahmed.exception.InvalidFileFormatException;
import xyz.mahmoudahmed.model.Sequence;
import xyz.mahmoudahmed.model.SequenceData;
import xyz.mahmoudahmed.util.FileUtil;
import xyz.mahmoudahmed.util.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of a sequence parser that supports FASTA format with 1-based indexing
 * for GenBank format compatibility.
 */
public class NCBICompatibleSequenceParser implements SequenceParser {
    private static final Pattern FASTA_HEADER_PATTERN = Pattern.compile(">(.*)");

    @Override
    public boolean supportsFormat(String format) {
        return "FASTA".equalsIgnoreCase(format) || "FA".equalsIgnoreCase(format) ||
                "FNA".equalsIgnoreCase(format) || "FAA".equalsIgnoreCase(format);
    }

    @Override
    public SequenceData parse(File file) throws IOException {
        List<Sequence> sequences = new ArrayList<>();
        String format = FileUtil.detectFileFormat(file);

        if ("FASTA".equalsIgnoreCase(format)) {
            sequences = parseFasta(file);
        } else {
            throw new InvalidFileFormatException("Unsupported format: " + format, format);
        }

        return SequenceData.builder()
                .addSequences(sequences)
                .build();
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
        String format = FileUtil.detectFileFormat(file);

        if ("FASTA".equalsIgnoreCase(format)) {
            sequences = parseFastaMetadataOnly(file);
        } else {
            throw new InvalidFileFormatException("Unsupported format: " + format, format);
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
                    String sequence = sequenceBuilder.toString();

                    Sequence seq = Sequence.builder()
                            .id(id)
                            .name(StringUtil.truncate(id, 16))
                            .description(description)
                            .sequence(sequence)
                            .moleculeType("DNA")
                            .topology("circular") // Changed to circular for mitochondrial genomes
                            .organism("Unknown organism")
                            .build();
                    sequences.add(seq);
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
            String sequence = sequenceBuilder.toString();

            Sequence seq = Sequence.builder()
                    .id(id)
                    .name(StringUtil.truncate(id, 16))
                    .description(description)
                    .sequence(sequence)
                    .moleculeType("DNA")
                    .topology("circular") // Changed to circular for mitochondrial genomes
                    .organism("Unknown organism")
                    .build();
            sequences.add(seq);
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
                                .topology("circular") // Changed to circular for mitochondrial genomes
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
                        .topology("circular") // Changed to circular for mitochondrial genomes
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