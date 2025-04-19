package xyz.mahmoudahmed.translator;

/**
 * Interface for genetic codes to translate codons to amino acids
 */
public interface GeneticCode {
    /**
     * Translates a codon to its corresponding amino acid
     *
     * @param codon The RNA codon to translate
     * @return The single-letter amino acid code, or '*' for stop codons
     */
    char translate(String codon);

    /**
     * Determines if a codon is a start codon in this genetic code
     *
     * @param codon The RNA codon to check
     * @return true if this is a start codon, false otherwise
     */
    boolean isStartCodon(String codon);

    /**
     * Determines if a codon is a stop codon in this genetic code
     *
     * @param codon The RNA codon to check
     * @return true if this is a stop codon, false otherwise
     */
    boolean isStopCodon(String codon);

    /**
     * Gets the name of this genetic code
     *
     * @return The name of the genetic code
     */
    String getName();

    /**
     * Translates a codon specifically in the context of being a start codon
     * Different genetic codes may have special rules for start codons
     *
     * @param codon The RNA codon to translate as a potential start codon
     * @return The amino acid code, usually 'M' for start codons
     */
    char translateStartCodon(String codon);
}