package xyz.mahmoudahmed.core;

import xyz.mahmoudahmed.api.*;
import xyz.mahmoudahmed.exception.ValidationException;
import xyz.mahmoudahmed.model.*;
import xyz.mahmoudahmed.parsers.FastaAnnotationParser;
import xyz.mahmoudahmed.util.FileSequenceStreamProvider;
import xyz.mahmoudahmed.util.SequenceStreamProvider;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Default implementation of GenbankConverter.
 */
public class DefaultGenbankConverter implements GenbankConverter {
    private final SequenceParser sequenceParser;
    private final AnnotationParser annotationParser;
    private final GenbankValidator validator;
    private final GenbankFormatter formatter;
    private final GenbankOptions options;

    private DefaultGenbankConverter(Builder builder) {
        this.sequenceParser = builder.sequenceParser;
        this.annotationParser = builder.annotationParser;
        this.validator = builder.validator;
        this.formatter = builder.formatter;
        this.options = builder.options;
    }

    @Override
    public GenbankResult convert(File sequenceFile, File annotationFile, ConversionOptions options) throws IOException {
        // Validate the files
        ValidationResult validationResult = validator.validateCompatibility(sequenceFile, annotationFile);
        if (!validationResult.isValid()) {
            throw new ValidationException("Validation failed: " + validationResult.getSummary());
        }

        // Pass the conversion options to the annotation parser if it supports it
        if (annotationParser instanceof FastaAnnotationParser) {
            ((FastaAnnotationParser) annotationParser).setConversionOptions(options);
        }

        // Rest of the method remains the same...
        if (this.options.isMemoryEfficient() || sequenceFile.length() > this.options.getMemoryThreshold()) {
            return convertLargeFiles(sequenceFile, annotationFile, options);
        } else {
            return convertStandard(sequenceFile, annotationFile, options);
        }
    }

