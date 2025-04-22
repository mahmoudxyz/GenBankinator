package xyz.mahmoudahmed.examples;


import xyz.mahmoudahmed.parsers.AnnotationParser;
import xyz.mahmoudahmed.converter.GenbankConverter;
import xyz.mahmoudahmed.parsers.SequenceParser;
import xyz.mahmoudahmed.parsers.DefaultAnnotationParser;
import xyz.mahmoudahmed.parsers.DefaultSequenceParser;
import xyz.mahmoudahmed.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Example of streaming the output to avoid memory issues with very large files.
 */
public class StreamingConversionExample {
    public static void main(String[] args) {
        try {
            // Create the converter with memory-efficient settings
            GenbankConverter converter = GenbankConverter.memoryEfficient();

            // Define basic conversion options
            ConversionOptions options = ConversionOptions.builder()
                    .organism("Homo sapiens")
                    .moleculeType("DNA")
                    .topology("linear")
                    .division("VRT")
                    .annotationFormat("FASTA")
                    .build();

            // Create an output stream
            try (FileOutputStream outputStream = new FileOutputStream("streaming_output.gb")) {
                // Parse files first to get data
                File sequenceFile = new File("src/main/java/xyz/mahmoudahmed/examples/str/3.fasta");
                File annotationFile = new File("src/main/java/xyz/mahmoudahmed/examples/str/3_ann.fasta");

                // Before starting, validate the compatibility
                ValidationResult validation = converter.validate(sequenceFile, annotationFile);
                if (!validation.isValid()) {
                    System.err.println("Validation failed:");
                    for (ValidationIssue issue : validation.getIssues()) {
                        System.err.println(issue.getType() + ": " + issue.getMessage());
                    }
                    return;
                }

                // Parse metadata only to save memory
                SequenceParser parser = new DefaultSequenceParser();
                SequenceData sequenceData = parser.parseMetadataOnly(sequenceFile);

                // Parse annotations
                AnnotationParser annParser = new DefaultAnnotationParser();
                AnnotationData annotationData = annParser.parse(annotationFile);

                // Stream the conversion directly to the output file
                converter.convertToStream(sequenceData, annotationData, outputStream, options);
            }

            System.out.println("Streaming conversion complete!");

        } catch (IOException e) {
            System.err.println("Error during streaming conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
