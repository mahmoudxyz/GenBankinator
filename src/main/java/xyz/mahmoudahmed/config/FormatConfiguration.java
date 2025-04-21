package xyz.mahmoudahmed.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Configuration for file formats.
 */
public class FormatConfiguration {
    private final Map<String, String> extensionFormatMap = new HashMap<>();
    private final Properties properties = new Properties();

    /**
     * Constructor that loads configuration from the default file.
     */
    public FormatConfiguration() {
        this("formats.properties");
    }

    /**
     * Constructor that loads configuration from the specified file.
     *
     * @param configPath The path to the configuration file
     */
    public FormatConfiguration(String configPath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (is != null) {
                properties.load(is);
                loadExtensionMappings();
            } else {
                loadDefaultExtensionMappings();
            }
        } catch (IOException e) {
            loadDefaultExtensionMappings();
        }
    }

    /**
     * Gets the format associated with the given extension.
     *
     * @param extension The file extension
     * @return The associated format or null if not found
     */
    public String getFormatForExtension(String extension) {
        return extensionFormatMap.get(extension.toLowerCase());
    }

    /**
     * Gets a property from the configuration.
     *
     * @param key The property key
     * @param defaultValue The default value if the property is not found
     * @return The property value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Loads extension mappings from properties.
     */
    private void loadExtensionMappings() {
        String extensionsProperty = properties.getProperty("format.extensions");
        if (extensionsProperty != null) {
            String[] formats = extensionsProperty.split(",");
            for (String format : formats) {
                String extensionsForFormat = properties.getProperty("format." + format.trim() + ".extensions");
                if (extensionsForFormat != null) {
                    String[] extensions = extensionsForFormat.split(",");
                    for (String extension : extensions) {
                        extensionFormatMap.put(extension.trim().toLowerCase(), format.trim().toUpperCase());
                    }
                }
            }
        } else {
            loadDefaultExtensionMappings();
        }
    }

    /**
     * Loads default extension mappings.
     */
    private void loadDefaultExtensionMappings() {
        // FASTA format
        extensionFormatMap.put("fa", "FASTA");
        extensionFormatMap.put("fasta", "FASTA");
        extensionFormatMap.put("fna", "FASTA");
        extensionFormatMap.put("faa", "FASTA");
        extensionFormatMap.put("ffn", "FASTA");

        // GFF format
        extensionFormatMap.put("gff", "GFF");
        extensionFormatMap.put("gff3", "GFF");

        // GTF format
        extensionFormatMap.put("gtf", "GTF");

        // BED format
        extensionFormatMap.put("bed", "BED");

        // GENBANK format
        extensionFormatMap.put("gb", "GENBANK");
        extensionFormatMap.put("gbk", "GENBANK");
        extensionFormatMap.put("genbank", "GENBANK");

        // VCF format
        extensionFormatMap.put("vcf", "VCF");
    }
}