package xyz.mahmoudahmed.formatters;

import xyz.mahmoudahmed.exception.ConversionException;
import xyz.mahmoudahmed.model.*;
import xyz.mahmoudahmed.util.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Default implementation of GenbankFormatter.
 * This class handles the actual formatting of sequences and annotations into GenBank format.
 */
public class DefaultGenbankFormatter implements GenbankFormatter {
    private static final int DEFAULT_LINE_WIDTH = 80;
    private static final int DEFAULT_SEQUENCE_LINE_WIDTH = 60;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

    // Standard feature type mapping
    private static final Map<String, String> STANDARD_FEATURE_TYPES = new HashMap<>();

    // Standard gene names
    private static final Map<String, String> STANDARD_GENE_NAMES = new HashMap<>();

    // Protein name mapping
    private static final Map<String, String> PROTEIN_NAME_MAP = new HashMap<>();

    // Set of numeric qualifiers (those that don't need quotes)
    private static final Set<String> NUMERIC_QUALIFIERS = Set.of(
            "codon_start", "transl_table", "codon", "number", "score"
    );

    static {
        // Initialize standard mappings
        initializeStandardMappings();
    }

    /**
     * Initialize standard mappings for feature types, gene names, etc.
     */
    private static void initializeStandardMappings() {
        // Feature type mappings
        STANDARD_FEATURE_TYPES.put("trnf", "tRNA");
        STANDARD_FEATURE_TYPES.put("trnv", "tRNA");
        STANDARD_FEATURE_TYPES.put("trnl", "tRNA");
        // Add more tRNA mappings...

        STANDARD_FEATURE_TYPES.put("rrns", "rRNA");
        STANDARD_FEATURE_TYPES.put("rrnl", "rRNA");
        STANDARD_FEATURE_TYPES.put("rrn12", "rRNA");
        STANDARD_FEATURE_TYPES.put("rrn16", "rRNA");

        STANDARD_FEATURE_TYPES.put("nad1", "CDS");
        STANDARD_FEATURE_TYPES.put("nad2", "CDS");
        STANDARD_FEATURE_TYPES.put("nad3", "CDS");
        STANDARD_FEATURE_TYPES.put("nad4", "CDS");
        STANDARD_FEATURE_TYPES.put("nad4l", "CDS");
        STANDARD_FEATURE_TYPES.put("nad5", "CDS");
        STANDARD_FEATURE_TYPES.put("nad6", "CDS");
        STANDARD_FEATURE_TYPES.put("cox1", "CDS");
        STANDARD_FEATURE_TYPES.put("cox2", "CDS");
        STANDARD_FEATURE_TYPES.put("cox3", "CDS");
        STANDARD_FEATURE_TYPES.put("atp6", "CDS");
        STANDARD_FEATURE_TYPES.put("atp8", "CDS");
        STANDARD_FEATURE_TYPES.put("cob", "CDS");
        STANDARD_FEATURE_TYPES.put("cytb", "CDS");

        // Standard gene names
        STANDARD_GENE_NAMES.put("nad1", "ND1");
        STANDARD_GENE_NAMES.put("nad2", "ND2");
        STANDARD_GENE_NAMES.put("nad3", "ND3");
        STANDARD_GENE_NAMES.put("nad4", "ND4");
        STANDARD_GENE_NAMES.put("nad4l", "ND4L");
        STANDARD_GENE_NAMES.put("nad5", "ND5");
        STANDARD_GENE_NAMES.put("nad6", "ND6");
        STANDARD_GENE_NAMES.put("cox1", "COX1");
        STANDARD_GENE_NAMES.put("cox2", "COX2");
        STANDARD_GENE_NAMES.put("cox3", "COX3");
        STANDARD_GENE_NAMES.put("atp6", "ATP6");
        STANDARD_GENE_NAMES.put("atp8", "ATP8");
        STANDARD_GENE_NAMES.put("cob", "CYTB");
        STANDARD_GENE_NAMES.put("cytb", "CYTB");

        // Protein names
        PROTEIN_NAME_MAP.put("nad1", "NADH dehydrogenase subunit 1");
        PROTEIN_NAME_MAP.put("nad2", "NADH dehydrogenase subunit 2");
        PROTEIN_NAME_MAP.put("nad3", "NADH dehydrogenase subunit 3");
        PROTEIN_NAME_MAP.put("nad4", "NADH dehydrogenase subunit 4");
        PROTEIN_NAME_MAP.put("nad4l", "NADH dehydrogenase subunit 4L");
        PROTEIN_NAME_MAP.put("nad5", "NADH dehydrogenase subunit 5");
        PROTEIN_NAME_MAP.put("nad6", "NADH dehydrogenase subunit 6");
        PROTEIN_NAME_MAP.put("cox1", "cytochrome c oxidase subunit I");
        PROTEIN_NAME_MAP.put("cox2", "cytochrome c oxidase subunit II");
        PROTEIN_NAME_MAP.put("cox3", "cytochrome c oxidase subunit III");
        PROTEIN_NAME_MAP.put("atp6", "ATP synthase F0 subunit 6");
        PROTEIN_NAME_MAP.put("atp8", "ATP synthase F0 subunit 8");
        PROTEIN_NAME_MAP.put("cob", "cytochrome b");
        PROTEIN_NAME_MAP.put("cytb", "cytochrome b");
    }

