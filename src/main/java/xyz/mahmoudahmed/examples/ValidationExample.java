package xyz.mahmoudahmed.examples;

import xyz.mahmoudahmed.converter.GenbankConverter;
import xyz.mahmoudahmed.model.ConversionOptions;
import xyz.mahmoudahmed.model.GenbankResult;
import xyz.mahmoudahmed.model.ValidationIssue;
import xyz.mahmoudahmed.model.ValidationResult;

import java.io.File;
import java.io.IOException;

/**
 * Example of validating files before conversion.
 */
public class ValidationExample {
    public static void main(String[] args) {
        try {
            // Create the converter
            GenbankConverter converter = GenbankConverter.standard();

            // Files to validate
            File sequenceFile = new File("path/to/sequences.fasta");
            File annotationFile = new File("path/to/annotations.gff");

            // Validate the sequence file
            ValidationResult seqValidation = converter.validateSequence(sequenceFile);
            System.out.println("Sequence validation: " + (seqValidation.isValid() ? "PASSED" : "FAILED"));
            System.out.println("- Format: " + seqValidation.getDetectedFormat());
            System.out.println("- Sequences: " + seqValidation.getSequenceCount());
            System.out.println("- Summary: " + seqValidation.getSummary());

            if (!seqValidation.getIssues().isEmpty()) {
                System.out.println("- Issues:");
                for (ValidationIssue issue : seqValidation.getIssues()) {
                    System.out.println("  * " + issue.getType() + ": " + issue.getMessage());
                }
            }

            // Validate the annotation file
            ValidationResult annValidation = converter.validateAnnotation(annotationFile, "GFF");
            System.out.println("\nAnnotation validation: " + (annValidation.isValid() ? "PASSED" : "FAILED"));
            System.out.println("- Format: " + annValidation.getDetectedFormat());
            System.out.println("- Sequences referenced: " + annValidation.getSequenceCount());
            System.out.println("- Features: " + annValidation.getFeatureCount());
            System.out.println("- Summary: " + annValidation.getSummary());

            if (!annValidation.getIssues().isEmpty()) {
                System.out.println("- Issues:");
                for (ValidationIssue issue : annValidation.getIssues()) {
                    System.out.println("  * " + issue.getType() + ": " + issue.getMessage());
                }
            }

            // Validate compatibility between files
            ValidationResult compatValidation = converter.validate(sequenceFile, annotationFile);
            System.out.println("\nCompatibility validation: " + (compatValidation.isValid() ? "PASSED" : "FAILED"));
            System.out.println("- Sequences: " + compatValidation.getSequenceCount());
            System.out.println("- Features: " + compatValidation.getFeatureCount());
            System.out.println("- Summary: " + compatValidation.getSummary());

            if (!compatValidation.getIssues().isEmpty()) {
                System.out.println("- Issues:");
                for (ValidationIssue issue : compatValidation.getIssues()) {
                    System.out.println("  * " + issue.getType() + ": " + issue.getMessage());
                }
            }

            // Only proceed with conversion if validation passes
            if (seqValidation.isValid() && annValidation.isValid() && compatValidation.isValid()) {
                System.out.println("\nAll validations passed, proceeding with conversion...");

                // Define conversion options
                ConversionOptions options = ConversionOptions.builder()
                        .organism("Homo sapiens")
                        .moleculeType("DNA")
                        .topology("linear")
                        .division("VRT")
                        .annotationFormat("GFF")
                        .build();

                // Convert to GenBank format
                GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

                // Write the result to a file
                result.writeToFile(new File("validated_output.gb"));

                System.out.println("Conversion complete!");
            } else {
                System.out.println("\nValidation failed, conversion aborted.");
            }

        } catch (IOException e) {
            System.err.println("Error during validation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}