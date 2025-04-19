package xyz.mahmoudahmed.translator;

import java.util.List;

/**
 * Interface for sequence validation and manipulation
 */
interface SequenceHandler {
    String validateSequence(String sequence);
    String toRNA(String dnaSequence);
    List<String> splitIntoCodons(String sequence);
}
