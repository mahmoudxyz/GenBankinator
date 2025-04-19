package xyz.mahmoudahmed.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * Default implementation of GenbankResult.
 */
class DefaultGenbankResult implements GenbankResult {
    private final byte[] genbankData;
    private final int sequenceCount;
    private final int featureCount;
    private final LocalDateTime timestamp;

    private DefaultGenbankResult(Builder builder) {
        this.genbankData = builder.genbankData;
        this.sequenceCount = builder.sequenceCount;
        this.featureCount = builder.featureCount;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
    }

    @Override
    public byte[] getGenbankData() {
        return genbankData;
    }

    @Override
    public void writeToFile(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(genbankData);
        }
    }

    @Override
    public void writeToStream(OutputStream outputStream) throws IOException {
        outputStream.write(genbankData);
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
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    static class Builder implements GenbankResult.Builder {
        private byte[] genbankData;
        private int sequenceCount;
        private int featureCount;
        private LocalDateTime timestamp;

        @Override
        public Builder genbankData(byte[] genbankData) {
            this.genbankData = genbankData;
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
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        @Override
        public GenbankResult build() {
            return new DefaultGenbankResult(this);
        }
    }
}