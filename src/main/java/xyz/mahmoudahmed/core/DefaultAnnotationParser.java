package xyz.mahmoudahmed.core;

import xyz.mahmoudahmed.api.AnnotationParser;
import xyz.mahmoudahmed.exception.InvalidFileFormatException;
import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.model.AnnotationData;
import xyz.mahmoudahmed.util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of an annotation parser that supports GFF and GTF formats.
 */
public class DefaultAnnotationParser implements AnnotationParser {
    private static final Logger logger = Logger.getLogger(DefaultAnnotationParser.class.getName());
    private static final Pattern FASTA_HEADER_PATTERN = Pattern.compile(
            ">([^;]+);\\s*([0-9]+-[0-9]+);\\s*([+\\-]);\\s*([^\\(]+)(?:\\(([^\\)]+)\\))?.*");

    @Override
    public boolean supportsFormat(String format) {
        return "GFF".equalsIgnoreCase(format) || "GTF".equalsIgnoreCase(format)
                || "FASTA".equalsIgnoreCase(format) || "GFF3".equalsIgnoreCase(format) || "BED".equalsIgnoreCase(format);
    }


    @Override
    public AnnotationData parse(File file) throws IOException {
        String format = FileUtil.detectFileFormat(file);

        if ("GFF".equalsIgnoreCase(format) || "GFF3".equalsIgnoreCase(format)) {
            return parseGff(file);
        } else if ("GTF".equalsIgnoreCase(format)) {
            return parseGtf(file);
        } else if ("BED".equalsIgnoreCase(format)) {
            return parseBed(file);
        } else if ("FASTA".equalsIgnoreCase(format)) {
            return parseFasta(file);
        } else {
            throw new InvalidFileFormatException("Unsupported annotation format: " + format, format);
        }
    }

