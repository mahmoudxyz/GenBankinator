package xyz.mahmoudahmed.examples;

import xyz.mahmoudahmed.api.GenbankConverter;
import xyz.mahmoudahmed.model.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Advanced conversion example with custom options.
 */
public class AdvancedConversionExample {
    public static void main(String[] args) {
        try {
            // Create the converter with memory-efficient settings for large files
            GenbankConverter converter = GenbankConverter.memoryEfficient();

            // Define feature type mapping for standardization
            Map<String, String> featureTypeMapping = new HashMap<>();
            featureTypeMapping.put("cds", "CDS");
            featureTypeMapping.put("mrna", "mRNA");
            featureTypeMapping.put("gene", "gene");

            // Define additional qualifiers to add to specific feature types
            Map<String, Map<String, String>> additionalQualifiers = new HashMap<>();
            Map<String, String> cdsQualifiers = new HashMap<>();
            cdsQualifiers.put("transl_table", "2");
            cdsQualifiers.put("codon_start", "1");
            additionalQualifiers.put("CDS", cdsQualifiers);

            // Create comprehensive conversion options
            ConversionOptions options = ConversionOptions.builder()
                    .organism("Homo sapiens mitochondrion")
                    .moleculeType("DNA")
                    .topology("circular")
                    .division("VRT")
                    .annotationFormat("GFF")
                    // Configure translation options
                    .translationOptions(TranslationOptions.builder()
                            .autoTranslate(true)
                            .geneticCode("vertebrate_mitochondrial")
                            .forceTranslateFeatureTypes(List.of("exon"))
                            .skipTranslateFeatureTypes(List.of("pseudogene"))
                            .includeTranslTableQualifier(true)
                            .includeCodonStartQualifier(true)
                            .build())
                    // Configure feature formatting options
                    .featureFormattingOptions(FeatureFormattingOptions.builder()
                            .standardizeFeatureTypes(true)
                            .preserveOriginalIds(true)
                            .includePseudoQualifier(true)
                            .featureTypeMapping(featureTypeMapping)
                            .additionalQualifiers(additionalQualifiers)
                            .build())
                    // Configure output formatting options
                    .outputFormattingOptions(OutputFormattingOptions.builder()
                            .sequenceLineWidth(60)
                            .lowercaseSequence(false)
                            .includeSequence(true)
                            .includeEmptyLinesBetweenFeatures(false)
                            .sortFeaturesByPosition(true)
                            .build())
                    // Configure feature filtering options
                    .filterOptions(FeatureFilterOptions.builder()
                            .includeFeatureTypes(List.of("gene", "CDS", "tRNA", "rRNA"))
                            .excludeFeatureTypes(List.of("misc_feature"))
                            .minFeatureLength(10)
                            .build())
                    // Configure header information
                    .headerInfo(HeaderInfo.builder()
                            .accessionNumber("MT123456")
                            .version("MT123456.1")
                            .definition("Homo sapiens mitochondrion, complete genome")
                            .keywords("mitochondrion; complete genome")
                            .taxonomy(List.of("Eukaryota", "Metazoa", "Chordata", "Craniata",
                                    "Vertebrata", "Euteleostomi", "Mammalia",
                                    "Primates", "Hominidae", "Homo"))
                            .comment("This is a complete mitochondrial genome.")
                            .build())
                    .build();

            // Perform the conversion
            GenbankResult result = converter.convert(
                    new File("path/to/sequences.fasta"),
                    new File("path/to/annotations.gff"),
                    options);

            // Write the result to a file
            result.writeToFile(new File("output.gb"));

            System.out.println("Advanced conversion complete!");

        } catch (IOException e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}