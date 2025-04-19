package xyz.mahmoudahmed.model;

import java.util.*;

/**
 * Default implementation of AnnotationData.
 */
public class DefaultAnnotationData implements AnnotationData {
    private final Map<String, List<Annotation>> annotationsBySequence;
    private final int totalCount;

    private DefaultAnnotationData(Builder builder) {
        // Create an immutable copy of the annotations
        Map<String, List<Annotation>> map = new HashMap<>();
        int count = 0;

        for (Map.Entry<String, List<Annotation>> entry : builder.annotationsBySequence.entrySet()) {
            List<Annotation> annotations = new ArrayList<>(entry.getValue());
            map.put(entry.getKey(), Collections.unmodifiableList(annotations));
            count += annotations.size();
        }

        this.annotationsBySequence = Collections.unmodifiableMap(map);
        this.totalCount = count;
    }

    @Override
    public Map<String, List<Annotation>> getAnnotationsBySequence() {
        return annotationsBySequence;
    }

    @Override
    public List<Annotation> getAnnotationsForSequence(String sequenceId) {
        return annotationsBySequence.getOrDefault(sequenceId, Collections.emptyList());
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Builder implementation for DefaultAnnotationData.
     */
    static class Builder implements AnnotationData.Builder {
        private final Map<String, List<Annotation>> annotationsBySequence = new HashMap<>();

        @Override
        public Builder addAnnotation(Annotation annotation) {
            if (annotation != null) {
                String sequenceId = annotation.getSequenceId();
                annotationsBySequence.computeIfAbsent(sequenceId, k -> new ArrayList<>()).add(annotation);
            }
            return this;
        }

        @Override
        public Builder addAnnotations(String sequenceId, List<Annotation> annotations) {
            if (sequenceId != null && annotations != null) {
                annotationsBySequence.computeIfAbsent(sequenceId, k -> new ArrayList<>()).addAll(annotations);
            }
            return this;
        }

        @Override
        public Builder addAnnotations(Map<String, List<Annotation>> annotationMap) {
            if (annotationMap != null) {
                for (Map.Entry<String, List<Annotation>> entry : annotationMap.entrySet()) {
                    addAnnotations(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        @Override
        public AnnotationData build() {
            return new DefaultAnnotationData(this);
        }
    }
}
