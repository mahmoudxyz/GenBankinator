package xyz.mahmoudahmed.model;

import java.util.List;
import java.util.Map;

/**
 * Container for annotation data.
 */
public interface AnnotationData {
    /**
     * Get the annotations grouped by sequence ID.
     *
     * @return Map of sequence IDs to their annotations
     */
    Map<String, List<Annotation>> getAnnotationsBySequence();

    /**
     * Get annotations for a specific sequence.
     *
     * @param sequenceId The sequence ID
     * @return List of annotations for the sequence, or an empty list if none
     */
    List<Annotation> getAnnotationsForSequence(String sequenceId);

    /**
     * Get the total number of annotations.
     *
     * @return The total number of annotations
     */
    int getTotalCount();

    /**
     * Create a builder for AnnotationData.
     *
     * @return A new builder
     */
    static Builder builder() {
        return new DefaultAnnotationData.Builder();
    }

    /**
     * Builder for AnnotationData.
     */
    interface Builder {
        /**
         * Add an annotation.
         *
         * @param annotation The annotation to add
         * @return This builder
         */
        Builder addAnnotation(Annotation annotation);

        /**
         * Add multiple annotations for a sequence.
         *
         * @param sequenceId The sequence ID
         * @param annotations The annotations to add
         * @return This builder
         */
        Builder addAnnotations(String sequenceId, List<Annotation> annotations);

        /**
         * Add all annotations from a map.
         *
         * @param annotationMap Map of sequence IDs to their annotations
         * @return This builder
         */
        Builder addAnnotations(Map<String, List<Annotation>> annotationMap);

        /**
         * Build the AnnotationData.
         *
         * @return The built AnnotationData
         */
        AnnotationData build();
    }
}
