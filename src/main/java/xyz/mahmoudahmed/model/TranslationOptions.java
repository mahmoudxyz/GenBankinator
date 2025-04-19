package xyz.mahmoudahmed.model;

import java.util.List;

/**
 * Options for controlling translation of sequences.
 */
public interface TranslationOptions {
    /**
     * Check if auto-translation is enabled.
     *
     * @return true if auto-translation is enabled
     */
    boolean isAutoTranslate();

    /**
     * Get the genetic code to use for translation.
     *
     * @return The genetic code
     */
    String getGeneticCode();

    /**
     * Get feature types to force translation for.
     *
     * @return List of feature types
     */
    List<String> getForceTranslateFeatureTypes();

    /**
     * Get feature types to skip translation for.
     *
     * @return List of feature types
     */
    List<String> getSkipTranslateFeatureTypes();

    /**
     * Check if transl_table qualifier should be included.
     *
     * @return true if transl_table qualifier should be included
     */
    boolean isIncludeTranslTableQualifier();

    /**
     * Check if codon_start qualifier should be included.
     *
     * @return true if codon_start qualifier should be included
     */
    boolean isIncludeCodonStartQualifier();

    /**
     * Create a builder for TranslationOptions.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultTranslationOptions.Builder();
    }

    /**
     * Builder for TranslationOptions.
     */
    interface Builder {
        /**
         * Set auto-translation.
         *
         * @param autoTranslate true to enable auto-translation
         * @return This builder
         */
        Builder autoTranslate(boolean autoTranslate);

        /**
         * Set the genetic code.
         *
         * @param geneticCode The genetic code
         * @return This builder
         */
        Builder geneticCode(String geneticCode);

        /**
         * Set feature types to force translation for.
         *
         * @param forceTranslateFeatureTypes List of feature types
         * @return This builder
         */
        Builder forceTranslateFeatureTypes(List<String> forceTranslateFeatureTypes);

        /**
         * Set feature types to skip translation for.
         *
         * @param skipTranslateFeatureTypes List of feature types
         * @return This builder
         */
        Builder skipTranslateFeatureTypes(List<String> skipTranslateFeatureTypes);

        /**
         * Set whether to include transl_table qualifier.
         *
         * @param includeTranslTableQualifier true to include transl_table qualifier
         * @return This builder
         */
        Builder includeTranslTableQualifier(boolean includeTranslTableQualifier);

        /**
         * Set whether to include codon_start qualifier.
         *
         * @param includeCodonStartQualifier true to include codon_start qualifier
         * @return This builder
         */
        Builder includeCodonStartQualifier(boolean includeCodonStartQualifier);

        /**
         * Build the TranslationOptions.
         *
         * @return The built TranslationOptions
         */
        TranslationOptions build();
    }
}
