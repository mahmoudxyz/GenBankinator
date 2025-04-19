package xyz.mahmoudahmed.model;

import java.util.List;

/**
 * Result of a validation operation.
 */
public interface ValidationResult {
    /**
     * Check if the validation passed.
     *
     * @return true if the validation passed
     */
    boolean isValid();

    /**
     * Get the detected file format.
     *
     * @return The detected format
     */
    String getDetectedFormat();

    /**
     * Get the number of sequences detected.
     *
     * @return The number of sequences
     */
    int getSequenceCount();

    /**
     * Get the number of features detected.
     *
     * @return The number of features
     */
    int getFeatureCount();

    /**
     * Get the validation issues.
     *
     * @return List of validation issues
     */
    List<ValidationIssue> getIssues();

    /**
     * Get a summary of the validation.
     *
     * @return The summary
     */
    String getSummary();

    /**
     * Create a builder for ValidationResult.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultValidationResult.Builder();
    }

    /**
     * Builder for ValidationResult.
     */
    interface Builder {
        /**
         * Set whether the validation passed.
         *
         * @param valid true if the validation passed
         * @return This builder
         */
        Builder valid(boolean valid);

        /**
         * Set the detected format.
         *
         * @param detectedFormat The detected format
         * @return This builder
         */
        Builder detectedFormat(String detectedFormat);

        /**
         * Set the number of sequences.
         *
         * @param sequenceCount The number of sequences
         * @return This builder
         */
        Builder sequenceCount(int sequenceCount);

        /**
         * Set the number of features.
         *
         * @param featureCount The number of features
         * @return This builder
         */
        Builder featureCount(int featureCount);

        /**
         * Set the validation issues.
         *
         * @param issues List of validation issues
         * @return This builder
         */
        Builder issues(List<ValidationIssue> issues);

        /**
         * Add a validation issue.
         *
         * @param issue The validation issue to add
         * @return This builder
         */
        Builder addIssue(ValidationIssue issue);

        /**
         * Set the summary.
         *
         * @param summary The summary
         * @return This builder
         */
        Builder summary(String summary);

        /**
         * Build the ValidationResult.
         *
         * @return The built ValidationResult
         */
        ValidationResult build();
    }
}