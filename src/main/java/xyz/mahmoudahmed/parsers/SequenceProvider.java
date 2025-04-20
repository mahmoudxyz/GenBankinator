package xyz.mahmoudahmed.parsers;


import java.util.HashMap;
import java.util.Map;

/**
 * Singleton utility class to store and provide access to full genomic sequences
 */
public class SequenceProvider {
    private static final Map<String, String> genomeSequences = new HashMap<>();

    private SequenceProvider() {
        // Private constructor to prevent instantiation
    }

    /**
     * Store a genome sequence
     *
     * @param id The sequence identifier
     * @param sequence The full genomic sequence
     */
    public static void addGenome(String id, String sequence) {
        genomeSequences.put(id, sequence);
    }

    /**
     * Extract a region from a genome sequence using 1-based coordinates
     *
     * @param id The sequence identifier
     * @param start Start position (1-based)
     * @param end End position (1-based, inclusive)
     * @return The extracted sequence or null if not found
     */
    public static String getRegion(String id, int start, int end) {
        String fullSequence = genomeSequences.get(id);
        if (fullSequence == null) return null;

        // Convert from 1-based to 0-based indexing
        int zeroBasedStart = start - 1;

        // Validate coordinates
        if (zeroBasedStart < 0 || end > fullSequence.length()) {
            return null;
        }

        return fullSequence.substring(zeroBasedStart, end);
    }

    /**
     * Clear all stored sequences
     */
    public static void clear() {
        genomeSequences.clear();
    }
}