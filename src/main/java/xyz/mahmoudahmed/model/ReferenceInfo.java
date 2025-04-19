package xyz.mahmoudahmed.model;

import java.util.List;

/**
 * Represents reference information for GenBank header.
 */
public interface ReferenceInfo {
    /**
     * Get the reference number.
     *
     * @return The reference number
     */
    Integer getNumber();

    /**
     * Get the reference base range.
     *
     * @return The base range
     */
    String getBaseRange();

    /**
     * Get the list of authors.
     *
     * @return The authors
     */
    List<String> getAuthors();

    /**
     * Get the reference title.
     *
     * @return The title
     */
    String getTitle();

    /**
     * Get the journal information.
     *
     * @return The journal
     */
    String getJournal();

    /**
     * Get the publication status.
     *
     * @return The publication status
     */
    String getPubStatus();

    /**
     * Create a builder for ReferenceInfo.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultReferenceInfo.Builder();
    }

    /**
     * Builder for ReferenceInfo.
     */
    interface Builder {
        /**
         * Set the reference number.
         *
         * @param number The reference number
         * @return This builder
         */
        Builder number(Integer number);

        /**
         * Set the base range.
         *
         * @param baseRange The base range
         * @return This builder
         */
        Builder baseRange(String baseRange);

        /**
         * Set the authors.
         *
         * @param authors The authors
         * @return This builder
         */
        Builder authors(List<String> authors);

        /**
         * Set the title.
         *
         * @param title The title
         * @return This builder
         */
        Builder title(String title);

        /**
         * Set the journal.
         *
         * @param journal The journal
         * @return This builder
         */
        Builder journal(String journal);

        /**
         * Set the publication status.
         *
         * @param pubStatus The publication status
         * @return This builder
         */
        Builder pubStatus(String pubStatus);

        /**
         * Build the ReferenceInfo.
         *
         * @return The built ReferenceInfo
         */
        ReferenceInfo build();
    }
}