package xyz.mahmoudahmed.model;

import java.util.Map;

/**
 * Options for controlling feature formatting.
 */
public interface FeatureFormattingOptions {
    /**
     * Check if feature types should be standardized.
     *
     * @return true if feature types should be standardized
     */
    boolean isStandardizeFeatureTypes();

    /**
     * Check if original IDs should be preserved.
     *
     * @return true if original IDs should be preserved
     */
    boolean isPreserveOriginalIds();

    /**
     * Check if pseudo qualifier should be included for incomplete features.
     *
     * @return true if pseudo qualifier should be included
     */
    boolean isIncludePseudoQualifier();

    /**
     * Get the feature type mapping.
     *
     * @return Map of feature type conversions
     */
    Map<String, String> getFeatureTypeMapping();

    /**
     * Get additional qualifiers to add to specific feature types.
     *
     * @return Map of feature types to additional qualifiers
     */
    Map<String, Map<String, String>> getAdditionalQualifiers();

    /**
     * Create a builder for FeatureFormattingOptions.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultFeatureFormattingOptions.Builder();
    }

    /**
     * Builder for FeatureFormattingOptions.
     */
    interface Builder {
        /**
         * Set whether to standardize feature types.
         *
         * @param standardizeFeatureTypes true to standardize feature types
         * @return This builder
         */
        Builder standardizeFeatureTypes(boolean standardizeFeatureTypes);

        /**
         * Set whether to preserve original IDs.
         *
         * @param preserveOriginalIds true to preserve original IDs
         * @return This builder
         */
        Builder preserveOriginalIds(boolean preserveOriginalIds);

        /**
         * Set whether to include pseudo qualifier.
         *
         * @param includePseudoQualifier true to include pseudo qualifier
         * @return This builder
         */
        Builder includePseudoQualifier(boolean includePseudoQualifier);

        /**
         * Set the feature type mapping.
         *
         * @param featureTypeMapping Map of feature type conversions
         * @return This builder
         */
        Builder featureTypeMapping(Map<String, String> featureTypeMapping);

        /**
         * Set additional qualifiers.
         *
         * @param additionalQualifiers Map of feature types to additional qualifiers
         * @return This builder
         */
        Builder additionalQualifiers(Map<String, Map<String, String>> additionalQualifiers);

        /**
         * Build the FeatureFormattingOptions.
         *
         * @return The built FeatureFormattingOptions
         */
        FeatureFormattingOptions build();
    }
}