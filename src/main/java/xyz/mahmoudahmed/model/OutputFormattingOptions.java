package xyz.mahmoudahmed.model;

/**
 * Options for controlling output formatting.
 */
public interface OutputFormattingOptions {
    /**
     * Get the maximum line width for sequence data.
     *
     * @return The sequence line width
     */
    int getSequenceLineWidth();

    /**
     * Check if sequence data should be lowercase.
     *
     * @return true if sequence data should be lowercase
     */
    boolean isLowercaseSequence();

    /**
     * Check if sequence should be included in the GenBank file.
     *
     * @return true if sequence should be included
     */
    boolean isIncludeSequence();

    /**
     * Check if empty lines should be included between features.
     *
     * @return true if empty lines should be included
     */
    boolean isIncludeEmptyLinesBetweenFeatures();

    /**
     * Check if features should be sorted by position.
     *
     * @return true if features should be sorted
     */
    boolean isSortFeaturesByPosition();

    /**
     * Create a builder for OutputFormattingOptions.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultOutputFormattingOptions.Builder();
    }

    /**
     * Builder for OutputFormattingOptions.
     */
    interface Builder {
        /**
         * Set the sequence line width.
         *
         * @param sequenceLineWidth The sequence line width
         * @return This builder
         */
        Builder sequenceLineWidth(int sequenceLineWidth);

        /**
         * Set whether to use lowercase for sequence data.
         *
         * @param lowercaseSequence true to use lowercase
         * @return This builder
         */
        Builder lowercaseSequence(boolean lowercaseSequence);

        /**
         * Set whether to include sequence in the GenBank file.
         *
         * @param includeSequence true to include sequence
         * @return This builder
         */
        Builder includeSequence(boolean includeSequence);

        /**
         * Set whether to include empty lines between features.
         *
         * @param includeEmptyLinesBetweenFeatures true to include empty lines
         * @return This builder
         */
        Builder includeEmptyLinesBetweenFeatures(boolean includeEmptyLinesBetweenFeatures);

        /**
         * Set whether to sort features by position.
         *
         * @param sortFeaturesByPosition true to sort features
         * @return This builder
         */
        Builder sortFeaturesByPosition(boolean sortFeaturesByPosition);

        /**
         * Build the OutputFormattingOptions.
         *
         * @return The built OutputFormattingOptions
         */
        OutputFormattingOptions build();
    }
}

