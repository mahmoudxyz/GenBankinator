package xyz.mahmoudahmed.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of FeatureFilterOptions.
 */
public class DefaultFeatureFilterOptions implements FeatureFilterOptions {
    private final List<String> includeFeatureTypes;
    private final List<String> excludeFeatureTypes;
    private final List<String> includeQualifiers;
    private final List<String> excludeQualifiers;
    private final Integer minFeatureLength;
    private final Integer maxFeatureLength;

    private DefaultFeatureFilterOptions(Builder builder) {
        this.includeFeatureTypes = builder.includeFeatureTypes != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.includeFeatureTypes)) :
                Collections.emptyList();
        this.excludeFeatureTypes = builder.excludeFeatureTypes != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.excludeFeatureTypes)) :
                Collections.emptyList();
        this.includeQualifiers = builder.includeQualifiers != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.includeQualifiers)) :
                Collections.emptyList();
        this.excludeQualifiers = builder.excludeQualifiers != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.excludeQualifiers)) :
                Collections.emptyList();
        this.minFeatureLength = builder.minFeatureLength;
        this.maxFeatureLength = builder.maxFeatureLength;
    }

    @Override
    public List<String> getIncludeFeatureTypes() {
        return includeFeatureTypes;
    }

    @Override
    public List<String> getExcludeFeatureTypes() {
        return excludeFeatureTypes;
    }

    @Override
    public List<String> getIncludeQualifiers() {
        return includeQualifiers;
    }

    @Override
    public List<String> getExcludeQualifiers() {
        return excludeQualifiers;
    }

    @Override
    public Integer getMinFeatureLength() {
        return minFeatureLength;
    }

    @Override
    public Integer getMaxFeatureLength() {
        return maxFeatureLength;
    }

    /**
     * Builder implementation for DefaultFeatureFilterOptions.
     */
    static class Builder implements FeatureFilterOptions.Builder {
        private List<String> includeFeatureTypes;
        private List<String> excludeFeatureTypes;
        private List<String> includeQualifiers;
        private List<String> excludeQualifiers;
        private Integer minFeatureLength;
        private Integer maxFeatureLength;

        @Override
        public Builder includeFeatureTypes(List<String> includeFeatureTypes) {
            this.includeFeatureTypes = includeFeatureTypes;
            return this;
        }

        @Override
        public Builder excludeFeatureTypes(List<String> excludeFeatureTypes) {
            this.excludeFeatureTypes = excludeFeatureTypes;
            return this;
        }

        @Override
        public Builder includeQualifiers(List<String> includeQualifiers) {
            this.includeQualifiers = includeQualifiers;
            return this;
        }

        @Override
        public Builder excludeQualifiers(List<String> excludeQualifiers) {
            this.excludeQualifiers = excludeQualifiers;
            return this;
        }

        @Override
        public Builder minFeatureLength(Integer minFeatureLength) {
            this.minFeatureLength = minFeatureLength;
            return this;
        }

        @Override
        public Builder maxFeatureLength(Integer maxFeatureLength) {
            this.maxFeatureLength = maxFeatureLength;
            return this;
        }

        @Override
        public FeatureFilterOptions build() {
            return new DefaultFeatureFilterOptions(this);
        }
    }
}