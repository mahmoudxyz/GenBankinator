package xyz.mahmoudahmed.model;

import java.util.Map;

/**
 * Options for conversion operations.
 */
public interface ConversionOptions {
    /**
     * Get the organism name.
     *
     * @return The organism name
     */
    String getOrganism();

    /**
     * Get the molecule type.
     *
     * @return The molecule type
     */
    String getMoleculeType();

    /**
     * Get the topology.
     *
     * @return The topology
     */
    String getTopology();

    /**
     * Get the division.
     *
     * @return The division
     */
    String getDivision();

    /**
     * Check if sequences should be merged.
     *
     * @return true if sequences should be merged
     */
    boolean isMergeSequences();

    /**
     * Get the annotation format.
     *
     * @return The annotation format
     */
    String getAnnotationFormat();

    /**
     * Get the translation options.
     *
     * @return The translation options
     */
    TranslationOptions getTranslationOptions();

    /**
     * Get the feature formatting options.
     *
     * @return The feature formatting options
     */
    FeatureFormattingOptions getFeatureFormattingOptions();

    /**
     * Get the output formatting options.
     *
     * @return The output formatting options
     */
    OutputFormattingOptions getOutputFormattingOptions();

    /**
     * Get the custom metadata.
     *
     * @return Map of custom metadata
     */
    Map<String, String> getCustomMetadata();

    /**
     * Get the feature filter options.
     *
     * @return The feature filter options
     */
    FeatureFilterOptions getFilterOptions();

    /**
     * Get the header information.
     *
     * @return The header information
     */
    HeaderInfo getHeaderInfo();

    /**
     * Create a builder for ConversionOptions.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultConversionOptions.Builder();
    }

    /**
     * Builder for ConversionOptions.
     */
    interface Builder {
        /**
         * Copy values from an existing ConversionOptions instance.
         *
         * @param options The options to copy from
         * @return This builder
         */
        Builder from(ConversionOptions options);

        /**
         * Set the organism.
         *
         * @param organism The organism
         * @return This builder
         */
        Builder organism(String organism);

        /**
         * Set the molecule type.
         *
         * @param moleculeType The molecule type
         * @return This builder
         */
        Builder moleculeType(String moleculeType);

        /**
         * Set the topology.
         *
         * @param topology The topology
         * @return This builder
         */
        Builder topology(String topology);

        /**
         * Set the division.
         *
         * @param division The division
         * @return This builder
         */
        Builder division(String division);

        /**
         * Set whether to merge sequences.
         *
         * @param mergeSequences true to merge sequences
         * @return This builder
         */
        Builder mergeSequences(boolean mergeSequences);

        /**
         * Set the annotation format.
         *
         * @param annotationFormat The annotation format
         * @return This builder
         */
        Builder annotationFormat(String annotationFormat);

        /**
         * Set the translation options.
         *
         * @param translationOptions The translation options
         * @return This builder
         */
        Builder translationOptions(TranslationOptions translationOptions);

        /**
         * Set the feature formatting options.
         *
         * @param featureFormattingOptions The feature formatting options
         * @return This builder
         */
        Builder featureFormattingOptions(FeatureFormattingOptions featureFormattingOptions);

        /**
         * Set the output formatting options.
         *
         * @param outputFormattingOptions The output formatting options
         * @return This builder
         */
        Builder outputFormattingOptions(OutputFormattingOptions outputFormattingOptions);

        /**
         * Set the custom metadata.
         *
         * @param customMetadata Map of custom metadata
         * @return This builder
         */
        Builder customMetadata(Map<String, String> customMetadata);

        /**
         * Set the feature filter options.
         *
         * @param filterOptions The feature filter options
         * @return This builder
         */
        Builder filterOptions(FeatureFilterOptions filterOptions);

        /**
         * Set the header information.
         *
         * @param headerInfo The header information
         * @return This builder
         */
        Builder headerInfo(HeaderInfo headerInfo);

        /**
         * Build the ConversionOptions.
         *
         * @return The built ConversionOptions
         */
        ConversionOptions build();
    }
}