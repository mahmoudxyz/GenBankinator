package xyz.mahmoudahmed.translator;

/**
 * Implementation of the Standard (Universal) Genetic Code
 */
public class StandardGeneticCode extends AbstractGeneticCode {
    public StandardGeneticCode() {
        super("Standard Code", GeneticCodeTable.STANDARD);

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

        // Isoleucine (I)
        codonTable.put("AUU", 'I');
        codonTable.put("AUC", 'I');
        codonTable.put("AUA", 'I');

        // Methionine (M)
        codonTable.put("AUG", 'M');

        // Valine (V)
        codonTable.put("GUU", 'V');
        codonTable.put("GUC", 'V');
        codonTable.put("GUA", 'V');
        codonTable.put("GUG", 'V');

        // Serine (S)
        codonTable.put("UCU", 'S');
        codonTable.put("UCC", 'S');
        codonTable.put("UCA", 'S');
        codonTable.put("UCG", 'S');
        codonTable.put("AGU", 'S');
        codonTable.put("AGC", 'S');

        // Proline (P)
        codonTable.put("CCU", 'P');
        codonTable.put("CCC", 'P');
        codonTable.put("CCA", 'P');
        codonTable.put("CCG", 'P');

        // Threonine (T)
        codonTable.put("ACU", 'T');
        codonTable.put("ACC", 'T');
        codonTable.put("ACA", 'T');
        codonTable.put("ACG", 'T');

        // Alanine (A)
        codonTable.put("GCU", 'A');
        codonTable.put("GCC", 'A');
        codonTable.put("GCA", 'A');
        codonTable.put("GCG", 'A');

        // Tyrosine (Y)
        codonTable.put("UAU", 'Y');
        codonTable.put("UAC", 'Y');

        // Histidine (H)
        codonTable.put("CAU", 'H');
        codonTable.put("CAC", 'H');

        // Glutamine (Q)
        codonTable.put("CAA", 'Q');
        codonTable.put("CAG", 'Q');

        // Asparagine (N)
        codonTable.put("AAU", 'N');
        codonTable.put("AAC", 'N');

        // Lysine (K)
        codonTable.put("AAA", 'K');
        codonTable.put("AAG", 'K');

        // Aspartic Acid (D)
        codonTable.put("GAU", 'D');
        codonTable.put("GAC", 'D');

        // Glutamic Acid (E)
        codonTable.put("GAA", 'E');
        codonTable.put("GAG", 'E');

        // Cysteine (C)
        codonTable.put("UGU", 'C');
        codonTable.put("UGC", 'C');

        // Tryptophan (W)
        codonTable.put("UGG", 'W');

        // Arginine (R)
        codonTable.put("CGU", 'R');
        codonTable.put("CGC", 'R');
        codonTable.put("CGA", 'R');
        codonTable.put("CGG", 'R');
        codonTable.put("AGA", 'R');
        codonTable.put("AGG", 'R');

        // Glycine (G)
        codonTable.put("GGU", 'G');
        codonTable.put("GGC", 'G');
        codonTable.put("GGA", 'G');
        codonTable.put("GGG", 'G');

        // Stop codons (*)
        codonTable.put("UAA", '*');
        codonTable.put("UAG", '*');
        codonTable.put("UGA", '*');
    }

    @Override
    protected void initializeStartCodons() {
        // Standard start codon
        startCodons.add("AUG");
        startCodons.add("GUG");
        startCodons.add("UUG");
    }

    @Override
    protected void initializeStopCodons() {
        stopCodons.add("UAA");
        stopCodons.add("UAG");
        stopCodons.add("UGA");
    }
}