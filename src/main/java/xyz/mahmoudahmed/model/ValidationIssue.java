package xyz.mahmoudahmed.model;

/**
 * Represents a validation issue.
 */
public interface ValidationIssue {
    /**
     * Get the type of issue (ERROR, WARNING).
     *
     * @return The issue type
     */
    String getType();

    /**
     * Get the issue message.
     *
     * @return The message
     */
    String getMessage();

    /**
     * Get the location in the file (line number, etc.).
     *
     * @return The location
     */
    String getLocation();

    /**
     * Create a builder for ValidationIssue.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultValidationIssue.Builder();
    }

    /**
     * Builder for ValidationIssue.
     */
    interface Builder {
        /**
         * Set the issue type.
         *
         * @param type The issue type
         * @return This builder
         */
        Builder type(String type);

        /**
         * Set the issue message.
         *
         * @param message The message
         * @return This builder
         */
        Builder message(String message);

        /**
         * Set the location.
         *
         * @param location The location
         * @return This builder
         */
        Builder location(String location);

        /**
         * Build the ValidationIssue.
         *
         * @return The built ValidationIssue
         */
        ValidationIssue build();
    }
}
