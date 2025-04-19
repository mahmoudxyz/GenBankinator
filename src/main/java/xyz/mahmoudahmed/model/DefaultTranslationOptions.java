package xyz.mahmoudahmed.model;

import java.util.*;

/**
 * Default implementation of TranslationOptions.
 */
public class DefaultTranslationOptions implements TranslationOptions {
    private final boolean autoTranslate;
    private final String geneticCode;
    private final List<String> forceTranslateFeatureTypes;
    private final List<String> skipTranslateFeatureTypes;
    private final boolean includeTranslTableQualifier;
    private final boolean includeCodonStartQualifier;

    private DefaultTranslationOptions(Builder builder) {
        this.autoTranslate = builder.autoTranslate;
        this.geneticCode = builder.geneticCode;
        this.forceTranslateFeatureTypes = builder.forceTranslateFeatureTypes != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.forceTranslateFeatureTypes)) :
                Collections.emptyList();
        this.skipTranslateFeatureTypes = builder.skipTranslateFeatureTypes != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.skipTranslateFeatureTypes)) :
                Collections.emptyList();
        this.includeTranslTableQualifier = builder.includeTranslTableQualifier;
        this.includeCodonStartQualifier = builder.includeCodonStartQualifier;
    }

    @Override
    public boolean isAutoTranslate() {
        return autoTranslate;
    }

    @Override
    public String getGeneticCode() {
        return geneticCode;
    }

    @Override
    public List<String> getForceTranslateFeatureTypes() {
        return forceTranslateFeatureTypes;
    }

    @Override
    public List<String> getSkipTranslateFeatureTypes() {
        return skipTranslateFeatureTypes;
    }

    @Override
    public boolean isIncludeTranslTableQualifier() {
        return includeTranslTableQualifier;
    }

    @Override
    public boolean isIncludeCodonStartQualifier() {
        return includeCodonStartQualifier;
    }

    /**
     * Builder implementation for DefaultTranslationOptions.
     */
    static class Builder implements TranslationOptions.Builder {
        private boolean autoTranslate = true;
        private String geneticCode = "vertebrate_mitochondrial";
        private List<String> forceTranslateFeatureTypes;
        private List<String> skipTranslateFeatureTypes;
        private boolean includeTranslTableQualifier = true;
        private boolean includeCodonStartQualifier = true;

        @Override
        public Builder autoTranslate(boolean autoTranslate) {
            this.autoTranslate = autoTranslate;
            return this;
        }

        @Override
        public Builder geneticCode(String geneticCode) {
            this.geneticCode = geneticCode;
            return this;
        }

        @Override
        public Builder forceTranslateFeatureTypes(List<String> forceTranslateFeatureTypes) {
            this.forceTranslateFeatureTypes = forceTranslateFeatureTypes;
            return this;
        }

        @Override
        public Builder skipTranslateFeatureTypes(List<String> skipTranslateFeatureTypes) {
            this.skipTranslateFeatureTypes = skipTranslateFeatureTypes;
            return this;
        }

        @Override
        public Builder includeTranslTableQualifier(boolean includeTranslTableQualifier) {
            this.includeTranslTableQualifier = includeTranslTableQualifier;
            return this;
        }

        @Override
        public Builder includeCodonStartQualifier(boolean includeCodonStartQualifier) {
            this.includeCodonStartQualifier = includeCodonStartQualifier;
            return this;
        }

        @Override
        public TranslationOptions build() {
            return new DefaultTranslationOptions(this);
        }
    }
}