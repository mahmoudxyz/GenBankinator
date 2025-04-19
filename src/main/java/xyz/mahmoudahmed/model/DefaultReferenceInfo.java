package xyz.mahmoudahmed.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of ReferenceInfo.
 */
public class DefaultReferenceInfo implements ReferenceInfo {
    private final Integer number;
    private final String baseRange;
    private final List<String> authors;
    private final String title;
    private final String journal;
    private final String pubStatus;

    private DefaultReferenceInfo(Builder builder) {
        this.number = builder.number;
        this.baseRange = builder.baseRange;
        this.authors = builder.authors != null ?
                Collections.unmodifiableList(new ArrayList<>(builder.authors)) :
                Collections.emptyList();
        this.title = builder.title;
        this.journal = builder.journal;
        this.pubStatus = builder.pubStatus;
    }

    @Override
    public Integer getNumber() {
        return number;
    }

    @Override
    public String getBaseRange() {
        return baseRange;
    }

    @Override
    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getJournal() {
        return journal;
    }

    @Override
    public String getPubStatus() {
        return pubStatus;
    }

    /**
     * Builder implementation for DefaultReferenceInfo.
     */
    static class Builder implements ReferenceInfo.Builder {
        private Integer number;
        private String baseRange;
        private List<String> authors;
        private String title;
        private String journal;
        private String pubStatus;

        @Override
        public Builder number(Integer number) {
            this.number = number;
            return this;
        }

        @Override
        public Builder baseRange(String baseRange) {
            this.baseRange = baseRange;
            return this;
        }

        @Override
        public Builder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        @Override
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        @Override
        public Builder journal(String journal) {
            this.journal = journal;
            return this;
        }

        @Override
        public Builder pubStatus(String pubStatus) {
            this.pubStatus = pubStatus;
            return this;
        }

        @Override
        public ReferenceInfo build() {
            return new DefaultReferenceInfo(this);
        }
    }
}
