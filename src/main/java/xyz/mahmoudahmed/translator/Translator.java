package xyz.mahmoudahmed.translator;

import java.util.List;

/**
 * Interface for translation functionality
 */
public interface Translator {
    String translate(String sequence, boolean isRNA);
    List<String> findOpenReadingFrames(String sequence, boolean isRNA);
}