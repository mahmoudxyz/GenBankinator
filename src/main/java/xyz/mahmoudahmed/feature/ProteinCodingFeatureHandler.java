package xyz.mahmoudahmed.feature;

import xyz.mahmoudahmed.parsers.FastaHeaderInfo;
import xyz.mahmoudahmed.translator.Translator;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Handler for protein-coding features
 * Enhanced to handle special gene name variations
 */
public class ProteinCodingFeatureHandler extends AbstractFeatureHandler {
    private static final Map<String, String> PROTEIN_NAME_MAP = createProteinNameMap();
    private static final Set<String> KNOWN_PROTEIN_GENES = createKnownProteinGenes();

    // Patterns to match gene name variations
    private static final Pattern COX_PATTERN = Pattern.compile("cox\\d+(-[a-z])?");
    private static final Pattern NAD_PATTERN = Pattern.compile("nad\\d+(_\\d+)?");
    private static final Pattern ND_PATTERN = Pattern.compile("nd\\d+(_\\d+)?");

    private static Map<String, String> createProteinNameMap() {
        Map<String, String> map = new HashMap<>();

        // Basic gene names
        map.put("nad1", "NADH dehydrogenase subunit 1");
        map.put("nad2", "NADH dehydrogenase subunit 2");
        map.put("nad3", "NADH dehydrogenase subunit 3");
        map.put("nad4", "NADH dehydrogenase subunit 4");
        map.put("nad4l", "NADH dehydrogenase subunit 4L");
        map.put("nad5", "NADH dehydrogenase subunit 5");
        map.put("nad6", "NADH dehydrogenase subunit 6");
        map.put("nd1", "NADH dehydrogenase subunit 1");
        map.put("nd2", "NADH dehydrogenase subunit 2");
        map.put("nd3", "NADH dehydrogenase subunit 3");
        map.put("nd4", "NADH dehydrogenase subunit 4");
        map.put("nd4l", "NADH dehydrogenase subunit 4L");
        map.put("nd5", "NADH dehydrogenase subunit 5");
        map.put("nd6", "NADH dehydrogenase subunit 6");
        map.put("cox1", "cytochrome c oxidase subunit I");
        map.put("cox2", "cytochrome c oxidase subunit II");
        map.put("cox3", "cytochrome c oxidase subunit III");
        map.put("atp6", "ATP synthase F0 subunit 6");
        map.put("atp8", "ATP synthase F0 subunit 8");
        map.put("cob", "cytochrome b");
        map.put("cytb", "cytochrome b");

        // Add known variations explicitly
        map.put("cox1-a", "cytochrome c oxidase subunit I, copy A");
        map.put("cox1-b", "cytochrome c oxidase subunit I, copy B");
        map.put("cox1-c", "cytochrome c oxidase subunit I, copy C");

        return Collections.unmodifiableMap(map);
    }

    private static Set<String> createKnownProteinGenes() {
        Set<String> genes = new HashSet<>(Arrays.asList(
                "cds", "gene", "exon", "mrna",
                "nad1", "nad2", "nad3", "nad4", "nad4l", "nad5", "nad6",
                "nd1", "nd2", "nd3", "nd4", "nd4l", "nd5", "nd6",
                "cox1", "cox2", "cox3", "atp6", "atp8", "cob", "cytb", "gpi"
        ));
        return Collections.unmodifiableSet(genes);
    }

    @Override
    public String getFeatureType() {
        return "CDS";
    }

    @Override
    public boolean canHandle(String featureName) {
        if (featureName == null) return false;

        String lower = featureName.toLowerCase();

        // Check known protein genes
        if (KNOWN_PROTEIN_GENES.contains(lower)) {
            return true;
        }

        // Check for gene variations with suffix patterns
        if (COX_PATTERN.matcher(lower).matches() ||
                NAD_PATTERN.matcher(lower).matches() ||
                ND_PATTERN.matcher(lower).matches()) {
            return true;
        }

        // Check pattern for generic protein genes (gpX) or ORFs (orfXXX)
        return lower.startsWith("gp") || lower.matches("orf\\d+");
    }

    @Override
    public Map<String, List<String>> buildQualifiers(FastaHeaderInfo header, String sequence, Translator translator) {
        Map<String, List<String>> qualifiers = super.buildQualifiers(header, sequence, translator);

        // Add standard protein coding qualifiers
        qualifiers.put("codon_start", Collections.singletonList("1"));
        qualifiers.put("transl_table", Collections.singletonList("5")); // Could be parameterized

        // Add product name
        String lowerFeature = header.getFeatureTypeLowerCase();

        // Check for predefined product name first
        if (PROTEIN_NAME_MAP.containsKey(lowerFeature)) {
            qualifiers.put("product", Collections.singletonList(PROTEIN_NAME_MAP.get(lowerFeature)));
        }
        // Handle variations with numerical suffixes (nad5_1, nad4_0, etc.)
        else if (lowerFeature.matches("(nad|nd)\\d+_\\d+")) {
            // Extract the base gene name and copy number
            String[] parts = lowerFeature.split("_");
            String baseGene = parts[0];
            String copyNumber = parts[1];

            // Get the base product name
            String baseProduct = PROTEIN_NAME_MAP.getOrDefault(baseGene,
                    baseGene.startsWith("nad") ?
                            "NADH dehydrogenase subunit " + baseGene.substring(3) :
                            "NADH dehydrogenase subunit " + baseGene.substring(2));

            qualifiers.put("product", Collections.singletonList(baseProduct + ", copy " + copyNumber));
        }
        // Handle cox variations with letter suffixes (cox1-a, etc.)
        else if (lowerFeature.matches("cox\\d+-[a-z]")) {
            // Extract the base gene name and copy letter
            String[] parts = lowerFeature.split("-");
            String baseGene = parts[0];
            String copyLetter = parts[1].toUpperCase();

            // Get the base product name
            String baseProduct = PROTEIN_NAME_MAP.getOrDefault(baseGene,
                    "cytochrome c oxidase subunit " +
                            (baseGene.equals("cox1") ? "I" :
                                    baseGene.equals("cox2") ? "II" : "III"));

            qualifiers.put("product", Collections.singletonList(baseProduct + ", copy " + copyLetter));
        }
        // If no standard product name, generate a generic one for gp/orf genes
        else if (lowerFeature.startsWith("gp")) {
            qualifiers.put("product", Collections.singletonList("gene product " + lowerFeature.substring(2)));
        } else if (lowerFeature.matches("orf\\d+")) {
            qualifiers.put("product", Collections.singletonList("hypothetical protein"));
        } else {
            // For any other unknown protein-coding gene, use a generic name
            qualifiers.put("product", Collections.singletonList("mitochondrial protein " + lowerFeature));
        }

        // Add protein_id (placeholder)
        qualifiers.put("protein_id", Collections.singletonList(""));

        // Generate protein translation
        if (translator != null && sequence != null && !sequence.isEmpty()) {
            String translation = translator.translate(sequence, false); // false = input is DNA

            // Format translation in chunks of 60 characters
            String formattedTranslation = formatTranslation(translation);
            qualifiers.put("translation", Collections.singletonList(formattedTranslation));
        }

        return qualifiers;
    }

    // Helper for formatting translations
    private String formatTranslation(String translation) {
        if (translation == null || translation.isEmpty()) {
            return "";
        }

        int chunkSize = 60;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < translation.length(); i += chunkSize) {
            if (i > 0) {
                sb.append("\n                     ");
            }

            int end = Math.min(i + chunkSize, translation.length());
            sb.append(translation.substring(i, end));
        }

        return sb.toString();
    }
}