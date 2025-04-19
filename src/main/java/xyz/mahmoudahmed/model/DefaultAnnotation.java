package xyz.mahmoudahmed.model;

import java.util.*;

/**
 * Default implementation of Annotation.
 */
public class DefaultAnnotation implements Annotation {
    private final String type;
    private final int start;
    private final int end;
    private final int strand;
    private final Integer phase;
    private final String sequenceId;
    private final String featureId;
    private final Map<String, List<String>> qualifiers;

    private DefaultAnnotation(Builder builder) {
        this.type = builder.type;
        this.start = builder.start;
        this.end = builder.end;
        this.strand = builder.strand;
        this.phase = builder.phase;
        this.sequenceId = builder.sequenceId;
        this.featureId = builder.featureId;

        // Create an immutable copy of the qualifiers
        Map<String, List<String>> map = new HashMap<>();
        if (builder.qualifiers != null) {
            for (Map.Entry<String, List<String>> entry : builder.qualifiers.entrySet()) {
                map.put(entry.getKey(), Collections.unmodifiableList(new ArrayList<>(entry.getValue())));
            }
        }
        this.qualifiers = Collections.unmodifiableMap(map);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getStrand() {
        return strand;
    }

    @Override
    public Integer getPhase() {
        return phase;
    }

    @Override
    public String getSequenceId() {
        return sequenceId;
    }

    @Override
    public String getFeatureId() {
        return featureId;
    }

    @Override
    public Map<String, List<String>> getQualifiers() {
        return qualifiers;
    }

    /**
     * Builder implementation for DefaultAnnotation.
     */
    static class Builder implements Annotation.Builder {
        private String type;
        private int start;
        private int end;
        private int strand;
        private Integer phase;
        private String sequenceId;
        private String featureId;
        private Map<String, List<String>> qualifiers = new HashMap<>();

        @Override
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder start(int start) {
            this.start = start;
            return this;
        }

        @Override
        public Builder end(int end) {
            this.end = end;
            return this;
        }

        @Override
        public Builder strand(int strand) {
            this.strand = strand;
            return this;
        }

        @Override
        public Builder phase(Integer phase) {
            this.phase = phase;
            return this;
        }

        @Override
        public Builder sequenceId(String sequenceId) {
            this.sequenceId = sequenceId;
            return this;
        }

        @Override
        public Builder featureId(String featureId) {
            this.featureId = featureId;
            return this;
        }

        @Override
        public Builder qualifiers(Map<String, List<String>> qualifiers) {
            this.qualifiers = new HashMap<>();
            if (qualifiers != null) {
                for (Map.Entry<String, List<String>> entry : qualifiers.entrySet()) {
                    this.qualifiers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
            }
            return this;
        }

        @Override
        public Builder addQualifier(String key, String value) {
            if (key != null) {
                this.qualifiers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
            return this;
        }

        @Override
        public Builder addQualifier(String key, List<String> values) {
            if (key != null && values != null) {
                this.qualifiers.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
            }
            return this;
        }

        @Override
        public Annotation build() {
            return new DefaultAnnotation(this);
        }
    }
}