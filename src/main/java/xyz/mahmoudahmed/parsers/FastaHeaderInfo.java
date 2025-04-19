package xyz.mahmoudahmed.parsers;


/**
 * Immutable record to store FASTA annotation header information.
 * This class encapsulates all the data extracted from a FASTA header line.
 */
public record FastaHeaderInfo(
        /**
         * The sequence identifier (e.g., accession number, organism name)
         */
        String seqId,

        /**
         * The feature type or gene name (e.g., "cox1", "trnM", "ND5")
         */
        String featureType,

        /**
         * The start position (1-based inclusive)
         */
        int start,

        /**
         * The end position (1-based inclusive)
         */
        int end,

        /**
         * The strand: 1 for forward (+), -1 for reverse (-)
         */
        int strand,

        /**
         * Optional qualifier information in parentheses (e.g., anticodon)
         */
        String qualifier,

        /**
         * Whether this feature is on the complement strand
         */
        boolean isComplement
) {
    /**
     * Returns the length of this feature in base pairs
     *
     * @return Length in nucleotides
     */
    public int length() {
        return end - start + 1;
    }

    /**
     * Checks if this feature is on the forward strand
     *
     * @return true if forward strand, false if reverse
     */
    public boolean isForwardStrand() {
        return strand > 0;
    }

    /**
     * Gets a standardized string representation of the position
     *
     * @return A string like "1..100" or "complement(1..100)"
     */
    public String getLocationString() {
        String position = start + ".." + end;
        return isComplement ? "complement(" + position + ")" : position;
    }

    /**
     * Gets the feature type in lowercase for case-insensitive comparisons
     *
     * @return Lowercase feature type
     */
    public String getFeatureTypeLowerCase() {
        return featureType != null ? featureType.toLowerCase() : null;
    }
}