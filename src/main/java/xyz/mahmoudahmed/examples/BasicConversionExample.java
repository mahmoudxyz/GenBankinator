package xyz.mahmoudahmed.examples;

import xyz.mahmoudahmed.converter.GenbankConverter;
import xyz.mahmoudahmed.model.*;
import xyz.mahmoudahmed.parsers.FastaAnnotationParser;
import xyz.mahmoudahmed.parsers.NCBICompatibleSequenceParser;

import java.io.*;
import java.util.*;

/**
 * Demonstrates different ways to use the GenBank converter.
 */
public class BasicConversionExample {
    public static void main(String[] args) {
        // Define input file paths
        File sequenceFile = new File("src/main/java/xyz/mahmoudahmed/examples/str/3.fasta");
        File annotationFile = new File("src/main/java/xyz/mahmoudahmed/examples/str/3_ann.fasta");
        try {
            conversionWithHeaderInfo(sequenceFile, annotationFile);

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
        }
    }


    /**
     * Conversion with HeaderInfo specified.
     */
    private static void conversionWithHeaderInfo(File sequenceFile, File annotationFile) throws IOException {

        GenbankOptions genbankOptions = GenbankOptions.builder().memoryEfficient(false).build();

        // Create the converter
        GenbankConverter converter = GenbankConverter.builder()
                .withAnnotationParser(new FastaAnnotationParser())
                .build();

        // Create reference information
        List<ReferenceInfo> references = new ArrayList<>();
        ReferenceInfo reference = ReferenceInfo.builder()
                .number(1)
                .authors(Arrays.asList(
                        "Colin, A.",
                        "Galvan-Tirado, C.",
                        "Carreon-Palau, L.",
                        "Bracken-Grissom, H.D.",
                        "Baeza, J.A."))
                .title("Mitochondrial genomes of the land hermit crab Coenobita clypeatus (Anomura: Paguroidea) and the mole crab Emerita talpoida (Anomura: Hippoidea) with insights into phylogenetic relationships in the Anomura (Crustacea: Decapoda)")
                .journal("Gene 849, 146896 (2022)")
                .pubStatus("Published")
                .build();
        references.add(reference);


        // Create a HeaderInfo object
        HeaderInfo headerInfo = HeaderInfo.builder()
                .definition("Emerita talpoida mitochondrion, complete genome.")
                .accessionNumber("NC_067557")
                .version("NC_067557.1")
                .keywords("RefSeq.")
                .taxonomy(Arrays.asList(
                        "Eukaryota",
                        "Metazoa",
                        "Ecdysozoa",
                        "Arthropoda",
                        "Crustacea",
                        "Multicrustacea",
                        "Malacostraca",
                        "Eumalacostraca",
                        "Eucarida",
                        "Decapoda",
                        "Pleocyemata",
                        "Anomura",
                        "Hippoidea",
                        "Hippidae",
                        "Emerita"
                ))
                .dbLinks(Map.of("BioProject", "PRJNA927338"))
                .references(references)
                .comment("PROVISIONAL REFSEQ: This record has not yet been subject to final NCBI review. The reference sequence is identical to ON164669.")
                .assemblyData(Map.of(
                        "Assembly Method", "NOVOPlasty v. v. 1.2.3",
                        "Sequencing Technology", "Illumina"))
                .build();


        // Define translation options with specific genetic code
        TranslationOptions translationOptions = TranslationOptions.builder()
                .translTableNumber(5)
                .translateCDS(true)
                .includeStopCodon(false)
                .build();


        // Define conversion options with HeaderInfo
        ConversionOptions options = ConversionOptions.builder()
                .organism("Emerita talpoida")
                .moleculeType("DNA")
                .topology("circular")
                .division("INV")
                .annotationFormat("FASTA")
                .headerInfo(headerInfo)
                .translationOptions(translationOptions)
                .build();

        // Convert to GenBank format
        GenbankResult result = converter.convert(sequenceFile, annotationFile, options);

        // Write the result to a file
        File outputFile = new File("output_with_header_info.gb");
        result.writeToFile(outputFile);
    }


}