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

    public AbstractGeneticCode(String name) {
        this.name = name;
        initializeCodonTable();
        initializeStartCodons();
        initializeStopCodons();
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