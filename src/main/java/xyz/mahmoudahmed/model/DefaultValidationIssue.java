package xyz.mahmoudahmed.model;

/**
 * Default implementation of ValidationIssue.
 */
class DefaultValidationIssue implements ValidationIssue {
    private final String type;
    private final String message;
    private final String location;

    private DefaultValidationIssue(Builder builder) {
        this.type = builder.type;
        this.message = builder.message;
        this.location = builder.location;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocation() {
        return location;
    }

    static class Builder implements ValidationIssue.Builder {
        private String type;
        private String message;
        private String location;

        @Override
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public Builder location(String location) {
            this.location = location;
            return this;
        }

        @Override
        public ValidationIssue build() {
            return new DefaultValidationIssue(this);
        }
    }
}