package xyz.mahmoudahmed.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.model.AnnotationData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultAnnotationParserTest {

    private DefaultAnnotationParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new DefaultAnnotationParser();
    }

    @Test
    void testSupportsFormat() {
        assertTrue(parser.supportsFormat("GFF"));
        assertTrue(parser.supportsFormat("gff"));
        assertTrue(parser.supportsFormat("GFF3"));
        assertTrue(parser.supportsFormat("GTF"));
        assertTrue(parser.supportsFormat("BED"));
        assertFalse(parser.supportsFormat("FASTA"));
        assertFalse(parser.supportsFormat("GENBANK"));
    }

    @Test
    void testParseGffFile() throws IOException {
        Path gffFile = tempDir.resolve("test.gff");
        Files.writeString(gffFile,
                "##gff-version 3\n" +
                        "chr1\t.\tgene\t1000\t2000\t.\t+\t.\tID=gene1;Name=testGene\n" +
                        "chr1\t.\tCDS\t1050\t1500\t.\t+\t0\tID=cds1;Parent=gene1\n" +
                        "chr2\t.\texon\t5000\t5500\t.\t-\t.\tID=exon1\n");

        AnnotationData result = parser.parse(gffFile.toFile());

        // Check counts
        assertNotNull(result);
        assertEquals(2, result.getAnnotationsBySequence().size());
        assertEquals(3, result.getTotalCount());

        // Check chr1 annotations
        List<Annotation> chr1Annotations = result.getAnnotationsForSequence("chr1");
        assertEquals(2, chr1Annotations.size());

        // Verify gene annotation
        Annotation geneAnnotation = chr1Annotations.get(0);
        assertEquals("gene", geneAnnotation.getType());
        assertEquals(999, geneAnnotation.getStart()); // 0-based
        assertEquals(2000, geneAnnotation.getEnd());
        assertEquals(1, geneAnnotation.getStrand());
        assertEquals("gene1", geneAnnotation.getQualifiers().get("ID").get(0));

        // Verify CDS annotation
        Annotation cdsAnnotation = chr1Annotations.get(1);
        assertEquals("CDS", cdsAnnotation.getType());
        assertEquals(1049, cdsAnnotation.getStart()); // 0-based
        assertEquals(1500, cdsAnnotation.getEnd());
        assertEquals(0, cdsAnnotation.getPhase().intValue());

        // Check chr2 annotations
        List<Annotation> chr2Annotations = result.getAnnotationsForSequence("chr2");
        assertEquals(1, chr2Annotations.size());
        assertEquals(-1, chr2Annotations.get(0).getStrand()); // Negative strand
    }

    @Test
    void testParseGtfFile() throws IOException {
        Path gtfFile = tempDir.resolve("test.gtf");
        Files.writeString(gtfFile,
                "chr1\tSource\tgene\t1000\t2000\t.\t+\t.\tgene_id \"gene1\"; gene_name \"testGene\";\n" +
                        "chr1\tSource\texon\t1000\t1200\t.\t+\t.\tgene_id \"gene1\"; exon_id \"exon1\";\n" +
                        "chr1\tSource\tCDS\t1000\t1200\t.\t+\t0\tgene_id \"gene1\"; transcript_id \"transcript1\";\n");

        AnnotationData result = parser.parse(gtfFile.toFile());

        assertNotNull(result);
        assertEquals(1, result.getAnnotationsBySequence().size());
        assertEquals(3, result.getTotalCount());

        List<Annotation> annotations = result.getAnnotationsForSequence("chr1");

        Annotation geneAnnotation = annotations.get(0);
        assertEquals("gene", geneAnnotation.getType());
        assertEquals("gene1", geneAnnotation.getQualifiers().get("gene_id").get(0));
        assertEquals("testGene", geneAnnotation.getQualifiers().get("gene_name").get(0));

        Annotation cdsAnnotation = annotations.get(2);
        assertEquals("CDS", cdsAnnotation.getType());
        assertEquals("transcript1", cdsAnnotation.getQualifiers().get("transcript_id").get(0));
        assertEquals(0, cdsAnnotation.getPhase().intValue());
    }

    @Test
    void testParseBedFile() throws IOException {
        Path bedFile = tempDir.resolve("test.bed");
        Files.writeString(bedFile,
                "chr1\t1000\t2000\tfeature1\t500\t+\n" +
                        "chr2\t3000\t4000\tfeature2\t.\t-\n" +
                        "chr3\t5000\t6000\n");

        AnnotationData result = parser.parse(bedFile.toFile());

        assertNotNull(result);
        assertEquals(3, result.getAnnotationsBySequence().size());
        assertEquals(3, result.getTotalCount());

        // Check first feature
        Annotation feature1 = result.getAnnotationsForSequence("chr1").get(0);
        assertEquals("region", feature1.getType());
        assertEquals(1000, feature1.getStart());
        assertEquals(2000, feature1.getEnd());
        assertEquals(1, feature1.getStrand());
        assertEquals("feature1", feature1.getQualifiers().get("Name").get(0));
        assertEquals("500", feature1.getQualifiers().get("score").get(0));

        // Check feature with missing fields
        Annotation feature3 = result.getAnnotationsForSequence("chr3").get(0);
        assertEquals("region", feature3.getType());
        assertEquals(5000, feature3.getStart());
        assertEquals(6000, feature3.getEnd());
        assertEquals(0, feature3.getStrand()); // Unspecified strand
    }

    @Test
    void testParseEmptyFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.gff");
        Files.writeString(emptyFile, "##gff-version 3\n");

        AnnotationData result = parser.parse(emptyFile.toFile());

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
        assertTrue(result.getAnnotationsBySequence().isEmpty());
    }

    @Test
    void testParseWithComments() throws IOException {
        Path gffWithComments = tempDir.resolve("comments.gff");
        Files.writeString(gffWithComments,
                "##gff-version 3\n" +
                        "# This is a comment\n" +
                        "chr1\t.\tgene\t1000\t2000\t.\t+\t.\tID=gene1\n" +
                        "# Another comment\n");

        AnnotationData result = parser.parse(gffWithComments.toFile());

        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getAnnotationsForSequence("chr1").size());
    }

    @Test
    void testParseWithInvalidLines() throws IOException {
        Path invalidLines = tempDir.resolve("invalid.gff");
        Files.writeString(invalidLines,
                "##gff-version 3\n" +
                        "This is not a valid GFF line\n" +
                        "chr1\t.\tgene\t1000\t2000\t.\t+\t.\tID=gene1\n" +
                        "chr1\t.\tCDS\tnot_a_number\t2000\t.\t+\t0\tID=cds1\n");

        AnnotationData result = parser.parse(invalidLines.toFile());

        // Should parse the valid line but skip the invalid ones
        assertNotNull(result);
        assertEquals(1, result.getTotalCount());
        assertEquals("gene", result.getAnnotationsForSequence("chr1").get(0).getType());
    }
}