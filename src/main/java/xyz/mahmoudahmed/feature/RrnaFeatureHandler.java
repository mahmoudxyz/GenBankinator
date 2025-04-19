package xyz.mahmoudahmed.feature;

import xyz.mahmoudahmed.parsers.FastaHeaderInfo;
import xyz.mahmoudahmed.translator.Translator;

import java.util.*;

/**
 * Handler for rRNA features
 */
public class RrnaFeatureHandler extends AbstractFeatureHandler {
    private static final Map<String, String> RRNA_PRODUCT_MAP = createRrnaProductMap();

    private static Map<String, String> createRrnaProductMap() {
        Map<String, String> map = new HashMap<>();
        map.put("rrn12", "12S ribosomal RNA");
        map.put("rrn16", "16S ribosomal RNA");
        map.put("rrns", "12S ribosomal RNA");
        map.put("rrnl", "16S ribosomal RNA");

        return Collections.unmodifiableMap(map);
    }

    @Override
    public String getFeatureType() {
        return "rRNA";
    }

    @Override
    public boolean canHandle(String featureName) {
        return featureName != null && featureName.toLowerCase().startsWith("rrn");
    }

    @Override
    public Map<String, List<String>> buildQualifiers(FastaHeaderInfo header, String sequence, Translator translator) {
        Map<String, List<String>> qualifiers = super.buildQualifiers(header, sequence, translator);

        // Add product qualifier
        Optional.ofNullable(RRNA_PRODUCT_MAP.get(header.featureType().toLowerCase()))
                .ifPresent(product -> qualifiers.put("product", Collections.singletonList(product)));

        return qualifiers;
    }
}