package xyz.mahmoudahmed.model;



/**
 * Global options for the GenBank converter.
 */
public interface GenbankOptions {
    /**
     * Get the default organism to use when not specified.
     *
     * @return The default organism
     */
    String getDefaultOrganism();

    /**
     * Get the default molecule type to use when not specified.
     *
     * @return The default molecule type
     */
    String getDefaultMoleculeType();

    /**
     * Get the default topology to use when not specified.
     *
     * @return The default topology
     */
    String getDefaultTopology();

    /**
     * Get the default division to use when not specified.
     *
     * @return The default division
     */
    String getDefaultDivision();

    /**
     * Check if memory-efficient processing is enabled.
     *
     * @return true if memory-efficient processing is enabled
     */
    boolean isMemoryEfficient();

    /**
     * Get the memory threshold for switching to memory-efficient processing.
     *
     * @return The memory threshold in bytes
     */
    long getMemoryThreshold();

    /**
     * Get the temporary directory for file operations.
     *
     * @return The temporary directory path
     */
    String getTempDirectory();

    /**
     * Create a builder for GenbankOptions.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultGenbankOptions.Builder();
    }

    /**
     * Builder for GenbankOptions.
     */
    interface Builder {
        /**
         * Set the default organism.
         *
         * @param organism The default organism
         * @return This builder
         */
        Builder defaultOrganism(String organism);

        /**
         * Set the default molecule type.
         *
         * @param moleculeType The default molecule type
         * @return This builder
         */
        Builder defaultMoleculeType(String moleculeType);

        /**
         * Set the default topology.
         *
         * @param topology The default topology
         * @return This builder
         */
        Builder defaultTopology(String topology);

        /**
         * Set the default division.
         *
         * @param division The default division
         * @return This builder
         */
        Builder defaultDivision(String division);

        /**
         * Enable or disable memory-efficient processing.
         *
         * @param memoryEfficient true to enable memory-efficient processing
         * @return This builder
         */
        Builder memoryEfficient(boolean memoryEfficient);

        /**
         * Set the memory threshold for switching to memory-efficient processing.
         *
         * @param memoryThreshold The memory threshold in bytes
         * @return This builder
         */
        Builder memoryThreshold(long memoryThreshold);

        /**
         * Set the temporary directory for file operations.
         *
         * @param tempDirectory The temporary directory path
         * @return This builder
         */
        Builder tempDirectory(String tempDirectory);

        /**
         * Build the GenbankOptions.
         *
         * @return The built GenbankOptions
         */
        GenbankOptions build();
    }
}
