package xyz.mahmoudahmed.examples;

import xyz.mahmoudahmed.api.GenbankConverter;
import xyz.mahmoudahmed.model.*;
import xyz.mahmoudahmed.parsers.FastaAnnotationParser;
import xyz.mahmoudahmed.parsers.NCBICompatibleSequenceParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Demonstrates different ways to use the GenBank converter.
 */
public class BasicConversionExample {
    public static void main(String[] args) {
        // Define input file paths
        File sequenceFile = new File("src/main/java/xyz/mahmoudahmed/examples/str/2.fasta");
        File annotationFile = new File("src/main/java/xyz/mahmoudahmed/examples/str/2_ann.fasta");
        try {
            // Run different examples
            basicConversion(sequenceFile, annotationFile);
            conversionWithHeaderInfo(sequenceFile, annotationFile);
            conversionWithCustomFormatting(sequenceFile, annotationFile);
            largeFileConversion(sequenceFile, annotationFile);
            fixedGenbankFormatting(sequenceFile, annotationFile);


            System.out.println("All examples completed successfully!");
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Basic conversion with minimal options.
     */
    private static void basicConversion(File sequenceFile, File annotationFile) throws IOException {
        System.out.println("\n--- Running Basic Conversion Example ---");

        // Create the converter with default settings
        GenbankConverter converter = GenbankConverter.builder()
                .withAnnotationParser(new FastaAnnotationParser())
                .withSequenceParser(new NCBICompatibleSequenceParser())
                .build();

        // Define basic conversion options
        ConversionOptions options = ConversionOptions.builder()
                .organism("Stephanocoenia intersepta")
                .moleculeType("DNA")
                .topology("circular")
                .division("SINT")
                .annotationFormat("FASTA")
                .build();

        // Convert to GenBank format
        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

        // Write the result to a file
        File outputFile = new File("output_basic.gb");
        result.writeToFile(outputFile);

        System.out.println("Basic conversion complete!");
        System.out.println("Processed " + result.getSequenceCount() + " sequences");
        System.out.println("Processed " + result.getFeatureCount() + " features");
        System.out.println("Output file: " + outputFile.getAbsolutePath());
        System.out.println("File size: " + outputFile.length() + " bytes");

        // Print a small sample of the output
        printFileSample(outputFile, 10);
    }

    /**
     * Conversion with HeaderInfo specified.
     */
    private static void conversionWithHeaderInfo(File sequenceFile, File annotationFile) throws IOException {
        System.out.println("\n--- Running Conversion with HeaderInfo Example ---");

        // Create the converter
        GenbankConverter converter = GenbankConverter.builder()
                .withAnnotationParser(new FastaAnnotationParser())
                .withSequenceParser(new NCBICompatibleSequenceParser())
                .build();

        // Create reference information
        List<ReferenceInfo> references = new ArrayList<>();
        ReferenceInfo reference = ReferenceInfo.builder()
                .number(1)
                .authors(Arrays.asList("Doe, J.", "Smith, A.", "Johnson, B."))
                .title("Complete mitochondrial genome of Centrophorus granulosus")
                .journal("Journal of Molecular Biology, 45(3): 123-145")
                .pubStatus("Unpublished")
                .build();
        references.add(reference);

        // Create a HeaderInfo object
        HeaderInfo headerInfo = HeaderInfo.builder()
                .definition("Centrophorus granulosus voucher XXXX mitochondrion, complete genome")
                .keywords("mitochondrion; complete genome")
                .taxonomy(Arrays.asList("Eukaryota", "Metazoa", "Chordata", "Craniata", "Vertebrata",
                        "Chondrichthyes", "Elasmobranchii", "Squaliformes", "Centrophoridae", "Centrophorus"))
                .dbLinks(Map.of("BioProject", "PRJNA123456", "BioSample", "SAMN12345678"))
                .references(references)
                .comment("This sequence was generated for demonstration purposes.")
                .assemblyData(Map.of(
                        "Assembly Method", "getOrganelle v1.7.6.1",
                        "Sequencing Technology", "Illumina",
                        "Coverage", "100x"
                ))
                .build();

        // Define conversion options with HeaderInfo
        ConversionOptions options = ConversionOptions.builder()
                .organism("Centrophorus granulosus")
                .moleculeType("DNA")
                .topology("circular")
                .division("VRT")
                .annotationFormat("FASTA")
                .headerInfo(headerInfo)  // Set the HeaderInfo object
                .build();

        // Convert to GenBank format
        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

        // Write the result to a file
        File outputFile = new File("output_with_header_info.gb");
        result.writeToFile(outputFile);

        System.out.println("Conversion with HeaderInfo complete!");
        System.out.println("Processed " + result.getSequenceCount() + " sequences");
        System.out.println("Processed " + result.getFeatureCount() + " features");
        System.out.println("Output file: " + outputFile.getAbsolutePath());

        // Print a small sample of the output
        printFileSample(outputFile, 20);
    }

    /**
     * Conversion with custom formatting options.
     */
    private static void conversionWithCustomFormatting(File sequenceFile, File annotationFile) throws IOException {
        System.out.println("\n--- Running Conversion with Custom Formatting Example ---");

        // Create the converter
        GenbankConverter converter = GenbankConverter.builder()
                .withAnnotationParser(new FastaAnnotationParser())
                .withSequenceParser(new NCBICompatibleSequenceParser())
                .build();

        // Define output formatting options
        OutputFormattingOptions formattingOptions = OutputFormattingOptions.builder()
                .lowercaseSequence(true)
                .sequenceLineWidth(60)
                .sortFeaturesByPosition(true)
                .includeEmptyLinesBetweenFeatures(true)
                .build();

        // Define feature formatting options
        FeatureFormattingOptions featureOptions = FeatureFormattingOptions.builder()
                .standardizeFeatureTypes(true)
                .includePseudoQualifier(true)
                .build();

        // Define conversion options with formatting options
        ConversionOptions options = ConversionOptions.builder()
                .organism("Centrophorus granulosus")
                .moleculeType("DNA")
                .topology("circular")
                .division("VRT")
                .annotationFormat("FASTA")
                .outputFormattingOptions(formattingOptions)
                .featureFormattingOptions(featureOptions)
                .build();

        // Convert to GenBank format
        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

        // Write the result to a file
        File outputFile = new File("output_custom_formatting.gb");
        result.writeToFile(outputFile);

        System.out.println("Conversion with custom formatting complete!");
        System.out.println("Processed " + result.getSequenceCount() + " sequences");
        System.out.println("Processed " + result.getFeatureCount() + " features");
        System.out.println("Output file: " + outputFile.getAbsolutePath());
    }

    /**
     * Conversion with large file handling options.
     */
    private static void largeFileConversion(File sequenceFile, File annotationFile) throws IOException {
        System.out.println("\n--- Running Large File Conversion Example ---");

        // Create options for memory-efficient processing
        GenbankOptions genbankOptions = GenbankOptions.builder()
                .memoryEfficient(true)
                .memoryThreshold(1024 * 1024) // 1MB threshold for demonstration
                .build();

        // Create the converter with memory-efficient options
        GenbankConverter converter = GenbankConverter.builder()
                .withAnnotationParser(new FastaAnnotationParser())
                .withSequenceParser(new NCBICompatibleSequenceParser())
                .withOptions(genbankOptions)
                .build();

        // Define conversion options
        ConversionOptions options = ConversionOptions.builder()
                .organism("Centrophorus granulosus")
                .moleculeType("DNA")
                .topology("circular")
                .division("VRT")
                .annotationFormat("FASTA")
                .build();

        // Convert to GenBank format
        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

        // Write the result to a file
        File outputFile = new File("output_large_file.gb");
        result.writeToFile(outputFile);

        System.out.println("Large file conversion complete!");
        System.out.println("Processed " + result.getSequenceCount() + " sequences");
        System.out.println("Processed " + result.getFeatureCount() + " features");
        System.out.println("Output file: " + outputFile.getAbsolutePath());
    }

    /**
     * Helper method to print the first n lines of a file.
     */
    private static void printFileSample(File file, int lines) throws IOException {
        System.out.println("\nSample of generated file (first " + lines + " lines):");
        System.out.println("-------------------------------------------");

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < lines) {
                System.out.println(line);
                count++;
            }
        }

        System.out.println("-------------------------------------------");
    }

