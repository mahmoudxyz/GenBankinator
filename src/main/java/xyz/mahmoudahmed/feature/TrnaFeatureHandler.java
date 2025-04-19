package xyz.mahmoudahmed.feature;


import xyz.mahmoudahmed.parsers.FastaHeaderInfo;
import xyz.mahmoudahmed.translator.Translator;

import java.util.*;

/**
 * Handler for tRNA features
 */
public class TrnaFeatureHandler extends AbstractFeatureHandler {
    private static final Map<String, String> TRNA_PRODUCT_MAP = createTrnaProductMap();

    private static Map<String, String> createTrnaProductMap() {
        Map<String, String> map = new HashMap<>();
        map.put("trnf", "tRNA-Phe");
        map.put("trnv", "tRNA-Val");
        map.put("trnl", "tRNA-Leu");
        map.put("trni", "tRNA-Ile");
        map.put("trnq", "tRNA-Gln");
        map.put("trnm", "tRNA-Met");
        map.put("trnw", "tRNA-Trp");
        map.put("trna", "tRNA-Ala");
        map.put("trnn", "tRNA-Asn");
        map.put("trnc", "tRNA-Cys");
        map.put("trny", "tRNA-Tyr");
        map.put("trns", "tRNA-Ser");
        map.put("trnd", "tRNA-Asp");
        map.put("trnk", "tRNA-Lys");
        map.put("trng", "tRNA-Gly");
        map.put("trnr", "tRNA-Arg");
        map.put("trnh", "tRNA-His");
        map.put("trne", "tRNA-Glu");
        map.put("trnt", "tRNA-Thr");
        map.put("trnp", "tRNA-Pro");

        return Collections.unmodifiableMap(map);
    }

    @Override
    public String getFeatureType() {
        return "tRNA";
    }

    @Override
    public boolean canHandle(String featureName) {
        return featureName != null && featureName.toLowerCase().startsWith("trn");
    }

    @Override
    public Map<String, List<String>> buildQualifiers(FastaHeaderInfo header, String sequence, Translator translator) {
        Map<String, List<String>> qualifiers = super.buildQualifiers(header, sequence, translator);

        // Add product qualifier
        Optional.ofNullable(TRNA_PRODUCT_MAP.get(header.featureType().toLowerCase()))
                .ifPresent(product -> qualifiers.put("product", Collections.singletonList(product)));

        // Add anticodon qualifier if available
        Optional.ofNullable(header.qualifier())
                .filter(q -> q.length() == 3)
                .ifPresent(anticodon -> qualifiers.put("note",
                        Collections.singletonList("anticodon:" + anticodon.toLowerCase())));

        return qualifiers;
    }
}