    @Override
    public byte[] format(SequenceData sequenceData, AnnotationData annotationData, ConversionOptions options) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            formatToStream(sequenceData, annotationData, outputStream, options);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ConversionException("Error formatting GenBank data: " + e.getMessage(), e);
        }
    }

    @Override
    public void formatToStream(SequenceData sequenceData, AnnotationData annotationData,
                               OutputStream outputStream, ConversionOptions options) throws IOException {
        // Initialize default options if null
        if (options == null) {
            options = ConversionOptions.builder().build();
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            List<Sequence> sequences = sequenceData.getSequences();

            for (int i = 0; i < sequences.size(); i++) {
                Sequence sequence = sequences.get(i);

                // Write the GenBank header
                writeHeader(writer, sequence, options);

                // Write the features
                writeFeatures(writer, sequence,
                        annotationData != null ? annotationData.getAnnotationsForSequence(sequence.getId()) : null,
                        options);

                // Write the sequence data
                writeSequence(writer, sequence, options);

                // Write ending delimiter
                writer.write("//");
                writer.newLine();

                // Add separator between records, except for the last one
                if (i < sequences.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }


    /**
     * Write the GenBank header section.
     */
    private void writeHeader(BufferedWriter writer, Sequence sequence, ConversionOptions options) throws IOException {
        // Get HeaderInfo from sequence, fall back to options if not present
        HeaderInfo headerInfo = sequence.getHeaderInfo();
        if (headerInfo == null && options != null) {
            headerInfo = options.getHeaderInfo();
        }

        // Write LOCUS line
        String locusLine = formatLocus(sequence, options);
        writer.write(locusLine);
        writer.newLine();

        // Write DEFINITION line
        String definition = sequence.getDescription();
        if (definition == null || definition.isEmpty()) {
            definition = sequence.getId();
        }

        // Check if header info contains definition
        if (headerInfo != null && headerInfo.getDefinition() != null && !headerInfo.getDefinition().isEmpty()) {
            definition = headerInfo.getDefinition();
        }

        writeMultiline(writer, "DEFINITION", definition, 12);

        // Write ACCESSION line
        String accession = sequence.getId();
        if (headerInfo != null && headerInfo.getAccessionNumber() != null && !headerInfo.getAccessionNumber().isEmpty()) {
            accession = headerInfo.getAccessionNumber();
        }
        writer.write("ACCESSION   " + accession);
        writer.newLine();

        // Write VERSION line
        String version = sequence.getId() + ".1";
        if (headerInfo != null && headerInfo.getVersion() != null && !headerInfo.getVersion().isEmpty()) {
            version = headerInfo.getVersion();
        }
        writer.write("VERSION     " + version);
        writer.newLine();

        // Write DBLINK lines if present
        if (headerInfo != null && headerInfo.getDbLinks() != null && !headerInfo.getDbLinks().isEmpty()) {
            boolean first = true;
            for (Map.Entry<String, String> link : headerInfo.getDbLinks().entrySet()) {
                if (first) {
                    writer.write("DBLINK      " + link.getKey() + ": " + link.getValue());
                    first = false;
                } else {
                    writer.write("            " + link.getKey() + ": " + link.getValue()); // 12 spaces
                }
                writer.newLine();
            }
        }


        // Write KEYWORDS line
        String keywords = ".";
        if (headerInfo != null && headerInfo.getKeywords() != null && !headerInfo.getKeywords().isEmpty()) {
            keywords = headerInfo.getKeywords();
        }
        writer.write("KEYWORDS    " + keywords);
        writer.newLine();

        // Write SOURCE line
        String organism = sequence.getOrganism();
        // Override with option if specified
        if (options.getOrganism() != null && !options.getOrganism().isEmpty()) {
            organism = options.getOrganism();
        } else if (organism == null || organism.isEmpty()) {
            organism = "Unknown organism";
        }
        writeMultiline(writer, "SOURCE", organism, 12);

        // Write ORGANISM line
        writer.write("  ORGANISM  " + organism);
        writer.newLine();

        // Write taxonomy if available
        List<String> taxonomy = null;
        if (sequence.getTaxonomy() != null && !sequence.getTaxonomy().isEmpty()) {
            taxonomy = sequence.getTaxonomy();
        } else if (headerInfo != null && headerInfo.getTaxonomy() != null && !headerInfo.getTaxonomy().isEmpty()) {
            taxonomy = headerInfo.getTaxonomy();
        }

        if (taxonomy != null) {
            String taxonomyStr = String.join("; ", taxonomy) + ".";
            writeMultiline(writer, "            ", taxonomyStr, 12);
        } else {
            writer.write("            Unclassified.");
            writer.newLine();
        }

        // Write REFERENCES section if available
        if (headerInfo != null && headerInfo.getReferences() != null &&
                !headerInfo.getReferences().isEmpty()) {
            writeReferences(writer, headerInfo.getReferences());
        }

        // Write a single COMMENT section if either comment or assembly data exists
        if (headerInfo != null && (StringUtil.isNotBlank(headerInfo.getComment()) ||
                (headerInfo.getAssemblyData() != null && !headerInfo.getAssemblyData().isEmpty()))) {

            // If there's a general comment, write it first
            if (StringUtil.isNotBlank(headerInfo.getComment())) {
                writeMultiline(writer, "COMMENT", headerInfo.getComment(), 12);

                // Add a separator line if we also have assembly data
                if (headerInfo.getAssemblyData() != null && !headerInfo.getAssemblyData().isEmpty()) {
                    writer.newLine();
                }
            } else {
                // If no general comment but we have assembly data, start with COMMENT line
                writer.write("COMMENT     ");
                writer.newLine();
            }

            // Write assembly data if available
            if (headerInfo.getAssemblyData() != null && !headerInfo.getAssemblyData().isEmpty()) {
                writer.write("            ##Assembly-Data-START##");
                writer.newLine();

                for (Map.Entry<String, String> entry : headerInfo.getAssemblyData().entrySet()) {
                    writer.write("            " + StringUtil.rightPad(entry.getKey(), 20) +
                            ":: " + entry.getValue());
                    writer.newLine();
                }

                writer.write("            ##Assembly-Data-END##");
                writer.newLine();
            }
        }
    }

    /**
     * Write the features section.
     */
    private void writeFeatures(BufferedWriter writer, Sequence sequence,
                               List<Annotation> annotations, ConversionOptions options) throws IOException {
        // Write FEATURES header
        writer.write("FEATURES             Location/Qualifiers");
        writer.newLine();

        // Write source feature
        writeSourceFeature(writer, sequence, options);

        // Handle null annotations
        if (annotations == null) {
            annotations = Collections.emptyList();
        }

        // Get formatting options (default if null)
        OutputFormattingOptions formattingOptions = options.getOutputFormattingOptions();
        if (formattingOptions == null) {
            formattingOptions = OutputFormattingOptions.builder().build();
        }

        // Sort features if requested
        List<Annotation> sortedAnnotations = new ArrayList<>(annotations);
        if (formattingOptions.isSortFeaturesByPosition()) {
            sortedAnnotations.sort(Comparator.comparingInt(Annotation::getStart));
        }

        // Apply filtering if requested
        List<Annotation> filteredAnnotations = filterAnnotations(sortedAnnotations, options.getFilterOptions());

        // Write features
        for (Annotation annotation : filteredAnnotations) {
            writeFeature(writer, annotation, options);

            // Add empty line between features if enabled
            if (formattingOptions.isIncludeEmptyLinesBetweenFeatures()) {
                writer.newLine();
            }
        }
    }

    /**
     * Filter annotations based on the filter options.
     */
    private List<Annotation> filterAnnotations(List<Annotation> annotations, FeatureFilterOptions filterOptions) {
        // If no annotations or no filter options, return as is
        if (annotations == null || annotations.isEmpty() || filterOptions == null) {
            return annotations;
        }

        List<Annotation> filteredAnnotations = new ArrayList<>();

        for (Annotation annotation : annotations) {
            // Skip null annotations
            if (annotation == null) {
                continue;
            }

            // Check include/exclude feature types
            if (filterOptions.getIncludeFeatureTypes() != null &&
                    !filterOptions.getIncludeFeatureTypes().isEmpty() &&
                    !filterOptions.getIncludeFeatureTypes().contains(annotation.getType())) {
                continue;
            }

            if (filterOptions.getExcludeFeatureTypes() != null &&
                    !filterOptions.getExcludeFeatureTypes().isEmpty() &&
                    filterOptions.getExcludeFeatureTypes().contains(annotation.getType())) {
                continue;
            }

            // Check feature length
            int length = annotation.getEnd() - annotation.getStart();
            if (filterOptions.getMinFeatureLength() != null &&
                    length < filterOptions.getMinFeatureLength()) {
                continue;
            }

            if (filterOptions.getMaxFeatureLength() != null &&
                    length > filterOptions.getMaxFeatureLength()) {
                continue;
            }

            // Check qualifiers
            Map<String, List<String>> qualifiers = annotation.getQualifiers();
            if (filterOptions.getIncludeQualifiers() != null &&
                    !filterOptions.getIncludeQualifiers().isEmpty()) {
                boolean hasIncludedQualifier = false;
                for (String qualifier : filterOptions.getIncludeQualifiers()) {
                    if (qualifiers != null && qualifiers.containsKey(qualifier)) {
                        hasIncludedQualifier = true;
                        break;
                    }
                }
                if (!hasIncludedQualifier) {
                    continue;
                }
            }

            if (filterOptions.getExcludeQualifiers() != null &&
                    !filterOptions.getExcludeQualifiers().isEmpty()) {
                boolean hasExcludedQualifier = false;
                for (String qualifier : filterOptions.getExcludeQualifiers()) {
                    if (qualifiers != null && qualifiers.containsKey(qualifier)) {
                        hasExcludedQualifier = true;
                        break;
                    }
                }
                if (hasExcludedQualifier) {
                    continue;
                }
            }

            // If we get here, the annotation passes all filters
            filteredAnnotations.add(annotation);
        }

        return filteredAnnotations;
    }

    /**
     * Write the source feature.
     */
    private void writeSourceFeature(BufferedWriter writer, Sequence sequence, ConversionOptions options) throws IOException {
        // Get sequence length
        String sequenceString = sequence.getSequence();
        int length = sequenceString != null ? sequenceString.length() : 0;

        writer.write("     source          1.." + length);
        writer.newLine();

        // Organism - use options if available
        String organism = options.getOrganism();
        if (organism == null || organism.isEmpty()) {
            organism = sequence.getOrganism();
        }

        if (organism != null && !organism.isEmpty()) {
            writer.write("                     /organism=\"" + organism + "\"");
            writer.newLine();
        }

        // Molecule type - use options if available
        String moleculeType = options.getMoleculeType();
        if (moleculeType == null || moleculeType.isEmpty()) {
            moleculeType = sequence.getMoleculeType();
        }

        if (moleculeType != null && !moleculeType.isEmpty()) {
            writer.write("                     /mol_type=\"" + moleculeType + "\"");
            writer.newLine();
        }

        // Add organelle for mitochondrial genomes
        if (organism != null && organism.toLowerCase().contains("mitochon")) {
            writer.write("                     /organelle=\"mitochondrion\"");
            writer.newLine();
        }
    }

    /**
     * Write a single feature.
     */
    private void writeFeature(BufferedWriter writer, Annotation annotation,
                              ConversionOptions options) throws IOException {
        // Get feature formatting options
        FeatureFormattingOptions formattingOptions = options.getFeatureFormattingOptions();
        if (formattingOptions == null) {
            formattingOptions = FeatureFormattingOptions.builder().build();
        }

        // Get feature type and standardize if requested
        String type = annotation.getType();
        if (formattingOptions.isStandardizeFeatureTypes()) {
            type = standardizeFeatureType(type, formattingOptions.getFeatureTypeMapping());
        }

        // Format the feature line
        String paddedType = StringUtil.rightPad(type, 16);
        String location = formatLocation(annotation);

        writer.write("     " + paddedType + location);
        writer.newLine();

        // Write qualifiers
        writeQualifiers(writer, annotation, options);
    }

    /**
     * Write the qualifiers for a feature.
     */
    private void writeQualifiers(BufferedWriter writer, Annotation annotation,
                                 ConversionOptions options) throws IOException {
        Map<String, List<String>> qualifiers = new HashMap<>();

        // Start with the feature's own qualifiers
        if (annotation.getQualifiers() != null) {
            for (Map.Entry<String, List<String>> entry : annotation.getQualifiers().entrySet()) {
                qualifiers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }

        // Get feature formatting options
        FeatureFormattingOptions formattingOptions = options.getFeatureFormattingOptions();
        if (formattingOptions == null) {
            formattingOptions = FeatureFormattingOptions.builder().build();
        }

        // Add additional qualifiers from options if applicable
        if (formattingOptions.getAdditionalQualifiers() != null) {
            Map<String, Map<String, String>> additionalQualifiers = formattingOptions.getAdditionalQualifiers();

            if (additionalQualifiers.containsKey(annotation.getType())) {
                Map<String, String> typeQualifiers = additionalQualifiers.get(annotation.getType());
                for (Map.Entry<String, String> entry : typeQualifiers.entrySet()) {
                    qualifiers.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                            .add(entry.getValue());
                }
            }
        }

        // Add pseudo qualifier if requested and feature appears to be a pseudogene
        if (formattingOptions.isIncludePseudoQualifier() && isPseudogene(annotation)) {
            qualifiers.put("pseudo", List.of());
        }

        // Write all qualifiers
        for (Map.Entry<String, List<String>> entry : qualifiers.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            // Skip internal qualifiers (ID)
            if ("ID".equals(key) || "Name".equals(key)) {
                continue;
            }



            for (String value : values) {
                if (value == null || value.isEmpty()) {
                    writer.write("                     /" + key);
                } else if (isNumericQualifier(key)) {
                    writer.write("                     /" + key + "=" + value);
                } else {
                    // Escape quotes in value
                    value = value.replace("\"", "\\\"");
                    writer.write("                     /" + key + "=\"" + value + "\"");
                }
                writer.newLine();
            }
        }
    }

    /**
     * Check if a qualifier should be formatted without quotes.
     */
    private boolean isNumericQualifier(String qualifier) {
        return NUMERIC_QUALIFIERS.contains(qualifier);
    }

    /**
     * Check if a feature appears to be a pseudogene.
     */
    private boolean isPseudogene(Annotation annotation) {
        // Check if the feature has a /pseudo qualifier
        if (annotation.getQualifiers() != null &&
                annotation.getQualifiers().containsKey("pseudo")) {
            return true;
        }

        // Check if the feature type is "pseudogene"
        if ("pseudogene".equalsIgnoreCase(annotation.getType())) {
            return true;
        }

        // Check if the qualifiers contain pseudogene indicators
        if (annotation.getQualifiers() != null) {
            for (Map.Entry<String, List<String>> entry : annotation.getQualifiers().entrySet()) {
                for (String value : entry.getValue()) {
                    if (value != null && value.toLowerCase().contains("pseudogene")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Write the sequence section.
     */
    private void writeSequence(BufferedWriter writer, Sequence sequence,
                               ConversionOptions options) throws IOException {
        // Write ORIGIN header
        writer.write("ORIGIN");
        writer.newLine();

        // Get output formatting options
        OutputFormattingOptions formattingOptions = options.getOutputFormattingOptions();
        if (formattingOptions == null) {
            formattingOptions = OutputFormattingOptions.builder().build();
        }

        // Skip sequence data if disabled
        if (!formattingOptions.isIncludeSequence()) {
            return;
        }

        // Get the sequence string
        String sequenceString = sequence.getSequence();
        if (sequenceString == null || sequenceString.isEmpty()) {
            return;
        }


        // Apply case formatting
        if (formattingOptions.isLowercaseSequence()) {
            sequenceString = sequenceString.toLowerCase();
        } else {
            sequenceString = sequenceString.toUpperCase();
        }

        // Get line width from options
        int lineWidth = formattingOptions.getSequenceLineWidth();
        if (lineWidth <= 0) {
            lineWidth = DEFAULT_SEQUENCE_LINE_WIDTH;
        }

        // Write sequence in formatted lines
        for (int i = 0; i < sequenceString.length(); i += lineWidth) {
            // Format line number
            String lineNumber = StringUtil.leftPad(String.valueOf(i + 1), 9);
            writer.write(lineNumber + " ");

            // Get sequence chunk for this line
            int end = Math.min(i + lineWidth, sequenceString.length());
            String lineSeq = sequenceString.substring(i, end);

            // Write in groups of 10 bases
            for (int j = 0; j < lineSeq.length(); j += 10) {
                int groupEnd = Math.min(j + 10, lineSeq.length());
                writer.write(lineSeq.substring(j, groupEnd));

                // Only add space if not the last group
                if (groupEnd < lineSeq.length()) {
                    writer.write(" ");
                }
            }

            writer.newLine();
        }
    }

    /**
     * Format the LOCUS line.
     */
    private String formatLocus(Sequence sequence, ConversionOptions options) {
        String name = StringUtil.truncate(sequence.getName(), 16);
        name = StringUtil.rightPad(name, 16);

        // Calculate length from sequence
        String sequenceString = sequence.getSequence();
        long length = sequenceString != null ? sequenceString.length() : 0;
        String lengthStr = StringUtil.leftPad(String.valueOf(length), 11);

        // Get molecule type with preference for options
        String moleculeType = options.getMoleculeType();
        if (moleculeType == null || moleculeType.isEmpty()) {
            moleculeType = sequence.getMoleculeType();
            if (moleculeType == null || moleculeType.isEmpty()) {
                moleculeType = "DNA";
            }
        }
        moleculeType = StringUtil.rightPad(moleculeType, 6);

        // Get topology with preference for options
        String topology = options.getTopology();
        if (topology == null || topology.isEmpty()) {
            topology = sequence.getTopology();
            if (topology == null || topology.isEmpty()) {
                topology = "linear";
            }
        }
        String formattedTopology = "circular".equals(topology.toLowerCase()) ? "circular " : "linear   ";

        // Get division with preference for options
        String division = options.getDivision();
        if (division == null || division.isEmpty()) {
            division = sequence.getDivision();
            if (division == null || division.isEmpty()) {
                division = "   ";
            }
        }

        // Ensure division is exactly 3 characters
        if (division.length() > 3) {
            division = division.substring(0, 3);
        } else if (division.length() < 3) {
            division = StringUtil.rightPad(division, 3);
        }

        // Get date
        String date = formatDate(sequence.getDate());

        return String.format("LOCUS       %s%s bp    %s%s%s%s",
                name, lengthStr, moleculeType, formattedTopology, division, date);
    }

    /**
     * Format a date for the LOCUS line.
     */
    private String formatDate(Date date) {
        if (date == null) {
            date = new Date();
        }
        return " " + DATE_FORMAT.format(date);
    }

    /**
     * Format a location string for a feature.
     */
    private String formatLocation(Annotation annotation) {
        StringBuilder location = new StringBuilder();

        // Handle strand
        if (annotation.getStrand() < 0) {
            location.append("complement(");
        }

        // Adjust for 1-based coordinates in GenBank
        int start = annotation.getStart() + 1;
        int end = annotation.getEnd();

        // Simple location
        location.append(start).append("..").append(end);

        // Close complement if needed
        if (annotation.getStrand() < 0) {
            location.append(")");
        }

        return location.toString();
    }

    /**
     * Standardize a feature type.
     */
    private String standardizeFeatureType(String featureType, Map<String, String> customMapping) {
        if (featureType == null) {
            return "misc_feature";
        }

        // Check custom mapping first
        if (customMapping != null && customMapping.containsKey(featureType)) {
            return customMapping.get(featureType);
        }

        // Then check standard mapping
        String lowerType = featureType.toLowerCase();
        return STANDARD_FEATURE_TYPES.getOrDefault(lowerType, featureType);
    }

    /**
     * Write references information.
     */
    private void writeReferences(BufferedWriter writer, List<ReferenceInfo> references) throws IOException {
        for (ReferenceInfo ref : references) {
            // Write REFERENCE line
            writer.write("REFERENCE   " + (ref.getNumber() != null ? ref.getNumber() : "1"));
            if (ref.getBaseRange() != null && !ref.getBaseRange().isEmpty()) {
                writer.write("  " + ref.getBaseRange());
            }
            writer.newLine();

            // Write AUTHORS
            if (ref.getAuthors() != null && !ref.getAuthors().isEmpty()) {
                String authors = String.join(", ", ref.getAuthors());
                writeMultiline(writer, "  AUTHORS", authors, 12);
            }

            // Write TITLE
            if (ref.getTitle() != null && !ref.getTitle().isEmpty()) {
                writeMultiline(writer, "  TITLE", ref.getTitle(), 12);
            }

            // Write JOURNAL
            if (ref.getJournal() != null && !ref.getJournal().isEmpty()) {
                writeMultiline(writer, "  JOURNAL", ref.getJournal(), 12);
            }

            // Write pub status if available
            if (ref.getPubStatus() != null && !ref.getPubStatus().isEmpty()) {
                writer.write("   " + ref.getPubStatus());
                writer.newLine();
            }
        }
    }

    /**
     * Write multiline fields, properly indented.
     */
    private void writeMultiline(BufferedWriter writer, String key, String value, int keyWidth) throws IOException {
        if (value == null || value.isEmpty()) {
            return;
        }

        String paddedKey = StringUtil.rightPad(key, keyWidth);
        String firstLine = paddedKey + value;

        // If it fits on one line
        if (firstLine.length() <= DEFAULT_LINE_WIDTH) {
            writer.write(firstLine);
            writer.newLine();
            return;
        }

        // First line with key
        int firstLineContentWidth = DEFAULT_LINE_WIDTH - keyWidth;
        int endIndex = Math.min(firstLineContentWidth, value.length());
        writer.write(paddedKey + value.substring(0, endIndex));
        writer.newLine();

        // Remaining lines
        if (endIndex < value.length()) {
            String remainingContent = value.substring(endIndex);
            String indent = StringUtil.repeat(" ", keyWidth);

            for (int i = 0; i < remainingContent.length(); i += (DEFAULT_LINE_WIDTH - keyWidth)) {
                int end = Math.min(i + (DEFAULT_LINE_WIDTH - keyWidth), remainingContent.length());
                writer.write(indent + remainingContent.substring(i, end));
                writer.newLine();
            }
        }
    }
}