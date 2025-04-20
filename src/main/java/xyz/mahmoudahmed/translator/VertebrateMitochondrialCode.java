package xyz.mahmoudahmed.translator;

/**
 * Implementation of the Vertebrate Mitochondrial Code
 */
public class VertebrateMitochondrialCode extends AbstractGeneticCode {
    public VertebrateMitochondrialCode() {
        super("Vertebrate Mitochondrial Code", GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL);
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

        // Methionine (M)
        codonTable.put("AUG", 'M');
        codonTable.put("AUA", 'M'); // AUA codes for Met in vertebrate mitochondria

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

        // Stop codons (*)
        codonTable.put("UAA", '*');
        codonTable.put("UAG", '*');
        codonTable.put("AGA", '*'); // AGA and AGG are stop codons in vertebrate mitochondria
        codonTable.put("AGG", '*');

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
        codonTable.put("UGA", 'W'); // UGA codes for Trp in mitochondria

        // Arginine (R)
        codonTable.put("CGU", 'R');
        codonTable.put("CGC", 'R');
        codonTable.put("CGA", 'R');
        codonTable.put("CGG", 'R');

        // Glycine (G)
        codonTable.put("GGU", 'G');
        codonTable.put("GGC", 'G');
        codonTable.put("GGA", 'G');
        codonTable.put("GGG", 'G');
    }

    @Override
    protected void initializeStartCodons() {
        // Standard start codons in vertebrate mitochondria
        startCodons.add("AUG");
        startCodons.add("AUA");
        startCodons.add("AUU");
        startCodons.add("GUG");
    }

    @Override
    protected void initializeStopCodons() {
        stopCodons.add("UAA");
        stopCodons.add("UAG");
        stopCodons.add("AGA");
        stopCodons.add("AGG");
    }
}