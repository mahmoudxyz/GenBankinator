package xyz.mahmoudahmed.util;

import java.io.IOException;

/**
 * Utility for streaming sequence data.
 */
public interface SequenceStreamProvider {
    /**
     * Stream a sequence to a consumer.
     *
     * @param sequenceId The ID of the sequence to stream
     * @param consumer The consumer to process the sequence chunks
     * @throws IOException If an I/O error occurs
     */
    void streamSequence(String sequenceId, SequenceConsumer consumer) throws IOException;

    /**
     * Functional interface for consuming sequence chunks.
     */
    @FunctionalInterface
    interface SequenceConsumer {
        /**
         * Consume a chunk of sequence data.
         *
         * @param chunk The sequence chunk to process
         * @throws IOException If an I/O error occurs
         */
        void consumeChunk(String chunk) throws IOException;
    }
}