    @Override
    public GenbankResult convert(SequenceData sequenceData, AnnotationData annotationData, ConversionOptions options) {
        // Handle sequence merging if needed
        if (options.isMergeSequences() && sequenceData.getCount() > 1) {
            sequenceData = mergeSequences(sequenceData, options);
        }

        // Format the data
        byte[] genbankData = formatter.format(sequenceData, annotationData, options);

        // Build the result
        return GenbankResult.builder()
                .genbankData(genbankData)
                .sequenceCount(sequenceData.getCount())
                .featureCount(annotationData.getTotalCount())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public void convertToStream(SequenceData sequenceData, AnnotationData annotationData,
                                OutputStream outputStream, ConversionOptions options) throws IOException {
        // Handle sequence merging if needed
        if (options.isMergeSequences() && sequenceData.getCount() > 1) {
            sequenceData = mergeSequences(sequenceData, options);
        }

        formatter.formatToStream(sequenceData, annotationData, outputStream, options);
    }

    @Override
    public ValidationResult validate(File sequenceFile, File annotationFile) throws IOException {
        return validator.validateCompatibility(sequenceFile, annotationFile);
    }

    /**
     * Standard conversion for regular-sized files.
     */
    private GenbankResult convertStandard(File sequenceFile, File annotationFile, ConversionOptions options) throws IOException {
        // Parse the files
        SequenceData sequenceData = sequenceParser.parse(sequenceFile);
        AnnotationData annotationData = annotationParser.parse(annotationFile);

        System.out.println();
        // Handle sequence merging if needed
        if (options.isMergeSequences() && sequenceData.getCount() > 1) {
            sequenceData = mergeSequences(sequenceData, options);
        }

        // Format the data
        byte[] genbankData = formatter.format(sequenceData, annotationData, options);

        // Build the result
        return GenbankResult.builder()
                .genbankData(genbankData)
                .sequenceCount(sequenceData.getCount())
                .featureCount(annotationData.getTotalCount())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Memory-efficient conversion for large files.
     */
    private GenbankResult convertLargeFiles(File sequenceFile, File annotationFile, ConversionOptions options) throws IOException {
        // Parse only metadata from sequence file to save memory
        SequenceData metadataOnly = sequenceParser.parseMetadataOnly(sequenceFile);
        AnnotationData annotationData = annotationParser.parse(annotationFile);

        // Handle sequence merging if needed
        if (options.isMergeSequences() && metadataOnly.getCount() > 1) {
            metadataOnly = mergeSequences(metadataOnly, options);
        }

        // Create a temporary file for the output
        File outputFile = File.createTempFile("genbank_", ".gb");
        outputFile.deleteOnExit();

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            // Stream the conversion
            SequenceStreamProvider streamProvider = new FileSequenceStreamProvider(sequenceFile);

            // Use the formatter to write the data directly to the stream
            StreamingGenbankFormatter streamingFormatter = new StreamingGenbankFormatter(streamProvider);
            streamingFormatter.formatToStream(metadataOnly, annotationData, outputStream, options);
        }

        // Read the generated file
        byte[] genbankData = new byte[(int) outputFile.length()];
        try (FileInputStream fis = new FileInputStream(outputFile)) {
            if (fis.read(genbankData) != outputFile.length()) {
                throw new IOException("Failed to read the entire GenBank file");
            }
        } finally {
            outputFile.delete();
        }

        // Build the result
        return GenbankResult.builder()
                .genbankData(genbankData)
                .sequenceCount(metadataOnly.getCount())
                .featureCount(annotationData.getTotalCount())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Merges multiple sequences into a single sequence.
     *
     * @param sequenceData The original sequence data
     * @param options Conversion options
     * @return A new SequenceData with the merged sequence
     */
    private SequenceData mergeSequences(SequenceData sequenceData, ConversionOptions options) {
        List<Sequence> originalSequences = sequenceData.getSequences();
        if (originalSequences.isEmpty()) {
            return sequenceData;
        }

        // Get the merged sequence properties
        String mergedId = "merged_" + originalSequences.get(0).getId();
        String mergedName = "Merged";
        String mergedDescription = "Merged sequence containing " + originalSequences.size() + " original sequences";

        // Build the merged sequence content
        StringBuilder mergedSequenceBuilder = new StringBuilder();
        for (Sequence seq : originalSequences) {
            String sequenceContent = seq.getSequence();
            if (sequenceContent != null) {
                mergedSequenceBuilder.append(sequenceContent);
            }
        }

        // Get HeaderInfo from options or first sequence
        HeaderInfo headerInfo = options.getHeaderInfo();
        if (headerInfo == null && !originalSequences.isEmpty()) {
            headerInfo = originalSequences.get(0).getHeaderInfo();
        }

        // Create the merged sequence with options-provided properties if available
        Sequence.Builder builder = Sequence.builder()
                .id(mergedId)
                .name(mergedName)
                .description(mergedDescription)
                .sequence(mergedSequenceBuilder.toString());

        if (options.getMoleculeType() != null && !options.getMoleculeType().isEmpty()) {
            builder.moleculeType(options.getMoleculeType());
        } else {
            builder.moleculeType("DNA");
        }

        if (options.getTopology() != null && !options.getTopology().isEmpty()) {
            builder.topology(options.getTopology());
        } else {
            builder.topology("linear");
        }

        if (options.getOrganism() != null && !options.getOrganism().isEmpty()) {
            builder.organism(options.getOrganism());
        } else if (!originalSequences.isEmpty() && originalSequences.get(0).getOrganism() != null) {
            builder.organism(originalSequences.get(0).getOrganism());
        } else {
            builder.organism("Merged organism");
        }

        if (headerInfo != null) {
            builder.headerInfo(headerInfo);
        }

        Sequence mergedSequence = builder.build();

        // Create new SequenceData with the merged sequence
        return SequenceData.builder()
                .addSequence(mergedSequence)
                .build();
    }

    /**
     * Builder for DefaultGenbankConverter.
     */
    public static class Builder implements GenbankConverterBuilder {
        private SequenceParser sequenceParser;
        private AnnotationParser annotationParser;
        private GenbankValidator validator;
        private GenbankFormatter formatter;
        private GenbankOptions options;

        @Override
        public GenbankConverterBuilder withSequenceParser(SequenceParser parser) {
            this.sequenceParser = parser;
            return this;
        }

        @Override
        public GenbankConverterBuilder withAnnotationParser(AnnotationParser parser) {
            this.annotationParser = parser;
            return this;
        }

        @Override
        public GenbankConverterBuilder withValidator(GenbankValidator validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public GenbankConverterBuilder withFormatter(GenbankFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        @Override
        public GenbankConverterBuilder withOptions(GenbankOptions options) {
            this.options = options;
            return this;
        }

        @Override
        public GenbankConverter build() {
            // Use defaults if not specified
            if (sequenceParser == null) {
                sequenceParser = new DefaultSequenceParser();
            }

            if (annotationParser == null) {
                annotationParser = new DefaultAnnotationParser();
            }

            if (validator == null) {
                validator = new DefaultGenbankValidator();
            }

            if (formatter == null) {
                formatter = new DefaultGenbankFormatter();
            }

            if (options == null) {
                options = GenbankOptions.builder().build();
            }

            return new DefaultGenbankConverter(this);
        }
    }

    @Override
    public ValidationResult validateSequence(File sequenceFile) throws IOException {
        return validator.validateSequence(sequenceFile);
    }

    @Override
    public ValidationResult validateAnnotation(File annotationFile, String format) throws IOException {
        return validator.validateAnnotation(annotationFile, format);
    }
}