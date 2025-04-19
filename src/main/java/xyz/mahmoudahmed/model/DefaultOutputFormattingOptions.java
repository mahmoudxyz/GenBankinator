package xyz.mahmoudahmed.model;

/**
 * Default implementation of OutputFormattingOptions.
 */
public class DefaultOutputFormattingOptions implements OutputFormattingOptions {
    private final int sequenceLineWidth;
    private final boolean lowercaseSequence;
    private final boolean includeSequence;
    private final boolean includeEmptyLinesBetweenFeatures;
    private final boolean sortFeaturesByPosition;

    private DefaultOutputFormattingOptions(Builder builder) {
        this.sequenceLineWidth = builder.sequenceLineWidth;
        this.lowercaseSequence = builder.lowercaseSequence;
        this.includeSequence = builder.includeSequence;
        this.includeEmptyLinesBetweenFeatures = builder.includeEmptyLinesBetweenFeatures;
        this.sortFeaturesByPosition = builder.sortFeaturesByPosition;
    }

    @Override
    public int getSequenceLineWidth() {
        return sequenceLineWidth;
    }

    @Override
    public boolean isLowercaseSequence() {
        return lowercaseSequence;
    }

    @Override
    public boolean isIncludeSequence() {
        return includeSequence;
    }

    @Override
    public boolean isIncludeEmptyLinesBetweenFeatures() {
        return includeEmptyLinesBetweenFeatures;
    }

    @Override
    public boolean isSortFeaturesByPosition() {
        return sortFeaturesByPosition;
    }

    /**
     * Builder implementation for DefaultOutputFormattingOptions.
     */
    static class Builder implements OutputFormattingOptions.Builder {
        private int sequenceLineWidth = 60;
        private boolean lowercaseSequence = false;
        private boolean includeSequence = true;
        private boolean includeEmptyLinesBetweenFeatures = false;
        private boolean sortFeaturesByPosition = true;

        @Override
        public Builder sequenceLineWidth(int sequenceLineWidth) {
            this.sequenceLineWidth = sequenceLineWidth;
            return this;
        }

        @Override
        public Builder lowercaseSequence(boolean lowercaseSequence) {
            this.lowercaseSequence = lowercaseSequence;
            return this;
        }

        @Override
        public Builder includeSequence(boolean includeSequence) {
            this.includeSequence = includeSequence;
            return this;
        }

        @Override
        public Builder includeEmptyLinesBetweenFeatures(boolean includeEmptyLinesBetweenFeatures) {
            this.includeEmptyLinesBetweenFeatures = includeEmptyLinesBetweenFeatures;
            return this;
        }

        @Override
        public Builder sortFeaturesByPosition(boolean sortFeaturesByPosition) {
            this.sortFeaturesByPosition = sortFeaturesByPosition;
            return this;
        }

        @Override
        public OutputFormattingOptions build() {
            return new DefaultOutputFormattingOptions(this);
        }
    }
}