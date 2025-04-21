package xyz.mahmoudahmed.model;

import xyz.mahmoudahmed.translator.GeneticCodeTable;

/**
 * Options for translation operations with proper precedence handling.
 */
public class TranslationOptions {
    private final Integer translTableNumber;
    private final String geneticCode;
    private final GeneticCodeTable explicitCodeTable;
    private final boolean includeStopCodon;
    private final boolean translateCDS;
    private final boolean allowInternalStopCodons;

    private TranslationOptions(Builder builder) {
        this.translTableNumber = builder.translTableNumber;
        this.geneticCode = builder.geneticCode;
        this.explicitCodeTable = builder.explicitCodeTable;
        this.includeStopCodon = builder.includeStopCodon;
        this.translateCDS = builder.translateCDS;
        this.allowInternalStopCodons = builder.allowInternalStopCodons;
    }

    /**
     * Get the genetic code table to use for translation, applying precedence:
     * 1. translTableNumber (if specified)
     * 2. geneticCode (if specified)
     * 3. explicitCodeTable (if specified)
     * 4. Default to Invertebrate Mitochondrial
     *
     * @return The genetic code table (never null)
     */
    public GeneticCodeTable getGeneticCodeTable() {
        // Apply precedence rules
        if (translTableNumber != null) {
            return GeneticCodeTable.getByTableNumber(translTableNumber);
        } else if (geneticCode != null && !geneticCode.isEmpty()) {
            return GeneticCodeTable.getByNameOrNumber(geneticCode);
        } else if (explicitCodeTable != null) {
            return explicitCodeTable;
        } else {
            return GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL;
        }
    }

    /**
     * Get the NCBI translation table number.
     *
     * @return The NCBI translation table number
     */
    public Integer getTranslTableNumber() {
        return translTableNumber;
    }

    /**
     * Get the genetic code string.
     *
     * @return The genetic code string
     */
    public String getGeneticCode() {
        return geneticCode;
    }

    /**
     * Check if stop codons should be included in translation.
     *
     * @return true if stop codons should be included
     */
    public boolean isIncludeStopCodon() {
        return includeStopCodon;
    }

    /**
     * Check if CDS features should be automatically translated.
     *
     * @return true if CDS features should be translated
     */
    public boolean isTranslateCDS() {
        return translateCDS;
    }

    /**
     * Check if internal stop codons should be represented as dashes.
     *
     * @return true if internal stop codons should be represented as dashes
     */
    public boolean isAllowInternalStopCodons() {
        return allowInternalStopCodons;
    }

    /**
     * Create a builder for TranslationOptions.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for TranslationOptions.
     */
    public static class Builder {
        private Integer translTableNumber;
        private String geneticCode;
        private GeneticCodeTable explicitCodeTable;
        private boolean includeStopCodon = false;
        private boolean translateCDS = true;
        private boolean allowInternalStopCodons = false;

        /**
         * Set whether internal stop codons should be represented as dashes.
         * Useful for certain marine mitochondrial genomes with known internal stops.
         *
         * @param allowInternalStopCodons true to represent internal stops as dashes
         * @return This builder
         */
        public Builder allowInternalStopCodons(boolean allowInternalStopCodons) {
            this.allowInternalStopCodons = allowInternalStopCodons;
            return this;
        }

        /**
         * Set the genetic code table explicitly.
         * Note: translTableNumber and geneticCode take precedence if specified.
         *
         * @param geneticCodeTable The genetic code table
         * @return This builder
         */
        public Builder geneticCodeTable(GeneticCodeTable geneticCodeTable) {
            this.explicitCodeTable = geneticCodeTable;
            return this;
        }

        /**
         * Set the genetic code table by its NCBI table number.
         * This has the highest precedence when determining which table to use.
         *
         * @param tableNumber The NCBI translation table number
         * @return This builder
         */
        public Builder translTableNumber(int tableNumber) {
            this.translTableNumber = tableNumber;
            return this;
        }

        /**
         * Set the genetic code by name or number as a string.
         * This can be the enum name, description, or table number.
         * translTableNumber takes precedence over this if both are specified.
         *
         * @param nameOrNumber The name or number of the genetic code table
         * @return This builder
         */
        public Builder geneticCode(String nameOrNumber) {
            this.geneticCode = nameOrNumber;
            return this;
        }

        /**
         * Set whether to include stop codons in translation.
         *
         * @param includeStopCodon true to include stop codons
         * @return This builder
         */
        public Builder includeStopCodon(boolean includeStopCodon) {
            this.includeStopCodon = includeStopCodon;
            return this;
        }

        /**
         * Set whether to automatically translate CDS features.
         *
         * @param translateCDS true to translate CDS features
         * @return This builder
         */
        public Builder translateCDS(boolean translateCDS) {
            this.translateCDS = translateCDS;
            return this;
        }

        /**
         * Build the TranslationOptions.
         *
         * @return The built TranslationOptions
         */
        public TranslationOptions build() {
            return new TranslationOptions(this);
        }
    }
}