package xyz.mahmoudahmed.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents a biological sequence.
 */
public interface Sequence {
    /**
     * Get the unique identifier for this sequence.
     *
     * @return The sequence ID
     */
    String getId();

    /**
     * Get the display name for this sequence (limited to 16 characters in GenBank).
     *
     * @return The sequence name
     */
    String getName();

    /**
     * Get the full description of this sequence.
     *
     * @return The sequence description
     */
    String getDescription();

    /**
     * Get the actual sequence data.
     *
     * @return The sequence data
     */
    String getSequence();

    /**
     * Get the length of the sequence.
     *
     * @return The sequence length
     */
    long getLength();

    /**
     * Get the type of molecule (DNA, RNA, protein).
     *
     * @return The molecule type
     */
    String getMoleculeType();

    /**
     * Get the topology (linear, circular).
     *
     * @return The topology
     */
    String getTopology();

    /**
     * Get the division code (e.g., PRI, ROD, MAM).
     *
     * @return The division code
     */
    String getDivision();

    /**
     * Get the taxonomy information.
     *
     * @return The taxonomy list
     */
    List<String> getTaxonomy();

    /**
     * Get the source organism.
     *
     * @return The source organism
     */
    String getOrganism();

    /**
     * Get general annotations.
     *
     * @return Map of annotations
     */
    Map<String, Object> getAnnotations();

    /**
     * Get the creation date.
     *
     * @return The creation date
     */
    Date getDate();

    /**
     * Get the detailed header information.
     *
     * @return The header information
     */
    HeaderInfo getHeaderInfo();

    /**
     * Create a builder for Sequence.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultSequence.Builder();
    }

    /**
     * Builder for Sequence.
     */
    interface Builder {
        /**
         * Set the ID.
         *
         * @param id The sequence ID
         * @return This builder
         */
        Builder id(String id);

        /**
         * Set the name.
         *
         * @param name The sequence name
         * @return This builder
         */
        Builder name(String name);

        /**
         * Set the description.
         *
         * @param description The sequence description
         * @return This builder
         */
        Builder description(String description);

        /**
         * Set the sequence data.
         *
         * @param sequence The sequence data
         * @return This builder
         */
        Builder sequence(String sequence);

        /**
         * Set the sequence length explicitly (for metadata-only).
         *
         * @param length The sequence length
         * @return This builder
         */
        Builder length(long length);

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
         * Set the division code.
         *
         * @param division The division code
         * @return This builder
         */
        Builder division(String division);

        /**
         * Set the taxonomy.
         *
         * @param taxonomy The taxonomy list
         * @return This builder
         */
        Builder taxonomy(List<String> taxonomy);

        /**
         * Set the organism.
         *
         * @param organism The organism
         * @return This builder
         */
        Builder organism(String organism);

        /**
         * Set annotations.
         *
         * @param annotations The annotations
         * @return This builder
         */
        Builder annotations(Map<String, Object> annotations);

        /**
         * Set the date.
         *
         * @param date The date
         * @return This builder
         */
        Builder date(Date date);

        /**
         * Set the header information.
         *
         * @param headerInfo The header information
         * @return This builder
         */
        Builder headerInfo(HeaderInfo headerInfo);

        /**
         * Build the Sequence.
         *
         * @return The built Sequence
         */
        Sequence build();
    }
}