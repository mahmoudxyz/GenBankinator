package xyz.mahmoudahmed.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of FeatureFormattingOptions.
 */
public class DefaultFeatureFormattingOptions implements FeatureFormattingOptions {
    private final boolean standardizeFeatureTypes;
    private final boolean preserveOriginalIds;
    private final boolean includePseudoQualifier;
    private final Map<String, String> featureTypeMapping;
    private final Map<String, Map<String, String>> additionalQualifiers;

    private DefaultFeatureFormattingOptions(Builder builder) {
        this.standardizeFeatureTypes = builder.standardizeFeatureTypes;
        this.preserveOriginalIds = builder.preserveOriginalIds;
        this.includePseudoQualifier = builder.includePseudoQualifier;
        this.featureTypeMapping = builder.featureTypeMapping != null ?
                Collections.unmodifiableMap(new HashMap<>(builder.featureTypeMapping)) :
                Collections.emptyMap();
        this.additionalQualifiers = builder.additionalQualifiers != null ?
                Collections.unmodifiableMap(deepCopyQualifiersMap(builder.additionalQualifiers)) :
                Collections.emptyMap();
    }

    private Map<String, Map<String, String>> deepCopyQualifiersMap(Map<String, Map<String, String>> original) {
        Map<String, Map<String, String>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

    @Override
    public boolean isStandardizeFeatureTypes() {
        return standardizeFeatureTypes;
    }

    @Override
    public boolean isPreserveOriginalIds() {
        return preserveOriginalIds;
    }

    @Override
    public boolean isIncludePseudoQualifier() {
        return includePseudoQualifier;
    }

    @Override
    public Map<String, String> getFeatureTypeMapping() {
        return featureTypeMapping;
    }

    @Override
    public Map<String, Map<String, String>> getAdditionalQualifiers() {
        return additionalQualifiers;
    }

    /**
     * Builder implementation for DefaultFeatureFormattingOptions.
     */
    static class Builder implements FeatureFormattingOptions.Builder {
        private boolean standardizeFeatureTypes = true;
        private boolean preserveOriginalIds = true;
        private boolean includePseudoQualifier = true;
        private Map<String, String> featureTypeMapping;
        private Map<String, Map<String, String>> additionalQualifiers;

        @Override
        public Builder standardizeFeatureTypes(boolean standardizeFeatureTypes) {
            this.standardizeFeatureTypes = standardizeFeatureTypes;
            return this;
        }

        @Override
        public Builder preserveOriginalIds(boolean preserveOriginalIds) {
            this.preserveOriginalIds = preserveOriginalIds;
            return this;
        }

        @Override
        public Builder includePseudoQualifier(boolean includePseudoQualifier) {
            this.includePseudoQualifier = includePseudoQualifier;
            return this;
        }

        @Override
        public Builder featureTypeMapping(Map<String, String> featureTypeMapping) {
            this.featureTypeMapping = featureTypeMapping;
            return this;
        }

        @Override
        public Builder additionalQualifiers(Map<String, Map<String, String>> additionalQualifiers) {
            this.additionalQualifiers = additionalQualifiers;
            return this;
        }

        @Override
        public FeatureFormattingOptions build() {
            return new DefaultFeatureFormattingOptions(this);
        }
    }
}
