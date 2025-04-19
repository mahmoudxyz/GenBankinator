package xyz.mahmoudahmed.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a genetic feature annotation.
 */
public interface Annotation {
    /**
     * Get the type of feature (e.g., gene, CDS).
     *
     * @return The feature type
     */
    String getType();

    /**
     * Get the start position (0-based inclusive).
     *
     * @return The start position
     */
    int getStart();

    /**
     * Get the end position (0-based exclusive).
     *
     * @return The end position
     */
    int getEnd();

    /**
     * Get the strand (1 for positive, -1 for negative, 0 for unspecified).
     *
     * @return The strand
     */
    int getStrand();

    /**
     * Get the phase for CDS features (0, 1, 2).
     *
     * @return The phase
     */
    Integer getPhase();

    /**
     * Get the ID of the parent sequence.
     *
     * @return The sequence ID
     */
    String getSequenceId();

    /**
     * Get the unique identifier for this feature.
     *
     * @return The feature ID
     */
    String getFeatureId();

    /**
     * Get additional qualifiers for this feature.
     *
     * @return Map of qualifiers
     */
    Map<String, List<String>> getQualifiers();

    /**
     * Create a builder for Annotation.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultAnnotation.Builder();
    }

    /**
     * Builder for Annotation.
     */
    interface Builder {
        /**
         * Set the feature type.
         *
         * @param type The feature type
         * @return This builder
         */
        Builder type(String type);

        /**
         * Set the start position.
         *
         * @param start The start position
         * @return This builder
         */
        Builder start(int start);

        /**
         * Set the end position.
         *
         * @param end The end position
         * @return This builder
         */
        Builder end(int end);

        /**
         * Set the strand.
         *
         * @param strand The strand
         * @return This builder
         */
        Builder strand(int strand);

        /**
         * Set the phase.
         *
         * @param phase The phase
         * @return This builder
         */
        Builder phase(Integer phase);

        /**
         * Set the sequence ID.
         *
         * @param sequenceId The sequence ID
         * @return This builder
         */
        Builder sequenceId(String sequenceId);

        /**
         * Set the feature ID.
         *
         * @param featureId The feature ID
         * @return This builder
         */
        Builder featureId(String featureId);

        /**
         * Set the qualifiers.
         *
         * @param qualifiers The qualifiers
         * @return This builder
         */
        Builder qualifiers(Map<String, List<String>> qualifiers);

        /**
         * Add a qualifier.
         *
         * @param key The qualifier key
         * @param value The qualifier value
         * @return This builder
         */
        Builder addQualifier(String key, String value);

        /**
         * Add a qualifier with multiple values.
         *
         * @param key The qualifier key
         * @param values The qualifier values
         * @return This builder
         */
        Builder addQualifier(String key, List<String> values);

        /**
         * Build the Annotation.
         *
         * @return The built Annotation
         */
        Annotation build();
    }
}
