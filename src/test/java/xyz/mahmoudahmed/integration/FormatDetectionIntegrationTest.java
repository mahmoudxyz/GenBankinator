package xyz.mahmoudahmed.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.util.BioFileUtils;
import xyz.mahmoudahmed.exception.FileProcessingException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the BioFileUtils using real files.
 */
class FormatDetectionIntegrationTest {

    private BioFileUtils bioFileUtils;

    @BeforeEach
    void setUp() {
        bioFileUtils = BioFileUtils.create();
    }

    @Test
    void detectFormat_fastaFile_returnsFasta(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a FASTA file
        File fastaFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(fastaFile)) {
            writer.write(">Sequence1\nACGTACGTACGT\n>Sequence2\nTGCATGCATGCA\n");
        }

        // Test format detection
        String format = bioFileUtils.detectFormat(fastaFile);
        assertEquals("FASTA", format);
    }

    @Test
    void detectFormat_gffFile_returnsGff(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a GFF file
        File gffFile = tempDir.resolve("test.gff").toFile();
        try (FileWriter writer = new FileWriter(gffFile)) {
            writer.write("##gff-version 3\n");
            writer.write("chr1\tEnsembl\tgene\t1000\t2000\t.\t+\t.\tID=gene1;Name=BRCA1\n");
        }

        // Test format detection
        String format = bioFileUtils.detectFormat(gffFile);
        assertEquals("GFF", format);
    }

    @Test
    void detectFormat_gtfFile_returnsGtf(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a GTF file with more explicit content
        File gtfFile = tempDir.resolve("test.gtf").toFile();
        String gtfContent =
                "chr1\thavana\tgene\t11869\t14409\t.\t+\t.\tgene_id \"ENSG00000223972\";\n" +
                        "chr1\thavana\ttranscript\t11869\t14409\t.\t+\t.\tgene_id \"ENSG00000223972\"; transcript_id \"ENST00000456328\";\n";

        Files.writeString(gtfFile.toPath(), gtfContent, StandardCharsets.UTF_8);

        // Test format detection
        String format = bioFileUtils.detectFormat(gtfFile);
        assertEquals("GTF", format, "File should be detected as GTF format");
    }


    @Test
    void detectFormat_bedFile_returnsBed(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a BED file
        File bedFile = tempDir.resolve("test.bed").toFile();
        try (FileWriter writer = new FileWriter(bedFile)) {
            writer.write("track name=\"BED Track\" description=\"Example BED track\"\n");
            writer.write("chr1\t1000\t2000\tfeature1\t500\t+\n");
            writer.write("chr1\t3000\t4000\tfeature2\t900\t-\n");
        }

        // Test format detection
        String format = bioFileUtils.detectFormat(bedFile);
        assertEquals("BED", format);
    }

    @Test
    void detectFormat_genbankFile_returnsGenbank(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a GENBANK file
        File genbankFile = tempDir.resolve("test.gb").toFile();
        try (FileWriter writer = new FileWriter(genbankFile)) {
            writer.write("LOCUS       SCU49845     5028 bp    DNA             PLN       21-JUN-1999\n");
            writer.write("FEATURES             Location/Qualifiers\n");
            writer.write("     source          1..5028\n");
            writer.write("                     /organism=\"Saccharomyces cerevisiae\"\n");
        }

        // Test format detection
        String format = bioFileUtils.detectFormat(genbankFile);
        assertEquals("GENBANK", format);
    }

    @Test
    void detectFormat_vcfFile_returnsVcf(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a VCF file
        File vcfFile = tempDir.resolve("test.vcf").toFile();
        try (FileWriter writer = new FileWriter(vcfFile)) {
            writer.write("##fileformat=VCFv4.2\n");
            writer.write("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n");
            writer.write("chr1\t1000\trs123\tA\tG\t100\tPASS\tAF=0.5\n");
        }

        // Test format detection
        String format = bioFileUtils.detectFormat(vcfFile);
        assertEquals("VCF", format);
    }

    @Test
    void  isFastaAnnotationFile_fastaAnnotation_returnsTrue(@TempDir Path tempDir) throws IOException, FileProcessingException {
        // Create a FASTA annotation file
        File fastaFile = tempDir.resolve("test.fasta").toFile();
        try (FileWriter writer = new FileWriter(fastaFile)) {
            writer.write(">gene1;1000-2000;+;CDS\nACGTACGTACGT\n>gene2;3000-4000;-;CDS\nTGCATGCATGCA\n");
        }

        // Test annotation detection
        boolean isAnnotation = bioFileUtils.isFastaAnnotationFile(fastaFile);
        assertTrue(isAnnotation);
    }

    @Test
    void fileOperations_readWriteAppend_worksCorrectly(@TempDir Path tempDir) throws IOException {
        // Create a file to test file operations
        File testFile = tempDir.resolve("fileops.txt").toFile();

        // Write content
        String content = "Initial content\n";
        bioFileUtils.writeStringToFile(content, testFile);

        // Read content
        String readContent = bioFileUtils.readFileToString(testFile);
        assertEquals(content, readContent);

        // Append content
        String appendedContent = "Appended content\n";
        bioFileUtils.appendStringToFile(appendedContent, testFile);

        // Read again
        String combinedContent = bioFileUtils.readFileToString(testFile);
        assertEquals(content + appendedContent, combinedContent);
    }
}