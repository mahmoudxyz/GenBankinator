package xyz.mahmoudahmed.translator;

import xyz.mahmoudahmed.model.TranslationOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the translator (uses Strategy Pattern)
 */
public class StandardTranslator implements Translator {
    private final SequenceHandler sequenceHandler;
    private final GeneticCode geneticCode;
    private final TranslationOptions options;

    public StandardTranslator(SequenceHandler sequenceHandler, GeneticCode geneticCode) {
        this(sequenceHandler, geneticCode, null);
    }

    public StandardTranslator(SequenceHandler sequenceHandler, GeneticCode geneticCode, TranslationOptions options) {
        this.sequenceHandler = sequenceHandler;
        this.geneticCode = geneticCode;
        this.options = options;
    }


    /**
     * Get the genetic code used by this translator.
     *
     * @return The genetic code
     */
    public GeneticCode getGeneticCode() {
        return geneticCode;
    }

    /**
     * Get the translation options.
     *
     * @return The translation options or null if not set
     */
    public TranslationOptions getOptions() {
        return options;
    }


    @Override
    public String translate(String sequence, boolean isRNA) {
        // Validate and prepare the sequence
        String validatedSequence = sequenceHandler.validateSequence(sequence);
        String rnaSequence = isRNA ? validatedSequence : sequenceHandler.toRNA(validatedSequence);

        // Split into codons
        List<String> codons = sequenceHandler.splitIntoCodons(rnaSequence);

        // Check if we need to handle internal stop codons
        boolean allowInternalStopCodons = false;
        if (geneticCode instanceof AbstractGeneticCode) {
            AbstractGeneticCode abstractCode = (AbstractGeneticCode) geneticCode;
            // Allow internal stop codons for marine mitochondrial codes or if specified in options
            allowInternalStopCodons = abstractCode.getTable() == GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL ||
                    (options != null && options.isAllowInternalStopCodons());
        }

        // Find the last non-stop codon
        int lastNonStopCodonIndex = -1;
        for (int i = codons.size() - 1; i >= 0; i--) {
            String codon = codons.get(i);
            if (codon.length() == 3 && !geneticCode.isStopCodon(codon)) {
                lastNonStopCodonIndex = i;
                break;
            }
        }

        // Translate
        StringBuilder protein = new StringBuilder();
        boolean isFirstCodon = true;

        for (int i = 0; i <= lastNonStopCodonIndex; i++) {
            // Only translate up to the last non-stop codon
            // This handles the case where there might be multiple stop codons at the end
            String codon = codons.get(i);
            if (codon.length() == 3) {
                char aminoAcid;

                // Special handling for start codons
                if (isFirstCodon) {
                    aminoAcid = geneticCode.translateStartCodon(codon);
                } else {
                    aminoAcid = geneticCode.translate(codon);
                }

                // Handle stop codons (any stop codons here must be internal)
                if (aminoAcid == '*') {
                    if (allowInternalStopCodons) {
                        // For marine mitochondrial genomes or when allowed: replace with dash
                        protein.append('-');
                    } else {
                        // Standard NCBI behavior: stop at first stop codon
                        break;
                    }
                } else {
                    protein.append(aminoAcid);
                }

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