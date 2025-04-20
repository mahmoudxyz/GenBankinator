package xyz.mahmoudahmed.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.model.AnnotationData;
import xyz.mahmoudahmed.model.ConversionOptions;
import xyz.mahmoudahmed.model.TranslationOptions;
import xyz.mahmoudahmed.translator.GeneticCodeTable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FastaAnnotationParserTest {

    private FastaAnnotationParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        parser = new FastaAnnotationParser();
    }

    @Test
    public void testSupportsFormat() {
        assertTrue(parser.supportsFormat("FASTA"));
        assertTrue(parser.supportsFormat("FA"));
        assertTrue(parser.supportsFormat("FNA"));
        assertTrue(parser.supportsFormat("FAA"));
        assertTrue(parser.supportsFormat("FFN"));
        assertTrue(parser.supportsFormat("fasta")); // Case insensitive
        assertFalse(parser.supportsFormat("UNKNOWN"));
        assertFalse(parser.supportsFormat("GenBank"));
    }

    @Test
    public void testParseFromInputStream() throws IOException {
        // Create a simple FASTA test string
        String fasta = ">seq1; 1-100; +; COX1\nATGCGATACGATACGATACG\n" +
                ">seq1; 101-200; -; ND2\nATGCCTAATGCCTAATGCCTAA";

        // Parse from input stream
        AnnotationData data = parser.parse(new ByteArrayInputStream(fasta.getBytes()));

        // Verify results
        assertNotNull(data);
//        assertEquals(1, data.getSequenceIds().size());
//        assertTrue(data.getSequenceIds().contains("seq1"));

        List<Annotation> annotations = data.getAnnotationsForSequence("seq1");
        assertEquals(2, annotations.size());

        // Check first annotation
        Annotation cox1 = annotations.get(0);
        assertEquals("CDS", cox1.getType()); // Assuming COX1 maps to CDS
        assertEquals(0, cox1.getStart()); // 1-based to 0-based conversion
        assertEquals(100, cox1.getEnd());
        assertEquals(1, cox1.getStrand());
        assertEquals("seq1", cox1.getSequenceId());

        // Check second annotation
        Annotation nd2 = annotations.get(1);
        assertEquals("CDS", nd2.getType()); // Assuming ND2 maps to CDS
        assertEquals(100, nd2.getStart()); // 1-based to 0-based conversion
        assertEquals(200, nd2.getEnd());
        assertEquals(-1, nd2.getStrand());
        assertEquals("seq1", nd2.getSequenceId());
    }

    @Test
    public void testParseFromFile(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        File fastaFile = tempDir.resolve("test.fa").toFile();
        try (FileWriter writer = new FileWriter(fastaFile)) {
            writer.write(">seq1; 1-100; +; COX1(cox1)\nATGCGATACGATACGATACG\n" +
                    ">seq1; 101-200; -; ND2\nATGCCTAATGCCTAATGCCTAA");
        }

        // Parse from file
        AnnotationData data = parser.parse(fastaFile);

        // Verify results
        assertNotNull(data);
//        assertEquals(1, data.getSequenceIds().size());
//        assertTrue(data.getSequenceIds().contains("seq1"));

        List<Annotation> annotations = data.getAnnotationsForSequence("seq1");
        assertEquals(2, annotations.size());

        // Check first annotation
        Annotation cox1 = annotations.get(0);
        assertEquals("CDS", cox1.getType());
        assertEquals(0, cox1.getStart());
        assertEquals(100, cox1.getEnd());
        assertEquals(1, cox1.getStrand());
        assertTrue(cox1.getQualifiers().containsKey("gene"));
        assertEquals("cox1", cox1.getQualifiers().get("gene").get(0));
    }

    @Test
    public void testParsingWithDifferentTranslationOptions() throws IOException {
        // Create a FASTA string with a CDS feature
        String fasta = ">seq1; 1-100; +; COX1\nATGCGATACGATACGATACG";

        // Set up parser with Standard translation table (1)
        ConversionOptions options1 = ConversionOptions.builder()
                .translationOptions(TranslationOptions.builder()
                        .translTableNumber(1)
                        .build())
                .build();

//        FastaAnnotationParser parser1 = new FastaAnnotationParser(options1);
//        AnnotationData data1 = parser1.parse(new ByteArrayInputStream(fasta.getBytes()));

        // Set up parser with Invertebrate Mitochondrial table (5)
        ConversionOptions options5 = ConversionOptions.builder()
                .translationOptions(TranslationOptions.builder()
                        .translTableNumber(5)
                        .build())
                .build();

//        FastaAnnotationParser parser5 = new FastaAnnotationParser(options5);
//        AnnotationData data5 = parser5.parse(new ByteArrayInputStream(fasta.getBytes()));
//
//        // Verify results
//        Annotation annotation1 = data1.getAnnotationsForSequence("seq1").get(0);
//        Annotation annotation5 = data5.getAnnotationsForSequence("seq1").get(0);
//
//        // Check that the transl_table qualifier matches the specified table
//        assertTrue(annotation1.getQualifiers().containsKey("transl_table"));
//        assertEquals("1", annotation1.getQualifiers().get("transl_table").get(0));

//        assertTrue(annotation5.getQualifiers().containsKey("transl_table"));
//        assertEquals("5", annotation5.getQualifiers().get("transl_table").get(0));
    }

    @Test
    public void testSetConversionOptions() throws IOException {
        // Create a FASTA string with a CDS feature
        String fasta = ">seq1; 1-100; +; COX1\nATGCGATACGATACGATACG";

        // First parse with default options (should use table 5)
        AnnotationData data1 = parser.parse(new ByteArrayInputStream(fasta.getBytes()));
        Annotation annotation1 = data1.getAnnotationsForSequence("seq1").get(0);

        assertTrue(annotation1.getQualifiers().containsKey("transl_table"));
        assertEquals("5", annotation1.getQualifiers().get("transl_table").get(0));

        // Now set options with table 2 and parse again
        ConversionOptions options2 = ConversionOptions.builder()
                .translationOptions(TranslationOptions.builder()
                        .translTableNumber(2)
                        .build())
                .build();

        parser.setConversionOptions(options2);
        AnnotationData data2 = parser.parse(new ByteArrayInputStream(fasta.getBytes()));
        Annotation annotation2 = data2.getAnnotationsForSequence("seq1").get(0);

        assertTrue(annotation2.getQualifiers().containsKey("transl_table"));
        assertEquals("2", annotation2.getQualifiers().get("transl_table").get(0));
    }

    @Test
    public void testParseComplexHeader() throws IOException {
        // Test with a more complex header format
        String fasta = ">seq1; 1-100; +; COX1(Cytochrome c oxidase subunit 1); complement\nATGCGATACGATACGATACG";

        AnnotationData data = parser.parse(new ByteArrayInputStream(fasta.getBytes()));

        assertNotNull(data);
        List<Annotation> annotations = data.getAnnotationsForSequence("seq1");
        assertEquals(1, annotations.size());

        Annotation annotation = annotations.get(0);
        assertEquals("CDS", annotation.getType());
        assertEquals(0, annotation.getStart());
        assertEquals(100, annotation.getEnd());
        assertEquals(1, annotation.getStrand()); // + strand

        Map<String, List<String>> qualifiers = annotation.getQualifiers();
        assertTrue(qualifiers.containsKey("gene"));
        assertEquals("COX1", qualifiers.get("gene").get(0));

        assertTrue(qualifiers.containsKey("product"));
        assertEquals("Cytochrome c oxidase subunit 1", qualifiers.get("product").get(0));
    }

    @Test
    public void testStandardizedGeneNames() throws IOException {
        // Test standardization of gene names
        String fasta = ">seq1; 1-100; +; nad1\nATGCGA\n" +
                ">seq1; 101-200; +; cox2\nATGCGA\n" +
                ">seq1; 201-300; +; atp6\nATGCGA\n" +
                ">seq1; 301-400; +; cytb\nATGCGA\n" +
                ">seq1; 401-500; +; rrns\nATGCGA\n" +
                ">seq1; 501-600; +; rrnl\nATGCGA";

        AnnotationData data = parser.parse(new ByteArrayInputStream(fasta.getBytes()));

        List<Annotation> annotations = data.getAnnotationsForSequence("seq1");
        assertEquals(6, annotations.size());

        // Check standardized gene names in qualifiers
        assertEquals("ND1", annotations.get(0).getQualifiers().get("gene").get(0));
        assertEquals("COX2", annotations.get(1).getQualifiers().get("gene").get(0));
        assertEquals("ATP6", annotations.get(2).getQualifiers().get("gene").get(0));
        assertEquals("CYTB", annotations.get(3).getQualifiers().get("gene").get(0));
        assertEquals("rrn12", annotations.get(4).getQualifiers().get("gene").get(0));
        assertEquals("rrn16", annotations.get(5).getQualifiers().get("gene").get(0));
    }
}