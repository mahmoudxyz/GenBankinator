package xyz.mahmoudahmed.translator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GeneticCodeTableTest {

    @Test
    public void testEnumValues() {
        // Test all enum values have unique table numbers
        assertNotNull(GeneticCodeTable.STANDARD);
        assertEquals(1, GeneticCodeTable.STANDARD.getTableNumber());
        assertEquals("Standard Code", GeneticCodeTable.STANDARD.getDescription());

        assertNotNull(GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL);
        assertEquals(2, GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL.getTableNumber());

        assertNotNull(GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL);
        assertEquals(5, GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL.getTableNumber());
    }

    @ParameterizedTest
    @MethodSource("tableNumberProvider")
    public void testGetByTableNumber(int tableNumber, GeneticCodeTable expected) {
        assertEquals(expected, GeneticCodeTable.getByTableNumber(tableNumber));
    }

    private static Stream<Arguments> tableNumberProvider() {
        return Stream.of(
                Arguments.of(1, GeneticCodeTable.STANDARD),
                Arguments.of(2, GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL),
                Arguments.of(5, GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL),
                Arguments.of(9, GeneticCodeTable.ECHINODERM_MITOCHONDRIAL),
                Arguments.of(11, GeneticCodeTable.BACTERIAL_PLASTID),
                Arguments.of(999, GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL) // Invalid number should default to table 5
        );
    }

    @ParameterizedTest
    @MethodSource("nameOrNumberProvider")
    public void testGetByNameOrNumber(String nameOrNumber, GeneticCodeTable expected) {
        assertEquals(expected, GeneticCodeTable.getByNameOrNumber(nameOrNumber));
    }

    private static Stream<Arguments> nameOrNumberProvider() {
        return Stream.of(
                Arguments.of("1", GeneticCodeTable.STANDARD),
                Arguments.of("STANDARD", GeneticCodeTable.STANDARD),
                Arguments.of("Standard Code", GeneticCodeTable.STANDARD),
                Arguments.of("5", GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL),
                Arguments.of("INVERTEBRATE_MITOCHONDRIAL", GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL),
                Arguments.of("Invertebrate Mitochondrial Code", GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL),
                Arguments.of("", GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL), // Empty string should default to table 5
                Arguments.of(null, GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL), // Null should default to table 5
                Arguments.of("Unknown", GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL) // Unknown should default to table 5
        );
    }
}