package xyz.mahmoudahmed.model;

import java.util.*;

/**
 * Default implementation of SequenceData.
 */
public class DefaultSequenceData implements SequenceData {
    private final List<Sequence> sequences;
    private final Map<String, Sequence> sequencesById;

    private DefaultSequenceData(Builder builder) {
        this.sequences = Collections.unmodifiableList(new ArrayList<>(builder.sequences));

        // Build a map of sequences by ID for quick lookup
        Map<String, Sequence> map = new HashMap<>();
        for (Sequence sequence : this.sequences) {
            map.put(sequence.getId(), sequence);
        }
        this.sequencesById = Collections.unmodifiableMap(map);
    }

    @Override
    public List<Sequence> getSequences() {
        return sequences;
    }

    @Override
    public Sequence getSequence(String id) {
        return sequencesById.get(id);
    }

    @Override
    public int getCount() {
        return sequences.size();
    }

    /**
     * Builder implementation for DefaultSequenceData.
     */
    static class Builder implements SequenceData.Builder {
        private final List<Sequence> sequences = new ArrayList<>();

        @Override
        public Builder addSequence(Sequence sequence) {
            if (sequence != null) {
                sequences.add(sequence);
            }
            return this;
        }

        @Override
        public Builder addSequences(List<Sequence> sequences) {
            if (sequences != null) {
                this.sequences.addAll(sequences);
            }
            return this;
        }

        @Override
        public SequenceData build() {
            return new DefaultSequenceData(this);
        }
    }
}