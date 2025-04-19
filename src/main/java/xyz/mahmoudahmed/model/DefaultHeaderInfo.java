package xyz.mahmoudahmed.model;

import java.util.*;

/**
 * Default implementation of HeaderInfo.
 */
class DefaultHeaderInfo implements HeaderInfo {
    private final String accessionNumber;
    private final String version;
    private final String definition;
    private final String keywords;
    private final List<String> taxonomy;
    private final Map<String, String> dbLinks;
    private final List<ReferenceInfo> references;
    private final String comment;
    private final Map<String, String> assemblyData;
    private final String date;

    private DefaultHeaderInfo(Builder builder) {
        this.accessionNumber = builder.accessionNumber;
        this.version = builder.version;
        this.definition = builder.definition;
        this.keywords = builder.keywords;
        this.taxonomy = builder.taxonomy != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.taxonomy)) :
                Collections.emptyList();
        this.dbLinks = builder.dbLinks != null ?
                Collections.unmodifiableMap(new HashMap<>(builder.dbLinks)) :
                Collections.emptyMap();
        this.references = builder.references != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.references)) :
                Collections.emptyList();
        this.comment = builder.comment;
        this.assemblyData = builder.assemblyData != null ?
                Collections.unmodifiableMap(new HashMap<>(builder.assemblyData)) :
                Collections.emptyMap();
        this.date = builder.date;
    }

    @Override
    public String getAccessionNumber() {
        return accessionNumber;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public String getKeywords() {
        return keywords;
    }

    @Override
    public List<String> getTaxonomy() {
        return taxonomy;
    }

    @Override
    public Map<String, String> getDbLinks() {
        return dbLinks;
    }

    @Override
    public List<ReferenceInfo> getReferences() {
        return references;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public Map<String, String> getAssemblyData() {
        return assemblyData;
    }

    @Override
    public String getDate() {
        return date;
    }

    /**
     * Builder implementation for DefaultHeaderInfo.
     */
    static class Builder implements HeaderInfo.Builder {
        private String accessionNumber;
        private String version;
        private String definition;
        private String keywords;
        private List<String> taxonomy;
        private Map<String, String> dbLinks;
        private List<ReferenceInfo> references;
        private String comment;
        private Map<String, String> assemblyData = new HashMap<>();
        private String date;

        @Override
        public Builder accessionNumber(String accessionNumber) {
            this.accessionNumber = accessionNumber;
            return this;
        }

        @Override
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        @Override
        public Builder definition(String definition) {
            this.definition = definition;
            return this;
        }

        @Override
        public Builder keywords(String keywords) {
            this.keywords = keywords;
            return this;
        }

        @Override
        public Builder taxonomy(List<String> taxonomy) {
            this.taxonomy = taxonomy;
            return this;
        }

        @Override
        public Builder dbLinks(Map<String, String> dbLinks) {
            this.dbLinks = dbLinks;
            return this;
        }

        @Override
        public Builder references(List<ReferenceInfo> references) {
            this.references = references;
            return this;
        }

        @Override
        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public Builder assemblyData(Map<String, String> assemblyData) {
            this.assemblyData = assemblyData;
            return this;
        }

        @Override
        public Builder date(String date) {
            this.date = date;
            return this;
        }

        @Override
        public HeaderInfo build() {
            return new DefaultHeaderInfo(this);
        }
    }
}
