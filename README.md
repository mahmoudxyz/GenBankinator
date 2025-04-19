# GenBank Converter Library

A high-performance, extensible Java library for converting between FASTA sequence data, various annotation formats (GFF, GTF, BED), and GenBank format files. Designed for researchers and developers working with genomic data.

## Overview

GenBank Converter is a comprehensive Java library that simplifies the process of working with biological sequence data. With a focus on memory efficiency and extensibility, it's particularly well-suited for large genomic datasets common in research environments.

Key features:
- Convert FASTA sequences and annotations to GenBank format
- Memory-efficient streaming for large files (>1GB)
- Comprehensive validation capabilities
- Rich customization options for the conversion process
- Clean, well-documented API following SOLID principles

## Installation

### Maven

```xml
<dependency>
    <groupId>xyz.mahmoudahmed</groupId>
    <artifactId>genbank-converter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'xyz.mahmoudahmed:genbank-converter:1.0.0'
```

## Basic Usage

Converting files to GenBank format:

```java
// Create converter with default settings
GenbankConverter converter = GenbankConverter.standard();

// Define basic conversion options
ConversionOptions options = ConversionOptions.builder()
        .organism("Homo sapiens")
        .moleculeType("DNA")
        .topology("linear")
        .annotationFormat("GFF")
        .build();

// Convert files
GenbankResult result = converter.convert(
        new File("sequences.fasta"), 
        new File("annotations.gff"), 
        options);

// Save the result
result.writeToFile(new File("output.gb"));
```

## Advanced Usage

### Memory-Efficient Processing for Large Files

```java
// Create memory-efficient converter
GenbankConverter converter = GenbankConverter.memoryEfficient();

// Define conversion options
ConversionOptions options = ConversionOptions.builder()
        .organism("Homo sapiens mitochondrion")
        .moleculeType("DNA")
        .topology("circular")
        .build();

// Convert with streaming to avoid memory issues
try (FileOutputStream outputStream = new FileOutputStream("output.gb")) {
    // Parse metadata only
    SequenceParser parser = new DefaultSequenceParser();
    SequenceData sequenceData = parser.parseMetadataOnly(new File("large_genome.fasta"));
    
    // Parse annotations
    AnnotationParser annParser = new DefaultAnnotationParser();
    AnnotationData annotationData = annParser.parse(new File("annotations.gff"));
    
    // Stream directly to output
    converter.convertToStream(sequenceData, annotationData, outputStream, options);
}
```

### Comprehensive Validation

```java
// Create converter
GenbankConverter converter = GenbankConverter.standard();

// Files to validate
File sequenceFile = new File("sequences.fasta");
File annotationFile = new File("annotations.gff");

// Validate sequence file
ValidationResult seqValidation = converter.validateSequence(sequenceFile);
if (!seqValidation.isValid()) {
    System.out.println("Sequence validation failed: " + seqValidation.getSummary());
    return;
}

// Validate annotation file
ValidationResult annValidation = converter.validateAnnotation(annotationFile, "GFF");
if (!annValidation.isValid()) {
    System.out.println("Annotation validation failed: " + annValidation.getSummary());
    return;
}

// Validate compatibility
ValidationResult compatValidation = converter.validate(sequenceFile, annotationFile);
if (!compatValidation.isValid()) {
    System.out.println("Compatibility validation failed: " + compatValidation.getSummary());
    return;
}

// Proceed with conversion if all validations pass
```

### Customizing the Conversion Process

```java
// Create detailed conversion options
ConversionOptions options = ConversionOptions.builder()
        .organism("Homo sapiens mitochondrion")
        .moleculeType("DNA")
        .topology("circular")
        .division("VRT")
        // Configure translation options
        .translationOptions(TranslationOptions.builder()
                .autoTranslate(true)
                .geneticCode("vertebrate_mitochondrial")
                .forceTranslateFeatureTypes(List.of("exon"))
                .skipTranslateFeatureTypes(List.of("pseudogene"))
                .build())
        // Configure output formatting
        .outputFormattingOptions(OutputFormattingOptions.builder()
                .sequenceLineWidth(60)
                .lowercaseSequence(false)
                .includeEmptyLinesBetweenFeatures(true)
                .build())
        // Configure feature filtering
        .filterOptions(FeatureFilterOptions.builder()
                .includeFeatureTypes(List.of("gene", "CDS", "tRNA", "rRNA"))
                .excludeFeatureTypes(List.of("misc_feature"))
                .build())
        .build();
```

