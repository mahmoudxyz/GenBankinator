package xyz.mahmoudahmed.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory-efficient file-based sequence stream provider.
 */
public class FileSequenceStreamProvider implements SequenceStreamProvider {
    private final File file;
    private final Map<String, SequencePosition> sequencePositions = new HashMap<>();

    // Buffer sizes for efficient memory usage
    private static final int INDEX_BUFFER_SIZE = 64 * 1024; // 64KB for indexing
    private static final int READER_BUFFER_SIZE = 256 * 1024; // 256KB for reading
    private static final int CHUNK_BUFFER_SIZE = 128 * 1024; // 128KB chunks for processing

    /**
     * Stores the position and length of a sequence in the file.
     */
    private static class SequencePosition {
        private final long headerStart;
        private long dataStart = -1;
        private long nextHeaderPos = -1;

        public SequencePosition(long headerStart) {
            this.headerStart = headerStart;
        }

        public void setDataStart(long dataStart) {
            this.dataStart = dataStart;
        }

        public void setNextHeaderPos(long nextHeaderPos) {
            this.nextHeaderPos = nextHeaderPos;
        }
    }

    /**
     * Create a new stream provider for the given file.
     *
     * @param file The file to stream from
     * @throws IOException If an error occurs indexing the file
     */
    public FileSequenceStreamProvider(File file) throws IOException {
        this.file = file;
        indexSequencePositions();
    }

    @Override
    public void streamSequence(String sequenceId, SequenceConsumer consumer) throws IOException {
        SequencePosition position = sequencePositions.get(sequenceId);
        if (position == null) {
            throw new IllegalArgumentException("Sequence ID not found: " + sequenceId);
        }

        try (RandomAccessFile randomAccess = new RandomAccessFile(file, "r")) {
            // Jump to the start of the sequence data
            randomAccess.seek(position.dataStart);

            // Skip the header line
            randomAccess.readLine();

            // Read the sequence data in chunks
            StringBuilder sequenceChunk = new StringBuilder(CHUNK_BUFFER_SIZE);
            String line;

            // Use a buffered reader for line-by-line processing
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(randomAccess.getFD()), READER_BUFFER_SIZE)) {

                while ((line = reader.readLine()) != null) {
                    // If we hit another header, we're done with this sequence
                    if (line.startsWith(">")) {
                        break;
                    }

                    // Add the trimmed line to our sequence chunk
                    sequenceChunk.append(line.trim());

                    // When we've accumulated enough data, send a chunk to the consumer
                    if (sequenceChunk.length() >= CHUNK_BUFFER_SIZE) {
                        consumer.consumeChunk(sequenceChunk.toString());
                        sequenceChunk.setLength(0); // Clear the buffer
                    }
                }

                // Send any remaining data
                if (sequenceChunk.length() > 0) {
                    consumer.consumeChunk(sequenceChunk.toString());
                }
            }
        }
    }

    /**
     * Index the positions of all sequences in the file.
     */
    private void indexSequencePositions() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file), INDEX_BUFFER_SIZE)) {
            String line;
            long position = 0;
            String currentId = null;
            SequencePosition currentPosition = null;

            while ((line = reader.readLine()) != null) {
                // Update position before processing the line
                long lineLength = line.length() + System.lineSeparator().length();

                if (line.startsWith(">")) {
                    // If we have a previous sequence, set its end position
                    if (currentId != null && currentPosition != null) {
                        currentPosition.setNextHeaderPos(position);
                    }

                    // Extract ID from header
                    String header = line.substring(1);
                    currentId = parseId(header);

                    // Record the start position
                    currentPosition = new SequencePosition(position);
                    currentPosition.setDataStart(position);
                    sequencePositions.put(currentId, currentPosition);
                }

                // Update position for next line
                position += lineLength;
            }

            // Set end position for the last sequence
            if (currentId != null && currentPosition != null) {
                currentPosition.setNextHeaderPos(position);
            }
        }
    }

    /**
     * Parse the ID from a FASTA header.
     *
     * @param header The FASTA header
     * @return The parsed ID
     */
    private String parseId(String header) {
        // Split on first whitespace - the part before is usually the ID
        int spaceIdx = header.indexOf(' ');
        if (spaceIdx > 0) {
            return header.substring(0, spaceIdx).trim();
        }

        // No space found, use the whole header as ID
        return header.trim();
    }
}