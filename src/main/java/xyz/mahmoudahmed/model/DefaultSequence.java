package xyz.mahmoudahmed.model;

import java.util.*;

/**
 * Default implementation of Sequence.
 */
public class DefaultSequence implements Sequence {
    private final String id;
    private final String name;
    private final String description;
    private final String sequence;
    private final long length;
    private final String moleculeType;
    private final String topology;
    private final String division;
    private final List<String> taxonomy;
    private final String organism;
    private final Map<String, Object> annotations;
    private final Date date;
    private final HeaderInfo headerInfo;

    private DefaultSequence(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.sequence = builder.sequence;
        this.length = builder.length > 0 ? builder.length :
                (builder.sequence != null ? builder.sequence.length() : 0);
        this.moleculeType = builder.moleculeType;
        this.topology = builder.topology;
        this.division = builder.division;
        this.taxonomy = builder.taxonomy != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.taxonomy)) :
                Collections.emptyList();
        this.organism = builder.organism;
        this.annotations = builder.annotations != null ?
                Collections.unmodifiableMap(new HashMap<>(builder.annotations)) :
                Collections.emptyMap();
        this.date = builder.date;
        this.headerInfo = builder.headerInfo;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public String getMoleculeType() {
        return moleculeType;
    }

    @Override
    public String getTopology() {
        return topology;
    }

    @Override
    public String getDivision() {
        return division;
    }

    @Override
    public List<String> getTaxonomy() {
        return taxonomy;
    }

    @Override
    public String getOrganism() {
        return organism;
    }

    @Override
    public Map<String, Object> getAnnotations() {
        return annotations;
    }

    @Override
    public Date getDate() {
        return date != null ? new Date(date.getTime()) : null;
    }

    @Override
    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    /**
     * Builder implementation for DefaultSequence.
     */
    static class Builder implements Sequence.Builder {
        private String id;
        private String name;
        private String description;
        private String sequence;
        private long length;
        private String moleculeType = "DNA";
        private String topology = "linear";
        private String division;
        private List<String> taxonomy;
        private String organism = "Unknown organism";
        private Map<String, Object> annotations;
        private Date date;
        private HeaderInfo headerInfo;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder sequence(String sequence) {
            this.sequence = sequence;
            return this;
        }

        @Override
        public Builder length(long length) {
            this.length = length;
            return this;
        }

        @Override
        public Builder moleculeType(String moleculeType) {
            this.moleculeType = moleculeType;
            return this;
        }

        @Override
        public Builder topology(String topology) {
            this.topology = topology;
            return this;
        }

        @Override
        public Builder division(String division) {
            this.division = division;
            return this;
        }

        @Override
        public Builder taxonomy(List<String> taxonomy) {
            this.taxonomy = taxonomy;
            return this;
        }

        @Override
        public Builder organism(String organism) {
            this.organism = organism;
            return this;
        }

        @Override
        public Builder annotations(Map<String, Object> annotations) {
            this.annotations = annotations;
            return this;
        }

        @Override
        public Builder date(Date date) {
            this.date = date != null ? new Date(date.getTime()) : null;
            return this;
        }

        @Override
        public Builder headerInfo(HeaderInfo headerInfo) {
            this.headerInfo = headerInfo;
            return this;
        }

        @Override
        public Sequence build() {
            return new DefaultSequence(this);
        }
    }
}
