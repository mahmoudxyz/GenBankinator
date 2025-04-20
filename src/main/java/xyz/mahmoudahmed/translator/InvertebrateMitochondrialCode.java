package xyz.mahmoudahmed.translator;

/**
 * Concrete implementation of the Invertebrate Mitochondrial Code
 */
public class InvertebrateMitochondrialCode extends AbstractGeneticCode {
    public InvertebrateMitochondrialCode() {
        super("Invertebrate Mitochondrial Code", GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL);
    }

    @Override
    protected void initializeCodonTable() {
        // Phenylalanine (F)
        codonTable.put("UUU", 'F');
        codonTable.put("UUC", 'F');

        // Leucine (L)
        codonTable.put("UUA", 'L');
        codonTable.put("UUG", 'L');
        codonTable.put("CUU", 'L');
        codonTable.put("CUC", 'L');
        codonTable.put("CUA", 'L');
        codonTable.put("CUG", 'L');

        // Serine (S)
        codonTable.put("UCU", 'S');
        codonTable.put("UCC", 'S');
        codonTable.put("UCA", 'S');
        codonTable.put("UCG", 'S');
        codonTable.put("AGU", 'S');
        codonTable.put("AGC", 'S');
        // Key differences from standard code
        codonTable.put("AGA", 'S');
        codonTable.put("AGG", 'S');

        // Tyrosine (Y)
        codonTable.put("UAU", 'Y');
        codonTable.put("UAC", 'Y');

        // Stop codons (*)
        codonTable.put("UAA", '*');
        codonTable.put("UAG", '*');

        // Cysteine (C)
        codonTable.put("UGU", 'C');
        codonTable.put("UGC", 'C');

        // Tryptophan (W)
        codonTable.put("UGG", 'W');
        // Key difference from standard code
        codonTable.put("UGA", 'W'); // UGA -> Trp (W) instead of Stop (*)

        // Proline (P)
        codonTable.put("CCU", 'P');
        codonTable.put("CCC", 'P');
        codonTable.put("CCA", 'P');
        codonTable.put("CCG", 'P');

        // Histidine (H)
        codonTable.put("CAU", 'H');
        codonTable.put("CAC", 'H');

        // Glutamine (Q)
        codonTable.put("CAA", 'Q');
        codonTable.put("CAG", 'Q');

        // Arginine (R)
        codonTable.put("CGU", 'R');
        codonTable.put("CGC", 'R');
        codonTable.put("CGA", 'R');
        codonTable.put("CGG", 'R');

        // Isoleucine (I)
        codonTable.put("AUU", 'I');
        codonTable.put("AUC", 'I');

        // Methionine (M)
        codonTable.put("AUG", 'M');
        // Key difference from standard code
        codonTable.put("AUA", 'M'); // AUA -> Met (M) instead of Ile (I)

        // Threonine (T)
        codonTable.put("ACU", 'T');
        codonTable.put("ACC", 'T');
        codonTable.put("ACA", 'T');
        codonTable.put("ACG", 'T');

        // Asparagine (N)
        codonTable.put("AAU", 'N');
        codonTable.put("AAC", 'N');

        // Lysine (K)
        codonTable.put("AAA", 'K');
        codonTable.put("AAG", 'K');

        // Valine (V)
        codonTable.put("GUU", 'V');
        codonTable.put("GUC", 'V');
        codonTable.put("GUA", 'V');
        codonTable.put("GUG", 'V');

        // Alanine (A)
        codonTable.put("GCU", 'A');
        codonTable.put("GCC", 'A');
        codonTable.put("GCA", 'A');
        codonTable.put("GCG", 'A');

        // Aspartic acid (D)
        codonTable.put("GAU", 'D');
        codonTable.put("GAC", 'D');

        // Glutamic acid (E)
        codonTable.put("GAA", 'E');
        codonTable.put("GAG", 'E');

        // Glycine (G)
        codonTable.put("GGU", 'G');
        codonTable.put("GGC", 'G');
        codonTable.put("GGA", 'G');
        codonTable.put("GGG", 'G');
    }

    @Override
    protected void initializeStartCodons() {
        // Standard start codon
        startCodons.add("AUG");
        startCodons.add("AUA");
        startCodons.add("AUU");
        startCodons.add("AUC");
        startCodons.add("GUG");
        startCodons.add("UUG");
    }

    @Override
    protected void initializeStopCodons() {
        stopCodons.add("UAA");
        stopCodons.add("UAG");
    }
}