    /**
     * Demonstrates fixing GenBank format issues with standardized gene names
     * and proper feature organization.
     */
    private static void fixedGenbankFormatting(File sequenceFile, File annotationFile) throws IOException {
        System.out.println("\n--- Running Fixed GenBank Formatting Example ---");

        // Create the converter
        GenbankConverter converter = GenbankConverter.builder()
                .withAnnotationParser(new FastaAnnotationParser())
                .withSequenceParser(new NCBICompatibleSequenceParser())
                .build();

        // Create formatting options with gene name standardization enabled
        FeatureFormattingOptions featureOptions = FeatureFormattingOptions.builder()
                .standardizeFeatureTypes(true)
                .includePseudoQualifier(true)
                .build();

        // Configure output formatting for better readability
        OutputFormattingOptions outputOptions = OutputFormattingOptions.builder()
                .sequenceLineWidth(60)
                .sortFeaturesByPosition(true)
                .includeEmptyLinesBetweenFeatures(true)
                .build();

        // Define conversion options
        ConversionOptions options = ConversionOptions.builder()
                .organism("Stephanocoenia intersepta")
                .moleculeType("DNA")
                .topology("circular")
                .division("INV")
                .annotationFormat("FASTA")
                .featureFormattingOptions(featureOptions)
                .outputFormattingOptions(outputOptions)
                .build();

        // Convert to GenBank format
        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

        // Write the result to a file
        File outputFile = new File("output_fixed_genbank_format.gb");
        result.writeToFile(outputFile);

        System.out.println("Fixed GenBank formatting complete!");
        System.out.println("Processed " + result.getSequenceCount() + " sequences");
        System.out.println("Processed " + result.getFeatureCount() + " features");
        System.out.println("Output file: " + outputFile.getAbsolutePath());

        // Print a sample of the output
        printFileSample(outputFile, 30);
    }
}