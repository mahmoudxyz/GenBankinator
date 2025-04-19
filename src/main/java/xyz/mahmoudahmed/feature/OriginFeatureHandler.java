package xyz.mahmoudahmed.feature;


import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.parsers.FastaHeaderInfo;
import xyz.mahmoudahmed.translator.Translator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for origin features (OL, OH)
 */
public class OriginFeatureHandler extends AbstractFeatureHandler {
    @Override
    public String getFeatureType() {
        return "misc_feature";
    }

    @Override
    public boolean canHandle(String featureName) {
        return featureName != null &&
                (featureName.equalsIgnoreCase("OL") || featureName.equalsIgnoreCase("OH"));
    }

    @Override
    public Map<String, List<String>> buildQualifiers(FastaHeaderInfo header, String sequence, Translator translator) {
        Map<String, List<String>> qualifiers = new HashMap<>();

        if (header.featureType().equalsIgnoreCase("OL")) {
            qualifiers.put("note", Collections.singletonList("origin of light strand replication (OL)"));
        } else if (header.featureType().equalsIgnoreCase("OH")) {
            qualifiers.put("note", Collections.singletonList("origin of heavy strand replication (OH)"));
        }

        return qualifiers;
    }

    @Override
    public List<Annotation> createAdditionalFeatures(Annotation feature) {
        // No gene feature needed for origin features
        return Collections.emptyList();
    }
}