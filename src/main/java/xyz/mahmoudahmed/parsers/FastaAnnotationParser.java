package xyz.mahmoudahmed.parsers;

import xyz.mahmoudahmed.api.AnnotationParser;
import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.model.AnnotationData;
import xyz.mahmoudahmed.exception.ParsingException;
import xyz.mahmoudahmed.feature.FeatureHandler;
import xyz.mahmoudahmed.feature.FeatureHandlerRegistry;
import xyz.mahmoudahmed.translator.Translator;
import xyz.mahmoudahmed.translator.TranslatorFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Functional implementation of AnnotationParser for FASTA annotation files.
 * This parser generates annotations in NCBI GenBank format.
 */
public class FastaAnnotationParser implements AnnotationParser {

    private static final List<String> SUPPORTED_FORMATS =
            List.of("FASTA", "FA", "FNA", "FAA", "FFN");

    private static final Pattern HEADER_PATTERN = Pattern.compile(
            "([^;]+);\\s*([0-9]+-[0-9]+);\\s*([+\\-]);\\s*([^\\(]+)(?:\\(([^\\)]+)\\))?.*"
    );

    /**
     * Immutable record to store sequence data
     */
    private record SequenceData(
            FastaHeaderInfo header,
            String sequence
    ) {}

    @Override
    public AnnotationData parse(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return parseFromReader(reader);
        } catch (IOException e) {
            throw new ParsingException("Failed to read FASTA annotation file: " + e.getMessage(), e);
        }
    }

    @Override
    public AnnotationData parse(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseFromReader(reader);
        } catch (IOException e) {
            throw new ParsingException("Failed to read FASTA annotation from input stream: " + e.getMessage(), e);
        }
    }

    /**
     * Pure function to parse reader content into AnnotationData
     */
    private AnnotationData parseFromReader(BufferedReader reader) throws IOException {
        List<SequenceData> sequenceDataList = extractSequenceData(reader);

        // Transform sequence data to annotations
        Map<String, List<Annotation>> annotationsMap = createAnnotationsMap(sequenceDataList);

        // Process annotations to add gene features
        Map<String, List<Annotation>> processedMap = processFeatures(annotationsMap);

        // Build the final AnnotationData
        return AnnotationData.builder()
                .addAnnotations(processedMap)
                .build();
    }

    /**
     * Pure function to extract sequence data from reader
     */
    private List<SequenceData> extractSequenceData(BufferedReader reader) throws IOException {
        List<String> lines = reader.lines().collect(Collectors.toList());

        // Group lines by feature
        List<List<String>> groupedLines = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith(">")) {
                if (!currentGroup.isEmpty()) {
                    groupedLines.add(new ArrayList<>(currentGroup));
                }
                currentGroup = new ArrayList<>();
            }
            currentGroup.add(line);
        }

        if (!currentGroup.isEmpty()) {
            groupedLines.add(currentGroup);
        }

        // Convert each group to SequenceData
        return groupedLines.stream()
                .map(this::convertToSequenceData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Pure function to convert a group of lines to SequenceData
     */
    private SequenceData convertToSequenceData(List<String> lines) {
        if (lines.isEmpty() || !lines.get(0).startsWith(">")) {
            return null;
        }

        FastaHeaderInfo header = parseHeaderLine(lines.get(0));
        if (header == null) {
            return null;
        }

        String sequence = lines.stream()
                .skip(1)  // Skip header line
                .collect(Collectors.joining());

        return new SequenceData(header, sequence);
    }

    /**
     * Pure function to create annotations map from sequence data
     */
    private Map<String, List<Annotation>> createAnnotationsMap(List<SequenceData> sequenceDataList) {
        return sequenceDataList.stream()
                .map(this::createAnnotation)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Annotation::getSequenceId,
                        Collectors.toList()
                ));
    }

    /**
     * Pure function to create an annotation from sequence data
     */
    private Annotation createAnnotation(SequenceData data) {
        FastaHeaderInfo header = data.header();

        // Get the appropriate feature handler
        FeatureHandler handler = FeatureHandlerRegistry.getHandlerFor(header.featureType());

        // Determine the correct feature type
        String featureType = handler.getFeatureType();

        // Create translator for protein translations
        Translator translator = TranslatorFactory.createInvertebrateMitochondrialTranslator();

        // Build qualifiers
        Map<String, List<String>> qualifiers = new HashMap<>();
        qualifiers.put("ID", Collections.singletonList(UUID.randomUUID().toString().substring(0, 8)));

        // Add feature-specific qualifiers
        qualifiers.putAll(handler.buildQualifiers(header, data.sequence(), translator));

        return Annotation.builder()
                .type(featureType)
                .start(header.start() - 1)
                .end(header.end())
                .strand(header.strand())
                .sequenceId(header.seqId())
                .featureId(qualifiers.get("ID").get(0))
                .qualifiers(qualifiers)
                .build();
    }

    /**
     * Pure function to process features, adding gene features as needed
     */
    private Map<String, List<Annotation>> processFeatures(Map<String, List<Annotation>> annotationsMap) {
        return annotationsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> processFeatureGroup(entry.getValue())
                ));
    }

    /**
     * Process a group of features, adding gene features as needed
     */
    private List<Annotation> processFeatureGroup(List<Annotation> features) {
        // Sort features by position
        List<Annotation> sortedFeatures = features.stream()
                .sorted(Comparator.comparingInt(Annotation::getStart))
                .collect(Collectors.toList());

        // Process each feature and add gene features as needed
        List<Annotation> result = new ArrayList<>();

        for (Annotation feature : sortedFeatures) {
            // Get handler for this feature type
            FeatureHandler handler = FeatureHandlerRegistry.getHandlerFor(
                    feature.getQualifiers().get("gene") != null ?
                            feature.getQualifiers().get("gene").get(0) :
                            feature.getType());

            // Get additional features (like gene features)
            List<Annotation> additionalFeatures = handler.createAdditionalFeatures(feature);

            // Add gene features first (in order)
            result.addAll(additionalFeatures);

            // Then add the feature itself
            result.add(feature);
        }

        return result;
    }

    /**
     * Pure function to parse a header line into FastaHeaderInfo
     */
    private static FastaHeaderInfo parseHeaderLine(String line) {
        Matcher matcher = HEADER_PATTERN.matcher(line.substring(1));
        if (!matcher.matches()) {
            return null;
        }

        try {
            String seqId = matcher.group(1).trim();
            String rawFeatureType = matcher.group(4).trim();
            String featureType = standardizeGeneName(rawFeatureType);

            // Check for complement strand
            boolean isComplement = line.toLowerCase().contains("complement");

            String[] positions = matcher.group(2).split("-");
            int rawStart = Integer.parseInt(positions[0]);
            int rawEnd = Integer.parseInt(positions[1]);

            int strand = matcher.group(3).equals("+") ? 1 : -1;
            String qualifier = matcher.groupCount() > 4 ? matcher.group(5) : null;

            return new FastaHeaderInfo(seqId, featureType, rawStart, rawEnd, strand, qualifier, isComplement);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Pure function to standardize gene names
     */
    private static String standardizeGeneName(String featureType) {
        String lower = featureType.toLowerCase();

        // Handle standard NADH dehydrogenase genes
        if (lower.startsWith("nad") && lower.length() > 3) {
            String suffix = lower.substring(3);
            if (suffix.equals("4l")) {
                return "ND4L";
            } else if (suffix.matches("\\d")) {
                return "ND" + suffix;
            }
        }

        // Handle standard cytochrome oxidase genes
        if (lower.startsWith("cox") && lower.length() > 3) {
            String suffix = lower.substring(3);
            if (suffix.matches("\\d")) {
                return "COX" + suffix;
            }
        }

        // Handle ATP synthase genes
        if (lower.startsWith("atp") && lower.length() > 3) {
            String suffix = lower.substring(3);
            if (suffix.matches("\\d")) {
                return "ATP" + suffix;
            }
        }

        // Handle cytochrome b
        if (lower.equals("cob") || lower.equals("cytb")) {
            return "CYTB";
        }

        // Handle ribosomal RNA genes
        if (lower.equals("rrns")) {
            return "rrn12";
        }
        if (lower.equals("rrnl")) {
            return "rrn16";
        }

        // Handle tRNA genes with duplicates
        if (lower.startsWith("trn") && lower.length() > 3) {
            return lower; // Keep tRNA original name format
        }

        // Return input with standardized case if no match
        return featureType;
    }

    @Override
    public boolean supportsFormat(String format) {
        return SUPPORTED_FORMATS.contains(format.toUpperCase());
    }
}