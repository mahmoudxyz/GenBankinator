package xyz.mahmoudahmed.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the translator (uses Strategy Pattern)
 */
public class StandardTranslator implements Translator {
    private final SequenceHandler sequenceHandler;
    private final GeneticCode geneticCode;

    public StandardTranslator(SequenceHandler sequenceHandler, GeneticCode geneticCode) {
        this.sequenceHandler = sequenceHandler;
        this.geneticCode = geneticCode;
    }

    @Override
    public String translate(String sequence, boolean isRNA) {
        // Validate and prepare the sequence
        String validatedSequence = sequenceHandler.validateSequence(sequence);
        String rnaSequence = isRNA ? validatedSequence : sequenceHandler.toRNA(validatedSequence);

        // Split into codons
        List<String> codons = sequenceHandler.splitIntoCodons(rnaSequence);

        // Translate
        StringBuilder protein = new StringBuilder();
        boolean isFirstCodon = true;

        for (String codon : codons) {
            if (codon.length() == 3) {  // Ensure complete codon
                char aminoAcid;

                // Special handling for start codons
                if (isFirstCodon) {
                    aminoAcid = geneticCode.translateStartCodon(codon);
                } else {
                    aminoAcid = geneticCode.translate(codon);
                }

                if (aminoAcid == '*') {
                    break;  // Stop at termination codon
                }

                protein.append(aminoAcid);
                isFirstCodon = false;
            }
        }

        return protein.toString();
    }

    @Override
    public List<String> findOpenReadingFrames(String sequence, boolean isRNA) {
        // Validate and prepare the sequence
        String validatedSequence = sequenceHandler.validateSequence(sequence);
        String rnaSequence = isRNA ? validatedSequence : sequenceHandler.toRNA(validatedSequence);

        List<String> orfs = new ArrayList<>();

        // Process each reading frame (0, 1, 2)
        for (int frameShift = 0; frameShift < 3; frameShift++) {
            StringBuilder currentProtein = null;

            for (int i = frameShift; i <= rnaSequence.length() - 3; i += 3) {
                String codon = rnaSequence.substring(i, i + 3);

                if (geneticCode.isStartCodon(codon) && currentProtein == null) {
                    // Start of a new ORF
                    currentProtein = new StringBuilder();
                    currentProtein.append(geneticCode.translateStartCodon(codon));
                } else if (currentProtein != null) {
                    char aminoAcid = geneticCode.translate(codon);

                    if (geneticCode.isStopCodon(codon)) {
                        // End of current ORF
                        orfs.add(currentProtein.toString());
                        currentProtein = null;
                    } else {
                        // Continue building current ORF
                        currentProtein.append(aminoAcid);
                    }
                }
            }

            // Add any incomplete ORF at the end
            if (currentProtein != null && !currentProtein.isEmpty()) {
                orfs.add(currentProtein.toString());
            }
        }

        return orfs;
    }
}