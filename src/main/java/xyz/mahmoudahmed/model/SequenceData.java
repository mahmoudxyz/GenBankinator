package xyz.mahmoudahmed.model;

import java.util.List;

/**
 * Container for sequence data.
 */
public interface SequenceData {
    /**
     * Get the sequences in this data set.
     *
     * @return List of sequences
     */
    List<Sequence> getSequences();

    /**
     * Get a sequence by ID.
     *
     * @param id The sequence ID
     * @return The sequence, or null if not found
     */
    Sequence getSequence(String id);

    /**
     * Get the number of sequences.
     *
     * @return The number of sequences
     */
    int getCount();

    /**
     * Create a builder for SequenceData.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultSequenceData.Builder();
    }

    /**
     * Builder for SequenceData.
     */
    interface Builder {
        /**
         * Add a sequence.
         *
         * @param sequence The sequence to add
         * @return This builder
         */
        Builder addSequence(Sequence sequence);

        /**
         * Add multiple sequences.
         *
         * @param sequences The sequences to add
         * @return This builder
         */
        Builder addSequences(List<Sequence> sequences);

        /**
         * Build the SequenceData.
         *
         * @return The built SequenceData
         */
        SequenceData build();
    }
}