## Extending the Library

### Implementing a Custom Sequence Parser

```java
public class CustomSequenceParser implements SequenceParser {
    @Override
    public boolean supportsFormat(String format) {
        return "CUSTOM_FORMAT".equalsIgnoreCase(format);
    }
    
    @Override
    public SequenceData parse(File file) throws IOException {
        // Custom parsing logic
        List<Sequence> sequences = new ArrayList<>();
        // ...
        return SequenceData.builder()
                .addSequences(sequences)
                .build();
    }
    
    @Override
    public SequenceData parse(InputStream inputStream) throws IOException {
        // Custom parsing logic
        // ...
    }
    
    @Override
    public SequenceData parseMetadataOnly(File file) throws IOException {
        // Custom metadata parsing logic
        // ...
    }
}

// Using the custom parser
GenbankConverter converter = GenbankConverter.builder()
        .withSequenceParser(new CustomSequenceParser())
        .build();
```

### Creating a Custom Formatter

```java
public class CustomGenbankFormatter implements GenbankFormatter {
    @Override
    public byte[] format(SequenceData sequenceData, AnnotationData annotationData, 
                        ConversionOptions options) {
        // Custom formatting logic
        // ...
    }
    
    @Override
    public void formatToStream(SequenceData sequenceData, AnnotationData annotationData,
                              OutputStream outputStream, ConversionOptions options) 
                              throws IOException {
        // Custom streaming format logic
        // ...
    }
}

// Using the custom formatter
GenbankConverter converter = GenbankConverter.builder()
        .withFormatter(new CustomGenbankFormatter())
        .build();
```

### Adding a New Feature Type

To add support for a new gene/feature type, simply:

1. Create a new handler class that extends `AbstractFeatureHandler`
2. Implement the `canHandle()` method to recognize your feature type
3. Implement the `buildQualifiers()` method for your feature's qualifiers
4. Register the handler with `FeatureHandlerRegistry.registerHandler()`

For example, adding support for a new type of gene "xyzG":

```java
public class XyzGeneHandler extends AbstractFeatureHandler {
    @Override
    public String getFeatureType() {
        return "CDS";  // Will be a CDS feature
    }
    
    @Override
    public boolean canHandle(String featureName) {
        return featureName != null && featureName.toLowerCase().startsWith("xyzg");
    }
    
    @Override
    public Map<String, List<String>> buildQualifiers(HeaderInfo header, 
                                                    String sequence, 
                                                    Translator translator) {
        Map<String, List<String>> qualifiers = super.buildQualifiers(header, sequence, translator);
        qualifiers.put("product", Collections.singletonList("XYZ product"));
        // Add other qualifiers...
        return qualifiers;
    }
}

// Then register it
FeatureHandlerRegistry.registerHandler(new XyzGeneHandler());
```


## Architecture

The library follows a modular architecture with clear separation of concerns:

- **API Layer** - Public interfaces defining the contract (`xyz.mahmoudahmed.api`)
- **Model Layer** - Domain model classes representing biological data (`xyz.mahmoudahmed.model`)
- **Core Layer** - Implementations of the API interfaces (`xyz.mahmoudahmed.core`)
- **Util Layer** - Utility classes for various operations (`xyz.mahmoudahmed.util`)
- **Exception Layer** - Exception hierarchy for error handling (`xyz.mahmoudahmed.exception`)

Key components:

1. **GenbankConverter** - Main entry point for conversion operations
2. **SequenceParser** & **AnnotationParser** - Interfaces for parsing file formats
3. **GenbankValidator** - Validation mechanisms
4. **GenbankFormatter** - Output generation
5. **Model Classes** - Rich domain model for biological data

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Citation

If you use this library in your research, please cite:

```
Ahmed, M. (2025). GenBank Converter: A Java library for biological sequence format conversion. 
Journal of Bioinformatics Tools, 10(2), 45-52.
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request