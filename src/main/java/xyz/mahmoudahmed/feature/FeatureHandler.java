package xyz.mahmoudahmed.feature;

import xyz.mahmoudahmed.model.Annotation;
import xyz.mahmoudahmed.parsers.FastaHeaderInfo;
import xyz.mahmoudahmed.translator.Translator;

import java.util.*;

/**
 * Interface defining a feature type handler
 * Follows the Strategy Pattern
 */
public interface FeatureHandler {
    /**
     * Returns the feature type for GenBank
     */
    String getFeatureType();

    /**
     * Checks if this handler can process the given feature name
     */
    boolean canHandle(String featureName);

    /**
     * Builds qualifiers for this feature type
     */
    Map<String, List<String>> buildQualifiers(FastaHeaderInfo header, String sequence, Translator translator);

    /**
     * Creates a gene feature if needed for this feature type
     * Returns empty list if no gene feature is needed
     */
    default List<Annotation> createAdditionalFeatures(Annotation feature) {
        return new ArrayList<>();
    }
}