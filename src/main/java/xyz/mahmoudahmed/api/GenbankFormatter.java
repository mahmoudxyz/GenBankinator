package xyz.mahmoudahmed.api;

import xyz.mahmoudahmed.model.AnnotationData;
import xyz.mahmoudahmed.model.ConversionOptions;
import xyz.mahmoudahmed.model.SequenceData;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for formatting GenBank data.
 */
public interface GenbankFormatter {
    /**
     * Format sequence and annotation data to GenBank format.
     *
     * @param sequenceData The sequence data
     * @param annotationData The annotation data
     * @param options The conversion options
     * @return The formatted GenBank data
     */
    byte[] format(SequenceData sequenceData, AnnotationData annotationData, ConversionOptions options);

    /**
     * Format sequence and annotation data to GenBank format and write to a stream.
     *
     * @param sequenceData The sequence data
     * @param annotationData The annotation data
     * @param outputStream The output stream to write to
     * @param options The conversion options
     * @throws IOException If an I/O error occurs
     */
    void formatToStream(SequenceData sequenceData, AnnotationData annotationData,
                        OutputStream outputStream, ConversionOptions options) throws IOException;
}
