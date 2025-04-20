package xyz.mahmoudahmed.translator;

/**
 * Enum representing different genetic code tables according to NCBI standards.
 */
public enum GeneticCodeTable {
    STANDARD(1, "Standard Code"),
    VERTEBRATE_MITOCHONDRIAL(2, "Vertebrate Mitochondrial Code"),
    YEAST_MITOCHONDRIAL(3, "Yeast Mitochondrial Code"),
    MOLD_PROTOZOAN_MITOCHONDRIAL(4, "Mold, Protozoan, and Coelenterate Mitochondrial Code"),
    INVERTEBRATE_MITOCHONDRIAL(5, "Invertebrate Mitochondrial Code"),
    CILIATE_NUCLEAR(6, "Ciliate, Dasycladacean and Hexamita Nuclear Code"),
    ECHINODERM_MITOCHONDRIAL(9, "Echinoderm and Flatworm Mitochondrial Code"),
    BACTERIAL_PLASTID(11, "Bacterial, Archaeal and Plant Plastid Code"),
    ALTERNATIVE_YEAST_NUCLEAR(12, "Alternative Yeast Nuclear Code"),
    ASCIDIAN_MITOCHONDRIAL(13, "Ascidian Mitochondrial Code"),
    TREMATODE_MITOCHONDRIAL(21, "Trematode Mitochondrial Code");

    private final int tableNumber;
    private final String description;

    GeneticCodeTable(int tableNumber, String description) {
        this.tableNumber = tableNumber;
        this.description = description;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the GeneticCodeTable by its NCBI table number.
     *
     * @param tableNumber The NCBI translation table number
     * @return The corresponding GeneticCodeTable or default if not found
     */
    public static GeneticCodeTable getByTableNumber(int tableNumber) {
        for (GeneticCodeTable table : values()) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return INVERTEBRATE_MITOCHONDRIAL; // Default to table 5 if not found
    }

    /**
     * Get the GeneticCodeTable by its name or number as a string.
     *
     * @param nameOrNumber The name or number of the genetic code table
     * @return The corresponding GeneticCodeTable or default if not found
     */
    public static GeneticCodeTable getByNameOrNumber(String nameOrNumber) {
        if (nameOrNumber == null || nameOrNumber.isEmpty()) {
            return INVERTEBRATE_MITOCHONDRIAL;
        }

        // Try to parse as a number first
        try {
            int tableNumber = Integer.parseInt(nameOrNumber);
            return getByTableNumber(tableNumber);
        } catch (NumberFormatException e) {
            // Not a number, try to match by enum name or description
            for (GeneticCodeTable table : values()) {
                if (table.name().equalsIgnoreCase(nameOrNumber) ||
                        table.getDescription().equalsIgnoreCase(nameOrNumber)) {
                    return table;
                }
            }
            return INVERTEBRATE_MITOCHONDRIAL; // Default if not found
        }
    }
}