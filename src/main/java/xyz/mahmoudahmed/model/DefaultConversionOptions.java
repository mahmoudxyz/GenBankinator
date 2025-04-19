package xyz.mahmoudahmed.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of ConversionOptions.
 */
public class DefaultConversionOptions implements ConversionOptions {
    private final String organism;
    private final String moleculeType;
    private final String topology;
    private final String division;
    private final boolean mergeSequences;
    private final String annotationFormat;
    private final TranslationOptions translationOptions;
    private final FeatureFormattingOptions featureFormattingOptions;
    private final OutputFormattingOptions outputFormattingOptions;
    private final Map<String, String> customMetadata;
    private final FeatureFilterOptions filterOptions;
    private final HeaderInfo headerInfo;

    private DefaultConversionOptions(Builder builder) {
        this.organism = builder.organism;
        this.moleculeType = builder.moleculeType;
        this.topology = builder.topology;
        this.division = builder.division;
        this.mergeSequences = builder.mergeSequences;
        this.annotationFormat = builder.annotationFormat;
        this.translationOptions = builder.translationOptions;
        this.featureFormattingOptions = builder.featureFormattingOptions;
        this.outputFormattingOptions = builder.outputFormattingOptions;
        this.customMetadata = builder.customMetadata != null ?
                Collections.unmodifiableMap(new HashMap<>(builder.customMetadata)) :
                Collections.emptyMap();
        this.filterOptions = builder.filterOptions;
        this.headerInfo = builder.headerInfo;
    }

    @Override
    public String getOrganism() {
        return organism;
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
    public boolean isMergeSequences() {
        return mergeSequences;
    }

    @Override
    public String getAnnotationFormat() {
        return annotationFormat;
    }

    @Override
    public TranslationOptions getTranslationOptions() {
        return translationOptions;
    }

    @Override
    public FeatureFormattingOptions getFeatureFormattingOptions() {
        return featureFormattingOptions;
    }

    @Override
    public OutputFormattingOptions getOutputFormattingOptions() {
        return outputFormattingOptions;
    }

    @Override
    public Map<String, String> getCustomMetadata() {
        return customMetadata;
    }

    @Override
    public FeatureFilterOptions getFilterOptions() {
        return filterOptions;
    }

    @Override
    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }

    /**
     * Builder implementation for DefaultConversionOptions.
     */
    static class Builder implements ConversionOptions.Builder {
        private String organism;
        private String moleculeType;
        private String topology;
        private String division;
        private boolean mergeSequences;
        private String annotationFormat;
        private TranslationOptions translationOptions = TranslationOptions.builder().build();
        private FeatureFormattingOptions featureFormattingOptions = FeatureFormattingOptions.builder().build();
        private OutputFormattingOptions outputFormattingOptions = OutputFormattingOptions.builder().build();
        private Map<String, String> customMetadata = new HashMap<>();
        private FeatureFilterOptions filterOptions = FeatureFilterOptions.builder().build();
        private HeaderInfo headerInfo;

        @Override
        public Builder from(ConversionOptions options) {
            if (options != null) {
                this.organism = options.getOrganism();
                this.moleculeType = options.getMoleculeType();
                this.topology = options.getTopology();
                this.division = options.getDivision();
                this.mergeSequences = options.isMergeSequences();
                this.annotationFormat = options.getAnnotationFormat();
                this.translationOptions = options.getTranslationOptions();
                this.featureFormattingOptions = options.getFeatureFormattingOptions();
                this.outputFormattingOptions = options.getOutputFormattingOptions();

                if (options.getCustomMetadata() != null) {
                    this.customMetadata = new HashMap<>(options.getCustomMetadata());
                }

                this.filterOptions = options.getFilterOptions();
                this.headerInfo = options.getHeaderInfo();
            }
            return this;
        }

        @Override
        public Builder organism(String organism) {
            this.organism = organism;
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
        public Builder mergeSequences(boolean mergeSequences) {
            this.mergeSequences = mergeSequences;
            return this;
        }

        @Override
        public Builder annotationFormat(String annotationFormat) {
            this.annotationFormat = annotationFormat;
            return this;
        }

        @Override
        public Builder translationOptions(TranslationOptions translationOptions) {
            this.translationOptions = translationOptions;
            return this;
        }

        @Override
        public Builder featureFormattingOptions(FeatureFormattingOptions featureFormattingOptions) {
            this.featureFormattingOptions = featureFormattingOptions;
            return this;
        }

        @Override
        public Builder outputFormattingOptions(OutputFormattingOptions outputFormattingOptions) {
            this.outputFormattingOptions = outputFormattingOptions;
            return this;
        }

        @Override
        public Builder customMetadata(Map<String, String> customMetadata) {
            this.customMetadata = customMetadata;
            return this;
        }

        @Override
        public Builder filterOptions(FeatureFilterOptions filterOptions) {
            this.filterOptions = filterOptions;
            return this;
        }

        @Override
        public Builder headerInfo(HeaderInfo headerInfo) {
            this.headerInfo = headerInfo;
            return this;
        }

        @Override
        public ConversionOptions build() {
            return new DefaultConversionOptions(this);
        }
    }
}