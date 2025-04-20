package xyz.mahmoudahmed.model;

import xyz.mahmoudahmed.translator.GeneticCodeTable;

/**
 * Options for translation operations.
 */
public class TranslationOptions {
    private final GeneticCodeTable geneticCodeTable;
    private final boolean includeStopCodon;
    private final boolean translateCDS;
    private final boolean allowInternalStopCodons;

    private TranslationOptions(Builder builder) {
        this.geneticCodeTable = builder.geneticCodeTable != null ?
                builder.geneticCodeTable :
                GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL;
        this.includeStopCodon = builder.includeStopCodon;
        this.translateCDS = builder.translateCDS;
        this.allowInternalStopCodons = builder.allowInternalStopCodons;
    }


    /**
     * Get the genetic code table to use it for translation.
     *
     * @return The genetic code table (never null)
     */
    public GeneticCodeTable getGeneticCodeTable() {
        return geneticCodeTable != null ?
                geneticCodeTable :
                GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL;
    }

    /**
     * Get the NCBI translation table number.
     *
     * @return The NCBI translation table number
     */
    public int getTranslTableNumber() {
        return geneticCodeTable != null ?
                geneticCodeTable.getTableNumber() :
                GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL.getTableNumber();
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
     * Create a builder for TranslationOptions.
     *
     * @return A new builder
     */
    public static Builder builder() {
        return new Builder();
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
     * Builder for TranslationOptions.
     */
    public static class Builder {
        private GeneticCodeTable geneticCodeTable = GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL;
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
         * Set the genetic code table to use for translation.
         *
         * @param geneticCodeTable The genetic code table
         * @return This builder
         */
        public Builder geneticCodeTable(GeneticCodeTable geneticCodeTable) {
            this.geneticCodeTable = geneticCodeTable;
            return this;
        }

        /**
         * Set the genetic code table by its NCBI table number.
         *
         * @param tableNumber The NCBI translation table number
         * @return This builder
         */
        public Builder translTableNumber(int tableNumber) {
            this.geneticCodeTable = GeneticCodeTable.getByTableNumber(tableNumber);
            return this;
        }

        /**
         * Set the genetic code by name or number as a string.
         * This can be the enum name, description, or table number.
         *
         * @param nameOrNumber The name or number of the genetic code table
         * @return This builder
         */
        public Builder geneticCode(String nameOrNumber) {
            this.geneticCodeTable = GeneticCodeTable.getByNameOrNumber(nameOrNumber);
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