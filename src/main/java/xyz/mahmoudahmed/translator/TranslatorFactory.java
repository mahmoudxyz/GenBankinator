package xyz.mahmoudahmed.translator;

import xyz.mahmoudahmed.model.TranslationOptions;

/**
 * Factory for creating translators based on genetic code tables.
 */
public class TranslatorFactory {
    /**
     * Create a translator based on the specified genetic code table and translation options.
     *
     * @param geneticCodeTable The genetic code table to use
     * @param options The translation options to customize behavior
     * @return A translator configured with the specified genetic code and options
     */
    public static Translator createTranslator(GeneticCodeTable geneticCodeTable, TranslationOptions options) {
        GeneticCode geneticCode;

        if (geneticCodeTable == null) {
            geneticCodeTable = GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL;
        }

        switch (geneticCodeTable) {
            case STANDARD:
                geneticCode = new StandardGeneticCode();
                break;
            case VERTEBRATE_MITOCHONDRIAL:
                geneticCode = new VertebrateMitochondrialCode();
                break;
            case INVERTEBRATE_MITOCHONDRIAL:
                geneticCode = new InvertebrateMitochondrialCode();
                break;
            case YEAST_MITOCHONDRIAL:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case MOLD_PROTOZOAN_MITOCHONDRIAL:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case CILIATE_NUCLEAR:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case ECHINODERM_MITOCHONDRIAL:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case BACTERIAL_PLASTID:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case ALTERNATIVE_YEAST_NUCLEAR:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case ASCIDIAN_MITOCHONDRIAL:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            case TREMATODE_MITOCHONDRIAL:
                // Add implementation when available
                geneticCode = new InvertebrateMitochondrialCode(); // Fallback
                break;
            default:
                // Fall back to Invertebrate Mitochondrial for unimplemented tables
                geneticCode = new InvertebrateMitochondrialCode();
                break;
        }

        return new StandardTranslator(new DefaultSequenceHandler(), geneticCode, options);
    }

    /**
     * Create a translator based on the specified genetic code table.
     *
     * @param geneticCodeTable The genetic code table to use
     * @return A translator configured with the specified genetic code
     */
    public static Translator createTranslator(GeneticCodeTable geneticCodeTable) {
        return createTranslator(geneticCodeTable, null);
    }

    /**
     * Create a translator based on the NCBI translation table number and options.
     *
     * @param translTableNumber The NCBI translation table number
     * @param options The translation options to customize behavior
     * @return A translator configured with the specified genetic code and options
     */
    public static Translator createTranslator(int translTableNumber, TranslationOptions options) {
        return createTranslator(GeneticCodeTable.getByTableNumber(translTableNumber), options);
    }

    /**
     * Create a translator based on the NCBI translation table number.
     *
     * @param translTableNumber The NCBI translation table number
     * @return A translator configured with the specified genetic code
     */
    public static Translator createTranslator(int translTableNumber) {
        return createTranslator(GeneticCodeTable.getByTableNumber(translTableNumber), null);
    }

    /**
     * Create a translator based on the genetic code name or number as a string and options.
     *
     * @param nameOrNumber The name or number of the genetic code table
     * @param options The translation options to customize behavior
     * @return A translator configured with the specified genetic code and options
     */
    public static Translator createTranslator(String nameOrNumber, TranslationOptions options) {
        return createTranslator(GeneticCodeTable.getByNameOrNumber(nameOrNumber), options);
    }

    /**
     * Create a translator based on the genetic code name or number as a string.
     *
     * @param nameOrNumber The name or number of the genetic code table
     * @return A translator configured with the specified genetic code
     */
    public static Translator createTranslator(String nameOrNumber) {
        return createTranslator(GeneticCodeTable.getByNameOrNumber(nameOrNumber), null);
    }

    /**
     * Create a translator using the Invertebrate Mitochondrial genetic code with options.
     *
     * @param options The translation options to customize behavior
     * @return A translator using the Invertebrate Mitochondrial genetic code and options
     */
    public static Translator createInvertebrateMitochondrialTranslator(TranslationOptions options) {
        return createTranslator(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, options);
    }

    /**
     * Create a translator using the Invertebrate Mitochondrial genetic code.
     * Kept for backward compatibility.
     *
     * @return A translator using the Invertebrate Mitochondrial genetic code
     */
    public static Translator createInvertebrateMitochondrialTranslator() {
        return createTranslator(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL, null);
    }
}