    @Override
    public AnnotationData parse(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String format = detectFormatFromStream(reader);
            inputStream.reset();

            if ("GFF".equalsIgnoreCase(format) || "GFF3".equalsIgnoreCase(format)) {
                return parseGffFromStream(reader);
            } else if ("GTF".equalsIgnoreCase(format)) {
                return parseGtfFromStream(reader);
            } else if ("BED".equalsIgnoreCase(format)) {
                return parseBedFromStream(reader);
            } else if ("FASTA".equalsIgnoreCase(format)) {
                return parseFastaFromStream(reader);
            } else {
                throw new InvalidFileFormatException("Unsupported annotation format: " + format, format);
            }
        }
    }


    /**
     * Detect the format from an input stream by examining the first few non-comment lines.
     */
    private String detectFormatFromStream(BufferedReader reader) throws IOException {
        reader.mark(8192);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Check for FASTA header
                if (line.startsWith(">")) {
                    return "FASTA";
                }

                // Existing checks for other formats...
                if (line.contains("gff-version 3")) {
                    return "GFF3";
                }

                String[] fields = line.split("\\t");
                if (fields.length >= 8) {
                    if (fields.length > 8 && fields[8].contains(" \"") && fields[8].contains("\";")) {
                        return "GTF";
                    } else {
                        return "GFF";
                    }
                } else if (fields.length >= 3) {
                    try {
                        Integer.parseInt(fields[1]);
                        Integer.parseInt(fields[2]);
                        return "BED";
                    } catch (NumberFormatException e) {
                        // Not a BED file
                    }
                }
                break;
            }
            return "GFF";
        } finally {
            reader.reset();
        }
    }

    /**
     * Parse a FASTA annotation file into annotation data.
     */
    private AnnotationData parseFasta(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return parseFastaFromStream(reader);
        }
    }

    /**
     * Parse a FASTA stream into annotation data.
     */
    private AnnotationData parseFastaFromStream(BufferedReader reader) throws IOException {
        Map<String, List<Annotation>> annotationsBySequence = new HashMap<>();
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith(">")) {
                Matcher matcher = FASTA_HEADER_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String seqId = matcher.group(1).trim();
                    String[] positions = matcher.group(2).split("-");
                    if (positions.length != 2) {
                        logger.log(Level.WARNING, "Invalid position format in FASTA header at line {0}", lineNumber);
                        continue;
                    }

                    try {
                        int start = Integer.parseInt(positions[0]) - 1; // Convert to 0-based
                        int end = Integer.parseInt(positions[1]);
                        if (start >= end) {
                            logger.log(Level.WARNING, "Start >= end at line {0}", lineNumber);
                            continue;
                        }

                        String strandStr = matcher.group(3).trim();
                        int strand = "+".equals(strandStr) ? 1 : -1;
                        String type = matcher.group(4).trim();
                        String gene = matcher.group(5) != null ? matcher.group(5).trim() : null;

                        Map<String, List<String>> qualifiers = new HashMap<>();
                        qualifiers.put("type", Collections.singletonList(type));
                        if (gene != null) {
                            qualifiers.put("gene", Collections.singletonList(gene));
                        }

                        String featureId = gene != null ? gene : type + "-" + UUID.randomUUID().toString().substring(0, 8);

                        Annotation annotation = Annotation.builder()
                                .type(type)
                                .start(start)
                                .end(end)
                                .strand(strand)
                                .sequenceId(seqId)
                                .featureId(featureId)
                                .qualifiers(qualifiers)
                                .build();

                        annotationsBySequence.computeIfAbsent(seqId, k -> new ArrayList<>()).add(annotation);
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING, "Invalid positions in FASTA header at line {0}", lineNumber);
                    }
                } else {
                    logger.log(Level.WARNING, "Invalid FASTA header at line {0}", lineNumber);
                }
            }
        }

        return AnnotationData.builder()
                .addAnnotations(annotationsBySequence)
                .build();
    }

    /**
     * Parse a GFF file into annotation data.
     */
    private AnnotationData parseGff(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return parseGffFromStream(reader);
        }
    }


    /**
     * Parse a GFF stream into annotation data.
     */
    private AnnotationData parseGffFromStream(BufferedReader reader) throws IOException {
        Map<String, List<Annotation>> annotationsBySequence = new HashMap<>();
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // Skip comment lines and empty lines
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            try {
                // Parse GFF line
                String[] fields = line.split("\\t");
                if (fields.length < 8) {
                    // Skip invalid lines instead of throwing an exception
                    logger.log(Level.WARNING, "Skipping line {0}: fewer than 8 fields", lineNumber);
                    continue;
                }

                // GFF format: seqid, source, type, start, end, score, strand, phase, attributes
                String seqId = fields[0];
                String type = fields[2];

                int start, end;
                try {
                    start = Integer.parseInt(fields[3]) - 1; // GFF is 1-based, convert to 0-based
                    end = Integer.parseInt(fields[4]); // GFF end is inclusive, but we need exclusive
                } catch (NumberFormatException e) {
                    // Skip lines with invalid numbers
                    logger.log(Level.WARNING, "Skipping line {0}: invalid number format in start/end fields", lineNumber);
                    continue;
                }

                // Parse strand
                int strand = 0;
                if (fields[6].equals("+")) {
                    strand = 1;
                } else if (fields[6].equals("-")) {
                    strand = -1;
                }

                // Parse phase
                Integer phase = null;
                if (!fields[7].equals(".")) {
                    try {
                        phase = Integer.parseInt(fields[7]);
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING, "Invalid phase value at line {0}, using null", lineNumber);
                    }
                }

                // Parse attributes
                Map<String, List<String>> qualifiers = new HashMap<>();
                if (fields.length > 8) {
                    String[] attrPairs = fields[8].split(";");
                    for (String pair : attrPairs) {
                        if (pair.trim().isEmpty()) {
                            continue;
                        }

                        if (pair.contains("=")) {
                            String[] keyValue = pair.split("=", 2);
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();

                            // Handle values with commas
                            if (value.contains(",")) {
                                List<String> values = Arrays.asList(value.split(","));
                                qualifiers.put(key, values);
                            } else {
                                qualifiers.put(key, List.of(value));
                            }
                        }
                    }
                }

                // Create feature ID if not present
                String featureId = qualifiers.containsKey("ID")
                        ? qualifiers.get("ID").get(0)
                        : type + "-" + UUID.randomUUID().toString().substring(0, 8);

                // Create the annotation
                Annotation annotation = Annotation.builder()
                        .type(type)
                        .start(start)
                        .end(end)
                        .strand(strand)
                        .phase(phase)
                        .sequenceId(seqId)
                        .featureId(featureId)
                        .qualifiers(qualifiers)
                        .build();

                // Add to the result map
                annotationsBySequence.computeIfAbsent(seqId, k -> new ArrayList<>()).add(annotation);

            } catch (Exception e) {
                // Skip any lines that cause exceptions during parsing
                logger.log(Level.WARNING, "Error parsing line {0}: {1}", new Object[]{lineNumber, e.getMessage()});
            }
        }

        return AnnotationData.builder()
                .addAnnotations(annotationsBySequence)
                .build();
    }

    /**
     * Parse a GTF file into annotation data.
     */
    private AnnotationData parseGtf(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return parseGtfFromStream(reader);
        }
    }

    /**
     * Parse a GTF stream into annotation data.
     */
    private AnnotationData parseGtfFromStream(BufferedReader reader) throws IOException {
        Map<String, List<Annotation>> annotationsBySequence = new HashMap<>();
        Pattern attributePattern = Pattern.compile("(\\S+)\\s+\"([^\"]+)\"");
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // Skip comment lines and empty lines
            if (line.trim().isEmpty() || line.startsWith("#")) {
                continue;
            }

            try {
                // Parse GTF line
                String[] fields = line.split("\\t");
                if (fields.length < 8) {
                    // Skip invalid lines
                    logger.log(Level.WARNING, "Skipping line {0}: fewer than 8 fields", lineNumber);
                    continue;
                }

                // GTF format: seqname, source, feature, start, end, score, strand, frame, attribute
                String seqId = fields[0];
                String type = fields[2];

                int start, end;
                try {
                    start = Integer.parseInt(fields[3]) - 1; // GTF is 1-based, convert to 0-based
                    end = Integer.parseInt(fields[4]); // GTF end is inclusive, but we need exclusive
                } catch (NumberFormatException e) {
                    // Skip lines with invalid numbers
                    logger.log(Level.WARNING, "Skipping line {0}: invalid number format in start/end fields", lineNumber);
                    continue;
                }

                // Parse strand
                int strand = 0;
                if (fields[6].equals("+")) {
                    strand = 1;
                } else if (fields[6].equals("-")) {
                    strand = -1;
                }

                // Parse phase/frame
                Integer phase = null;
                if (!fields[7].equals(".")) {
                    try {
                        phase = Integer.parseInt(fields[7]);
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING, "Invalid phase value at line {0}, using null", lineNumber);
                    }
                }

                // Parse attributes (GTF format is different from GFF)
                Map<String, List<String>> qualifiers = new HashMap<>();
                if (fields.length > 8) {
                    String attributeStr = fields[8];
                    Matcher matcher = attributePattern.matcher(attributeStr);

                    while (matcher.find()) {
                        String key = matcher.group(1);
                        String value = matcher.group(2);

                        qualifiers.computeIfAbsent(key, k -> new ArrayList<>())
                                .add(value);
                    }
                }

                // Generate feature ID
                String featureId;
                if (qualifiers.containsKey("gene_id")) {
                    featureId = qualifiers.get("gene_id").get(0);
                } else if (qualifiers.containsKey("transcript_id")) {
                    featureId = qualifiers.get("transcript_id").get(0);
                } else {
                    featureId = type + "-" + UUID.randomUUID().toString().substring(0, 8);
                }

                // Create the annotation
                Annotation annotation = Annotation.builder()
                        .type(type)
                        .start(start)
                        .end(end)
                        .strand(strand)
                        .phase(phase)
                        .sequenceId(seqId)
                        .featureId(featureId)
                        .qualifiers(qualifiers)
                        .build();

                // Add to the result map
                annotationsBySequence.computeIfAbsent(seqId, k -> new ArrayList<>()).add(annotation);

            } catch (Exception e) {
                // Skip any lines that cause exceptions during parsing
                logger.log(Level.WARNING, "Error parsing line {0}: {1}", new Object[]{lineNumber, e.getMessage()});
            }
        }

        return AnnotationData.builder()
                .addAnnotations(annotationsBySequence)
                .build();
    }

    /**
     * Parse a BED file into annotation data.
     */
    private AnnotationData parseBed(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return parseBedFromStream(reader);
        }
    }

    /**
     * Parse a BED stream into annotation data.
     */
    private AnnotationData parseBedFromStream(BufferedReader reader) throws IOException {
        Map<String, List<Annotation>> annotationsBySequence = new HashMap<>();
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // Skip comment or header lines
            if (line.trim().isEmpty() || line.startsWith("#") ||
                    line.startsWith("browser") || line.startsWith("track")) {
                continue;
            }

            try {
                // Parse BED line
                String[] fields = line.split("\\t");
                if (fields.length < 3) {
                    // Skip invalid lines
                    logger.log(Level.WARNING, "Skipping line {0}: fewer than 3 fields", lineNumber);
                    continue;
                }

                // BED format fields:
                // 1. chrom - The name of the chromosome (e.g. chr3, chrY)
                // 2. chromStart - The starting position (0-based)
                // 3. chromEnd - The ending position (exclusive)
                String seqId = fields[0];

                int start, end;
                try {
                    start = Integer.parseInt(fields[1]);
                    end = Integer.parseInt(fields[2]);
                } catch (NumberFormatException e) {
                    // Skip lines with invalid numbers
                    logger.log(Level.WARNING, "Skipping line {0}: invalid number format in start/end fields", lineNumber);
                    continue;
                }

                // Feature name (if available)
                String name = fields.length > 3 ? fields[3] : "feature_" + start + "_" + end;

                // Strand (if available)
                int strand = 0;
                if (fields.length > 5) {
                    if (fields[5].equals("+")) {
                        strand = 1;
                    } else if (fields[5].equals("-")) {
                        strand = -1;
                    }
                }

                // Create qualifiers map
                Map<String, List<String>> qualifiers = new HashMap<>();
                qualifiers.put("ID", List.of(name + "-" + UUID.randomUUID().toString().substring(0, 8)));
                qualifiers.put("Name", List.of(name));

                // Add score if available
                if (fields.length > 4 && !fields[4].equals(".")) {
                    qualifiers.put("score", List.of(fields[4]));
                }

                // Generate unique feature ID
                String featureId = name + "-" + UUID.randomUUID().toString().substring(0, 8);

                // Create the annotation
                Annotation annotation = Annotation.builder()
                        .type("region") // Default type for BED features
                        .start(start)
                        .end(end)
                        .strand(strand)
                        .sequenceId(seqId)
                        .featureId(featureId)
                        .qualifiers(qualifiers)
                        .build();

                // Add to the result map
                annotationsBySequence.computeIfAbsent(seqId, k -> new ArrayList<>()).add(annotation);

            } catch (Exception e) {
                // Skip any lines that cause exceptions during parsing
                logger.log(Level.WARNING, "Error parsing line {0}: {1}", new Object[]{lineNumber, e.getMessage()});
            }
        }

        return AnnotationData.builder()
                .addAnnotations(annotationsBySequence)
                .build();
    }


}