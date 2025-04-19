package xyz.mahmoudahmed.model;

/**
 * Default implementation of GenbankOptions.
 */
public class DefaultGenbankOptions implements GenbankOptions {
    private final String defaultOrganism;
    private final String defaultMoleculeType;
    private final String defaultTopology;
    private final String defaultDivision;
    private final boolean memoryEfficient;
    private final long memoryThreshold;
    private final String tempDirectory;

    private DefaultGenbankOptions(Builder builder) {
        this.defaultOrganism = builder.defaultOrganism;
        this.defaultMoleculeType = builder.defaultMoleculeType;
        this.defaultTopology = builder.defaultTopology;
        this.defaultDivision = builder.defaultDivision;
        this.memoryEfficient = builder.memoryEfficient;
        this.memoryThreshold = builder.memoryThreshold;
        this.tempDirectory = builder.tempDirectory;
    }

    @Override
    public String getDefaultOrganism() {
        return defaultOrganism;
    }

    @Override
    public String getDefaultMoleculeType() {
        return defaultMoleculeType;
    }

    @Override
    public String getDefaultTopology() {
        return defaultTopology;
    }

    @Override
    public String getDefaultDivision() {
        return defaultDivision;
    }

    @Override
    public boolean isMemoryEfficient() {
        return memoryEfficient;
    }

    @Override
    public long getMemoryThreshold() {
        return memoryThreshold;
    }

    @Override
    public String getTempDirectory() {
        return tempDirectory;
    }

    /**
     * Builder implementation for DefaultGenbankOptions.
     */
    static class Builder implements GenbankOptions.Builder {
        private String defaultOrganism = "Unknown organism";
        private String defaultMoleculeType = "DNA";
        private String defaultTopology = "linear";
        private String defaultDivision = "UNC";
        private boolean memoryEfficient = false;
        private long memoryThreshold = 10 * 1024 * 1024; // 10MB
        private String tempDirectory = System.getProperty("java.io.tmpdir");

        @Override
        public Builder defaultOrganism(String defaultOrganism) {
            this.defaultOrganism = defaultOrganism;
            return this;
        }

        @Override
        public Builder defaultMoleculeType(String defaultMoleculeType) {
            this.defaultMoleculeType = defaultMoleculeType;
            return this;
        }

        @Override
        public Builder defaultTopology(String defaultTopology) {
            this.defaultTopology = defaultTopology;
            return this;
        }

        @Override
        public Builder defaultDivision(String defaultDivision) {
            this.defaultDivision = defaultDivision;
            return this;
        }

        @Override
        public Builder memoryEfficient(boolean memoryEfficient) {
            this.memoryEfficient = memoryEfficient;
            return this;
        }

        @Override
        public Builder memoryThreshold(long memoryThreshold) {
            this.memoryThreshold = memoryThreshold;
            return this;
        }

        @Override
        public Builder tempDirectory(String tempDirectory) {
            this.tempDirectory = tempDirectory;
            return this;
        }

        @Override
        public GenbankOptions build() {
            return new DefaultGenbankOptions(this);
        }
    }
}