package xyz.mahmoudahmed.model;

import java.util.List;

/**
 * Options for filtering features.
 */
public interface FeatureFilterOptions {
    /**
     * Get feature types to include.
     *
     * @return List of feature types to include
     */
    List<String> getIncludeFeatureTypes();

    /**
     * Get feature types to exclude.
     *
     * @return List of feature types to exclude
     */
    List<String> getExcludeFeatureTypes();

    /**
     * Get qualifiers to include.
     *
     * @return List of qualifiers to include
     */
    List<String> getIncludeQualifiers();

    /**
     * Get qualifiers to exclude.
     *
     * @return List of qualifiers to exclude
     */
    List<String> getExcludeQualifiers();

    /**
     * Get minimum feature length.
     *
     * @return The minimum feature length
     */
    Integer getMinFeatureLength();

    /**
     * Get maximum feature length.
     *
     * @return The maximum feature length
     */
    Integer getMaxFeatureLength();

    /**
     * Create a builder for FeatureFilterOptions.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultFeatureFilterOptions.Builder();
    }

    /**
     * Builder for FeatureFilterOptions.
     */
    interface Builder {
        /**
         * Set feature types to include.
         *
         * @param includeFeatureTypes List of feature types to include
         * @return This builder
         */
        Builder includeFeatureTypes(List<String> includeFeatureTypes);

        /**
         * Set feature types to exclude.
         *
         * @param excludeFeatureTypes List of feature types to exclude
         * @return This builder
         */
        Builder excludeFeatureTypes(List<String> excludeFeatureTypes);

        /**
         * Set qualifiers to include.
         *
         * @param includeQualifiers List of qualifiers to include
         * @return This builder
         */
        Builder includeQualifiers(List<String> includeQualifiers);

        /**
         * Set qualifiers to exclude.
         *
         * @param excludeQualifiers List of qualifiers to exclude
         * @return This builder
         */
        Builder excludeQualifiers(List<String> excludeQualifiers);

        /**
         * Set minimum feature length.
         *
         * @param minFeatureLength The minimum feature length
         * @return This builder
         */
        Builder minFeatureLength(Integer minFeatureLength);

        /**
         * Set maximum feature length.
         *
         * @param maxFeatureLength The maximum feature length
         * @return This builder
         */
        Builder maxFeatureLength(Integer maxFeatureLength);

        /**
         * Build the FeatureFilterOptions.
         *
         * @return The built FeatureFilterOptions
         */
        FeatureFilterOptions build();
    }
}