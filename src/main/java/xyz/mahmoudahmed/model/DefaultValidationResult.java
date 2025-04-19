package xyz.mahmoudahmed.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of ValidationResult.
 */
class DefaultValidationResult implements ValidationResult {
    private final boolean valid;
    private final String detectedFormat;
    private final int sequenceCount;
    private final int featureCount;
    private final List<ValidationIssue> issues;
    private final String summary;

    private DefaultValidationResult(Builder builder) {
        this.valid = builder.valid;
        this.detectedFormat = builder.detectedFormat;
        this.sequenceCount = builder.sequenceCount;
        this.featureCount = builder.featureCount;
        this.issues = Collections.unmodifiableList(new ArrayList<>(builder.issues));
        this.summary = builder.summary;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public String getDetectedFormat() {
        return detectedFormat;
    }

    @Override
    public int getSequenceCount() {
        return sequenceCount;
    }

    @Override
    public int getFeatureCount() {
        return featureCount;
    }

    @Override
    public List<ValidationIssue> getIssues() {
        return issues;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    static class Builder implements ValidationResult.Builder {
        private boolean valid;
        private String detectedFormat;
        private int sequenceCount;
        private int featureCount;
        private final List<ValidationIssue> issues = new ArrayList<>();
        private String summary;

        @Override
        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        @Override
        public Builder detectedFormat(String detectedFormat) {
            this.detectedFormat = detectedFormat;
            return this;
        }

        @Override
        public Builder sequenceCount(int sequenceCount) {
            this.sequenceCount = sequenceCount;
            return this;
        }

        @Override
        public Builder featureCount(int featureCount) {
            this.featureCount = featureCount;
            return this;
        }

        @Override
        public Builder issues(List<ValidationIssue> issues) {
            this.issues.clear();
            if (issues != null) {
                this.issues.addAll(issues);
            }
            return this;
        }

        @Override
        public Builder addIssue(ValidationIssue issue) {
            if (issue != null) {
                this.issues.add(issue);
            }
            return this;
        }

        @Override
        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        @Override
        public ValidationResult build() {
            return new DefaultValidationResult(this);
        }
    }

    @Override
    public String toString() {
        return "DefaultValidationResult{" +
                "valid=" + valid +
                ", detectedFormat='" + detectedFormat + '\'' +
                ", sequenceCount=" + sequenceCount +
                ", featureCount=" + featureCount +
                ", issues=" + issues +
                ", summary='" + summary + '\'' +
                '}';
    }
}