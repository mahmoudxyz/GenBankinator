package xyz.mahmoudahmed.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of sequence handling
 */
public class DefaultSequenceHandler implements SequenceHandler {
    @Override
    public String validateSequence(String sequence) {
        // Remove whitespace and convert to uppercase
        String cleanedSequence = sequence.replaceAll("\\s+", "").toUpperCase();

        // Validate characters
        if (!cleanedSequence.matches("[ACGTU]+")) {
            throw new IllegalArgumentException("Invalid sequence: contains characters other than A, C, G, T, or U");
        }

        return cleanedSequence;
    }

    @Override
    public String toRNA(String dnaSequence) {
        return dnaSequence.replace('T', 'U');
    }

    @Override
    public List<String> splitIntoCodons(String sequence) {
        List<String> codons = new ArrayList<>();

        for (int i = 0; i < sequence.length() - 2; i += 3) {
            codons.add(sequence.substring(i, Math.min(i + 3, sequence.length())));
        }

        return codons;
    }
}
