package xyz.mahmoudahmed.feature;

import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.parsers.FastaHeaderInfo;
import xyz.mahmoudahmed.translator.Translator;

import java.util.*;

/**
 * Base implementation for feature handlers
 */
public abstract class AbstractFeatureHandler implements FeatureHandler {
    @Override
    public boolean canHandle(String featureName) {
        return false; // Default implementation - subclasses should override
    }

    @Override
    public Map<String, List<String>> buildQualifiers(FastaHeaderInfo header, String sequence, Translator translator) {
        Map<String, List<String>> qualifiers = new HashMap<>();
        qualifiers.put("gene", Collections.singletonList(header.featureType()));
        return qualifiers;
    }

    @Override
    public List<Annotation> createAdditionalFeatures(Annotation feature) {
        // By default, create a gene feature for this feature
        if (!feature.getType().equals("gene")) {
            Annotation geneFeature = createGeneFeature(feature);
            return Collections.singletonList(geneFeature);
        }
        return Collections.emptyList();
    }

    // Helper to create a gene feature
    protected Annotation createGeneFeature(Annotation feature) {
        Map<String, List<String>> geneQualifiers = new HashMap<>();
        geneQualifiers.put("gene", Collections.singletonList(feature.getQualifiers().get("gene").get(0)));

        return Annotation.builder()
                .type("gene")
                .start(feature.getStart())
                .end(feature.getEnd())
                .strand(feature.getStrand())
                .sequenceId(feature.getSequenceId())
                .featureId(UUID.randomUUID().toString().substring(0, 8))
                .qualifiers(geneQualifiers)
                .build();
    }
}