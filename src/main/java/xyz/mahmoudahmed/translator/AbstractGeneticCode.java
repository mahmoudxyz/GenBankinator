package xyz.mahmoudahmed.translator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for genetic codes (Template Pattern)
 */
public abstract class AbstractGeneticCode implements GeneticCode {
    protected final Map<String, Character> codonTable = new HashMap<>();
    protected final Set<String> startCodons = new HashSet<>();
    protected final Set<String> stopCodons = new HashSet<>();
    protected final String name;
    protected final GeneticCodeTable table;

    public AbstractGeneticCode(String name, GeneticCodeTable table) {
        this.name = name;
        this.table = table;
        initializeCodonTable();
        initializeStartCodons();
        initializeStopCodons();
    }

    /**
     * Get the genetic code table associated with this code.
     *
     * @return The genetic code table
     */
    public GeneticCodeTable getTable() {
        return table;
    }

    protected abstract void initializeCodonTable();
    protected abstract void initializeStartCodons();
    protected abstract void initializeStopCodons();

    @Override
    public char translate(String codon) {
        return codonTable.getOrDefault(codon.toUpperCase(), '?');
    }

    @Override
    public boolean isStartCodon(String codon) {
        return startCodons.contains(codon.toUpperCase());
    }

    @Override
    public boolean isStopCodon(String codon) {
        return stopCodons.contains(codon.toUpperCase());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char translateStartCodon(String codon) {
        return isStartCodon(codon.toUpperCase()) ? 'M' : translate(codon);
    }
}