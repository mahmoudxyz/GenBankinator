package xyz.mahmoudahmed.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.mahmoudahmed.exception.InvalidFileFormatException;
import xyz.mahmoudahmed.model.Sequence;
import xyz.mahmoudahmed.model.SequenceData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSequenceParserTest {

    private DefaultSequenceParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new DefaultSequenceParser();
    }

    @Test
    void testSupportsFormat() {
        assertTrue(parser.supportsFormat("FASTA"));
        assertTrue(parser.supportsFormat("fasta"));
        assertTrue(parser.supportsFormat("FA"));
        assertFalse(parser.supportsFormat("GENBANK"));
        assertFalse(parser.supportsFormat("GFF"));
    }

    @Test
    void testParseValidFastaFile() throws IOException {
        // Create a temp FASTA file
        Path fastaFile = tempDir.resolve("test.fasta");
        Files.writeString(fastaFile,
                ">seq1 Test Sequence 1\n" +
                        "ATGCATGCATGC\n" +
                        ">seq2 Test Sequence 2\n" +
                        "GTATATATCGCGATATATA\n");

        SequenceData result = parser.parse(fastaFile.toFile());

        assertNotNull(result);
        assertEquals(2, result.getCount());

        List<Sequence> sequences = result.getSequences();
        assertEquals("seq1", sequences.get(0).getId());
        assertEquals("Test Sequence 1", sequences.get(0).getDescription().substring(5));
        assertEquals("ATGCATGCATGC", sequences.get(0).getSequence());

        assertEquals("seq2", sequences.get(1).getId());
        assertEquals("GTATATATCGCGATATATA", sequences.get(1).getSequence());
    }

    @Test
    void testParseEmptyFastaFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.fasta");
        Files.writeString(emptyFile, "");

        SequenceData result = parser.parse(emptyFile.toFile());

        assertNotNull(result);
        assertEquals(0, result.getCount());
        assertTrue(result.getSequences().isEmpty());
    }

    @Test
    void testParseInvalidFile() throws IOException {
        Path invalidFile = tempDir.resolve("invalid.txt");
        Files.writeString(invalidFile, "This is not a FASTA file");

        assertThrows(InvalidFileFormatException.class, () -> {
            parser.parse(invalidFile.toFile());
        });
    }

    @Test
    void testParseMetadataOnly() throws IOException {
        Path fastaFile = tempDir.resolve("metadata.fasta");
        Files.writeString(fastaFile,
                ">seq1 Test Sequence 1\n" +
                        "ATGCATGCATGC\n" +
                        ">seq2 Test Sequence 2\n" +
                        "GTATATATCGCGATATATA\n");

        SequenceData result = parser.parseMetadataOnly(fastaFile.toFile());

        assertNotNull(result);
        assertEquals(2, result.getCount());

        List<Sequence> sequences = result.getSequences();
        assertEquals("seq1", sequences.get(0).getId());
        // Sequence should be null or empty in metadata-only mode
        assertNull(sequences.get(0).getSequence());
        assertEquals(12, sequences.get(0).getLength());

        assertEquals("seq2", sequences.get(1).getId());
        assertEquals(19, sequences.get(1).getLength());
    }

    @Test
    void testParseWithInputStream() throws IOException {
        String fastaContent =
                ">seq1 Test Sequence 1\n" +
                        "ATGCATGCATGC\n" +
                        ">seq2 Test Sequence 2\n" +
                        "GTATATATCGCGATATATA\n";

        InputStream inputStream = Files.newInputStream(
                Files.writeString(tempDir.resolve("stream.fasta"), fastaContent));

        SequenceData result = parser.parse(inputStream);

        assertNotNull(result);
        assertEquals(2, result.getCount());
        assertEquals("seq1", result.getSequence("seq1").getId());
        assertEquals("ATGCATGCATGC", result.getSequence("seq1").getSequence());
    }

    @Test
    void testParseMultilineFasta() throws IOException {
        Path fastaFile = tempDir.resolve("multiline.fasta");
        Files.writeString(fastaFile,
                ">seq1 Multiline sequence\n" +
                        "ATGCATGC\n" +
                        "ATGCATGC\n" +
                        "ATGCATGC\n");

        SequenceData result = parser.parse(fastaFile.toFile());

        assertNotNull(result);
        assertEquals(1, result.getCount());
        assertEquals("ATGCATGCATGCATGCATGCATGC", result.getSequence("seq1").getSequence());
    }

    @Test
    void testParseWithComments() throws IOException {
        Path fastaFile = tempDir.resolve("comments.fasta");
        Files.writeString(fastaFile,
                "# This is a comment\n" +
                        ">seq1 Test Sequence\n" +
                        "ATGCATGCATGC\n");

        SequenceData result = parser.parse(fastaFile.toFile());

        assertNotNull(result);
        assertEquals(1, result.getCount());
        assertEquals("seq1", result.getSequences().get(0).getId());
    }
}