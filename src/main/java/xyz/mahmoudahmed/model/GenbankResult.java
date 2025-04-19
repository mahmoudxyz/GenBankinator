package xyz.mahmoudahmed.model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * Result of a GenBank conversion operation.
 */
public interface GenbankResult {
    /**
     * Get the GenBank data as a byte array.
     *
     * @return The GenBank data
     */
    byte[] getGenbankData();

    /**
     * Write the GenBank data to a file.
     *
     * @param file The file to write to
     * @throws IOException If an I/O error occurs
     */
    void writeToFile(File file) throws IOException;

    /**
     * Write the GenBank data to an output stream.
     *
     * @param outputStream The output stream to write to
     * @throws IOException If an I/O error occurs
     */
    void writeToStream(OutputStream outputStream) throws IOException;

    /**
     * Get the number of sequences processed.
     *
     * @return The number of sequences
     */
    int getSequenceCount();

    /**
     * Get the number of features processed.
     *
     * @return The number of features
     */
    int getFeatureCount();

    /**
     * Get the timestamp of the conversion.
     *
     * @return The timestamp
     */
    LocalDateTime getTimestamp();

    /**
     * Create a builder for GenbankResult.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultGenbankResult.Builder();
    }

    /**
     * Builder for GenbankResult.
     */
    interface Builder {
        /**
         * Set the GenBank data.
         *
         * @param genbankData The GenBank data
         * @return This builder
         */
        Builder genbankData(byte[] genbankData);

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
         * Set the timestamp.
         *
         * @param timestamp The timestamp
         * @return This builder
         */
        Builder timestamp(LocalDateTime timestamp);

        /**
         * Build the GenbankResult.
         *
         * @return The built GenbankResult
         */
        GenbankResult build();
    }
}
