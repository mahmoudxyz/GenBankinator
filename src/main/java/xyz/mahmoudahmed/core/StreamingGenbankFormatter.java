package xyz.mahmoudahmed.core;

import xyz.mahmoudahmed.model.*;
import xyz.mahmoudahmed.util.SequenceStreamProvider;
import xyz.mahmoudahmed.util.StringUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Streaming implementation of GenbankFormatter for large files.
 */
public class StreamingGenbankFormatter {
    private final SequenceStreamProvider streamProvider;

    public StreamingGenbankFormatter(SequenceStreamProvider streamProvider) {
        this.streamProvider = streamProvider;
    }

    /**
     * Format sequences and annotations to GenBank format and write to a stream.
     */
    public void formatToStream(SequenceData sequenceData, AnnotationData annotationData,
                               OutputStream outputStream, ConversionOptions options) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            List<Sequence> sequences = sequenceData.getSequences();

            for (int i = 0; i < sequences.size(); i++) {
                Sequence sequence = sequences.get(i);

                // Write the GenBank header
                writeGenbankHeader(writer, sequence, options);

                // Write the features
                writeFeatures(writer, sequence, annotationData.getAnnotationsForSequence(sequence.getId()), options);

                // Write the sequence data
                writeSequenceData(writer, sequence, options);

                // Write ending delimiter
                writer.write("//");
                writer.newLine();

                // Add separator between records, except for the last one
                if (i < sequences.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Write the GenBank header.
     */
    private void writeGenbankHeader(BufferedWriter writer, Sequence sequence, ConversionOptions options) throws IOException {
        // Write LOCUS line
        String locusLine = formatLocus(sequence, options);
        writer.write(locusLine);
        writer.newLine();

        // Write DEFINITION line
        String definition = sequence.getDescription();
        if (definition == null || definition.isEmpty()) {
            definition = sequence.getId();
        }
        writeMultiline(writer, "DEFINITION", definition, 12);

        // Write ACCESSION line
        writer.write("ACCESSION   " + sequence.getId());
        writer.newLine();

        // Write VERSION line
        String version = (sequence.getHeaderInfo() != null && sequence.getHeaderInfo().getVersion() != null &&
                !sequence.getHeaderInfo().getVersion().isEmpty()) ?
                sequence.getHeaderInfo().getVersion() :
                sequence.getId() + ".1";
        writer.write("VERSION     " + version);
        writer.newLine();

        // Write KEYWORDS line
        String keywords = (sequence.getHeaderInfo() != null && sequence.getHeaderInfo().getKeywords() != null &&
                !sequence.getHeaderInfo().getKeywords().isEmpty()) ?
                sequence.getHeaderInfo().getKeywords() :
                ".";
        writer.write("KEYWORDS    " + keywords);
        writer.newLine();

        // Write SOURCE line
        String organism = sequence.getOrganism();
        if (organism == null || organism.isEmpty()) {
            organism = "Unknown organism";
        }
        writeMultiline(writer, "SOURCE", organism, 12);

        // Write ORGANISM line
        writer.write("  ORGANISM  " + organism);
        writer.newLine();

        // Write taxonomy if available
        if (sequence.getTaxonomy() != null && !sequence.getTaxonomy().isEmpty()) {
            String taxonomy = String.join("; ", sequence.getTaxonomy()) + ".";
            writeMultiline(writer, "", taxonomy, 12);
        } else {
            writer.write("            Unclassified.");
            writer.newLine();
        }
    }

    /**
     * Write the features section.
     */
    private void writeFeatures(BufferedWriter writer, Sequence sequence, List<Annotation> annotations,
                               ConversionOptions options) throws IOException {
        // Write FEATURES header
        writer.write("FEATURES             Location/Qualifiers");
        writer.newLine();

        // Write source feature
        writer.write("     source          1.." + sequence.getLength());
        writer.newLine();
        writer.write("                     /organism=\"" + sequence.getOrganism() + "\"");
        writer.newLine();
        writer.write("                     /mol_type=\"" + sequence.getMoleculeType() + "\"");
        writer.newLine();

        // Write other features
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                writeFeature(writer, annotation, options);

                // Add empty line between features if enabled
                if (options.getOutputFormattingOptions().isIncludeEmptyLinesBetweenFeatures()) {
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Write a feature entry.
     */
    private void writeFeature(BufferedWriter writer, Annotation annotation, ConversionOptions options) throws IOException {
        // Format feature type
        String type = annotation.getType();
        String paddedType = StringUtil.rightPad(type, 16);

        // Format location
        String location = formatLocation(annotation);

        // Write feature line
        writer.write("     " + paddedType + location);
        writer.newLine();

        // Write qualifiers
        Map<String, List<String>> qualifiers = annotation.getQualifiers();
        if (qualifiers != null) {
            for (Map.Entry<String, List<String>> entry : qualifiers.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                // Skip internal qualifiers (ID)
                if (key.equals("ID") || key.equals("Name")) {
                    continue;
                }

                for (String value : values) {
                    if (value == null || value.isEmpty()) {
                        writer.write("                     /" + key);
                    } else if (isNumericQualifier(key)) {
                        writer.write("                     /" + key + "=" + value);
                    } else {
                        // Escape quotes in value
                        value = value.replace("\"", "\\\"");
                        writer.write("                     /" + key + "=\"" + value + "\"");
                    }
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Write sequence data using streaming.
     */
    private void writeSequenceData(BufferedWriter writer, Sequence sequence, ConversionOptions options) throws IOException {
        // Write ORIGIN header
        writer.write("ORIGIN");
        writer.newLine();

        // Only stream sequence if includeSequence is enabled
        if (options.getOutputFormattingOptions().isIncludeSequence()) {
            // Set up for sequence streaming
            final long[] position = {1}; // 1-based for GenBank
            final int sequenceLineWidth = options.getOutputFormattingOptions().getSequenceLineWidth() > 0 ?
                    options.getOutputFormattingOptions().getSequenceLineWidth() : 60;
            final boolean lowercase = options.getOutputFormattingOptions().isLowercaseSequence();

            // Use a streamer to process the sequence in chunks
            SequenceStreamer streamer = new SequenceStreamer(writer, position[0], sequenceLineWidth, lowercase);

            // If we have the sequence in memory, use it directly
            if (sequence.getSequence() != null) {
                streamer.processChunk(sequence.getSequence());
                streamer.flush();
            } else {
                // Otherwise stream it from the provider
                streamProvider.streamSequence(sequence.getId(), chunk -> {
                    streamer.processChunk(chunk);
                    position[0] += chunk.length();
                });

                streamer.flush();
            }
        }
    }

    /**
     * Format the LOCUS line.
     */
    private String formatLocus(Sequence sequence, ConversionOptions options) {
        String name = StringUtil.truncate(sequence.getName(), 16);
        name = StringUtil.rightPad(name, 16);

        long length = sequence.getLength();
        String lengthStr = StringUtil.leftPad(String.valueOf(length), 11);

        String moleculeType = sequence.getMoleculeType();
        if (moleculeType == null || moleculeType.isEmpty()) {
            moleculeType = "DNA";
        }
        moleculeType = StringUtil.rightPad(moleculeType, 6);

        String topology = sequence.getTopology();
        if (topology == null || topology.isEmpty()) {
            topology = "linear";
        }
        String formattedTopology = "circular".equals(topology.toLowerCase()) ? "circular " : "linear   ";

        String division = "VRT"; // Default for mitochondrial genomes

        return String.format("LOCUS       %s%s bp    %s%s%s%s",
                name, lengthStr, moleculeType, formattedTopology, division, "");
    }

    /**
     * Format a feature location string.
     */
    private String formatLocation(Annotation annotation) {
        StringBuilder location = new StringBuilder();

        // Handle strand
        if (annotation.getStrand() < 0) {
            location.append("complement(");
        }

        // Adjust for 1-based coordinates in GenBank
        int start = annotation.getStart() + 1;
        int end = annotation.getEnd();

        // Simple location
        location.append(start).append("..").append(end);

        // Close complement if needed
        if (annotation.getStrand() < 0) {
            location.append(")");
        }

        return location.toString();
    }

    /**
     * Write multiline fields, properly indented.
     */
    private void writeMultiline(BufferedWriter writer, String key, String value, int keyWidth) throws IOException {
        if (value == null || value.isEmpty()) {
            return;
        }

        String paddedKey = StringUtil.rightPad(key, keyWidth);
        String firstLine = paddedKey + value;

        // If it fits on one line
        if (firstLine.length() <= 80) {
            writer.write(firstLine);
            writer.newLine();
            return;
        }

        // First line with key
        int firstLineContentWidth = 80 - keyWidth;
        writer.write(paddedKey + value.substring(0, firstLineContentWidth));
        writer.newLine();

        // Remaining lines
        String remainingContent = value.substring(firstLineContentWidth);
        String indent = StringUtil.repeat(" ", keyWidth);

        for (int i = 0; i < remainingContent.length(); i += (80 - keyWidth)) {
            int endIndex = Math.min(i + (80 - keyWidth), remainingContent.length());
            writer.write(indent + remainingContent.substring(i, endIndex));
            writer.newLine();
        }
    }

    /**
     * Check if a qualifier should be formatted without quotes.
     */
    private boolean isNumericQualifier(String qualifier) {
        return List.of("codon_start", "transl_table", "codon", "number", "score").contains(qualifier);
    }

    /**
     * Helper class for sequence streaming.
     */
    private static class SequenceStreamer {
        private final BufferedWriter writer;
        private final StringBuilder buffer;
        private final int lineWidth;
        private final boolean lowercase;
        private long currentPosition;

        public SequenceStreamer(BufferedWriter writer, long startPosition, int lineWidth, boolean lowercase) {
            this.writer = writer;
            this.buffer = new StringBuilder(lineWidth * 2); // Buffer size twice the line width
            this.lineWidth = lineWidth;
            this.lowercase = lowercase;
            this.currentPosition = startPosition;
        }

        /**
         * Process a chunk of sequence data.
         */
        public void processChunk(String chunk) throws IOException {
            // Add the chunk to our buffer
            buffer.append(chunk);

            // Process complete lines from the buffer
            processCompleteLines();
        }

        /**
         * Process as many complete lines as possible from the buffer.
         */
        private void processCompleteLines() throws IOException {
            while (buffer.length() >= lineWidth) {
                // Extract a complete line
                String line = buffer.substring(0, lineWidth);
                buffer.delete(0, lineWidth);

                // Write the line in GenBank format
                writeFormattedLine(line);

                // Update position for next line
                currentPosition += lineWidth;
            }
        }

        /**
         * Write any remaining data in the buffer.
         */
        public void flush() throws IOException {
            if (buffer.length() > 0) {
                writeFormattedLine(buffer.toString());
            }
        }

        /**
         * Write a single formatted line of sequence data.
         */
        private void writeFormattedLine(String line) throws IOException {
            // Line number
            String lineNumber = StringUtil.leftPad(String.valueOf(currentPosition), 9);
            writer.write(lineNumber + " ");

            // Always use lowercase for NCBI compatibility
            line = line.toLowerCase();

            // Write in groups of 10 bases
            for (int j = 0; j < line.length(); j += 10) {
                int groupEnd = Math.min(j + 10, line.length());
                writer.write(line.substring(j, groupEnd) + " ");
            }

            writer.newLine();
        }
    }
}