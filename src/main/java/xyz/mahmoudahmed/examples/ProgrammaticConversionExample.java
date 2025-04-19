package xyz.mahmoudahmed.examples;

import xyz.mahmoudahmed.api.GenbankConverter;
import xyz.mahmoudahmed.model.*;

import java.io.File;
import java.io.IOException;

/**
 * Example of programmatically creating sequence data and annotations.
 */
public class ProgrammaticConversionExample {
    public static void main(String[] args) {
        try {
            // Create the converter
            GenbankConverter converter = GenbankConverter.standard();

            // Create sequence data programmatically
            Sequence sequence = Sequence.builder()
                    .id("MT123456")
                    .name("MT123456")
                    .description("Homo sapiens mitochondrion, complete genome")
                    .sequence("ATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCAT")
                    .moleculeType("DNA")
                    .topology("circular")
                    .organism("Homo sapiens")
                    .build();

            SequenceData sequenceData = SequenceData.builder()
                    .addSequence(sequence)
                    .build();

            // Create annotations programmatically
            Annotation gene = Annotation.builder()
                    .type("gene")
                    .start(0)
                    .end(30)
                    .strand(1)
                    .sequenceId("MT123456")
                    .featureId("gene1")
                    .addQualifier("gene", "ND1")
                    .build();

            Annotation cds = Annotation.builder()
                    .type("CDS")
                    .start(0)
                    .end(30)
                    .strand(1)
                    .sequenceId("MT123456")
                    .featureId("cds1")
                    .addQualifier("gene", "ND1")
                    .addQualifier("product", "NADH dehydrogenase subunit 1")
                    .addQualifier("translation", "MAPMQLGPDWQIJKL")
                    .build();

            AnnotationData annotationData = AnnotationData.builder()
                    .addAnnotation(gene)
                    .addAnnotation(cds)
                    .build();

            // Define basic conversion options
            ConversionOptions options = ConversionOptions.builder()
                    .organism("Homo sapiens")
                    .moleculeType("DNA")
                    .topology("circular")
                    .division("VRT")
                    .build();

            // Convert the programmatically created data
            GenbankResult result = converter.convert(sequenceData, annotationData, options);

            // Write the result to a file
            result.writeToFile(new File("programmatic_output.gb"));

            System.out.println("Programmatic conversion complete!");

        } catch (IOException e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

