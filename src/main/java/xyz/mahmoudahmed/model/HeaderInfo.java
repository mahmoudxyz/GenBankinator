package xyz.mahmoudahmed.model;

import java.util.List;
import java.util.Map;

/**
 * Represents detailed GenBank header information.
 */
public interface HeaderInfo {
    /**
     * Get the accession number.
     *
     * @return The accession number
     */
    String getAccessionNumber();

    /**
     * Get the version information.
     *
     * @return The version
     */
    String getVersion();

    /**
     * Get the detailed definition/description.
     *
     * @return The definition
     */
    String getDefinition();

    /**
     * Get the keywords for indexing.
     *
     * @return The keywords
     */
    String getKeywords();

    /**
     * Get the taxonomy list.
     *
     * @return The taxonomy list
     */
    List<String> getTaxonomy();

    /**
     * Get the database links.
     *
     * @return Map of database links
     */
    Map<String, String> getDbLinks();

    /**
     * Get the references information.
     *
     * @return List of references
     */
    List<ReferenceInfo> getReferences();

    /**
     * Get the comment text.
     *
     * @return The comment
     */
    String getComment();

    /**
     * Get the assembly data.
     *
     * @return Map of assembly data
     */
    Map<String, String> getAssemblyData();

    /**
     * Get the date.
     *
     * @return The date
     */
    String getDate();

    /**
     * Create a builder for HeaderInfo.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultHeaderInfo.Builder();
    }

    /**
     * Builder for HeaderInfo.
     */
    interface Builder {
        /**
         * Set the accession number.
         *
         * @param accessionNumber The accession number
         * @return This builder
         */
        Builder accessionNumber(String accessionNumber);

        /**
         * Set the version.
         *
         * @param version The version
         * @return This builder
         */
        Builder version(String version);

        /**
         * Set the definition.
         *
         * @param definition The definition
         * @return This builder
         */
        Builder definition(String definition);

        /**
         * Set the keywords.
         *
         * @param keywords The keywords
         * @return This builder
         */
        Builder keywords(String keywords);

        /**
         * Set the taxonomy.
         *
         * @param taxonomy The taxonomy list
         * @return This builder
         */
        Builder taxonomy(List<String> taxonomy);

        /**
         * Set the database links.
         *
         * @param dbLinks Map of database links
         * @return This builder
         */
        Builder dbLinks(Map<String, String> dbLinks);

        /**
         * Set the references.
         *
         * @param references List of references
         * @return This builder
         */
        Builder references(List<ReferenceInfo> references);

        /**
         * Set the comment.
         *
         * @param comment The comment
         * @return This builder
         */
        Builder comment(String comment);

        /**
         * Set the assembly data.
         *
         * @param assemblyData Map of assembly data
         * @return This builder
         */
        Builder assemblyData(Map<String, String> assemblyData);

        /**
         * Set the date.
         *
         * @param date The date
         * @return This builder
         */
        Builder date(String date);

        /**
         * Build the HeaderInfo.
         *
         * @return The built HeaderInfo
         */
        HeaderInfo build();
    }
}