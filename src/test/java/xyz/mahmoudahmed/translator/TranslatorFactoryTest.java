package xyz.mahmoudahmed.translator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TranslatorFactoryTest {

    @Test
    public void testCreateInvertebrateMitochondrialTranslator() {
        Translator translator = TranslatorFactory.createInvertebrateMitochondrialTranslator();

        assertNotNull(translator);
        assertInstanceOf(StandardTranslator.class, translator);

        StandardTranslator stdTranslator = (StandardTranslator) translator;
        GeneticCode geneticCode = stdTranslator.getGeneticCode();

        assertInstanceOf(InvertebrateMitochondrialCode.class, geneticCode);
        assertEquals("Invertebrate Mitochondrial Code", geneticCode.getName());
    }

    @ParameterizedTest
    @EnumSource(GeneticCodeTable.class)
    public void testCreateTranslatorByEnum(GeneticCodeTable table) {
        Translator translator = TranslatorFactory.createTranslator(table);

        assertNotNull(translator);
        assertInstanceOf(StandardTranslator.class, translator);

        StandardTranslator stdTranslator = (StandardTranslator) translator;
        GeneticCode geneticCode = stdTranslator.getGeneticCode();

        // For tables we've implemented specific classes for
        if (table == GeneticCodeTable.STANDARD) {
            assertInstanceOf(StandardGeneticCode.class, geneticCode);
        } else if (table == GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL) {
            assertInstanceOf(InvertebrateMitochondrialCode.class, geneticCode);
        } else if (table == GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL) {
            assertInstanceOf(VertebrateMitochondrialCode.class, geneticCode);
        }

        // The table reference in the genetic code should match the input table
        if (geneticCode instanceof AbstractGeneticCode) {
            GeneticCodeTable codeTable = ((AbstractGeneticCode) geneticCode).getTable();
            // For unimplemented tables, we fall back to table 5, so we can't do a direct comparison
            if (table == GeneticCodeTable.STANDARD ||
                    table == GeneticCodeTable.INVERTEBRATE_MITOCHONDRIAL ||
                    table == GeneticCodeTable.VERTEBRATE_MITOCHONDRIAL) {
                assertEquals(table, codeTable);
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 9, 11, 999})
    public void testCreateTranslatorByTableNumber(int tableNumber) {
        Translator translator = TranslatorFactory.createTranslator(tableNumber);

        assertNotNull(translator);
        assertInstanceOf(StandardTranslator.class, translator);

        StandardTranslator stdTranslator = (StandardTranslator) translator;
        GeneticCode geneticCode = stdTranslator.getGeneticCode();

        // For tables we've implemented specific classes for
        if (tableNumber == 1) {
            assertInstanceOf(StandardGeneticCode.class, geneticCode);
        } else if (tableNumber == 5) {
            assertInstanceOf(InvertebrateMitochondrialCode.class, geneticCode);
        } else if (tableNumber == 2) {
            assertInstanceOf(VertebrateMitochondrialCode.class, geneticCode);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"STANDARD", "1", "Standard Code", "INVERTEBRATE_MITOCHONDRIAL", "5",
            "Invertebrate Mitochondrial Code", "VERTEBRATE_MITOCHONDRIAL", "2",
            "Vertebrate Mitochondrial Code", "INVALID", ""})
    public void testCreateTranslatorByString(String nameOrNumber) {
        Translator translator = TranslatorFactory.createTranslator(nameOrNumber);

        assertNotNull(translator);
        assertInstanceOf(StandardTranslator.class, translator);

        StandardTranslator stdTranslator = (StandardTranslator) translator;
        GeneticCode geneticCode = stdTranslator.getGeneticCode();

        // For specific inputs, check the genetic code type
        if ("STANDARD".equals(nameOrNumber) || "1".equals(nameOrNumber) || "Standard Code".equals(nameOrNumber)) {
            assertInstanceOf(StandardGeneticCode.class, geneticCode);
        } else if ("INVERTEBRATE_MITOCHONDRIAL".equals(nameOrNumber) || "5".equals(nameOrNumber) ||
                "Invertebrate Mitochondrial Code".equals(nameOrNumber) || "INVALID".equals(nameOrNumber) ||
                "".equals(nameOrNumber)) {
            assertInstanceOf(InvertebrateMitochondrialCode.class, geneticCode);
        } else if ("VERTEBRATE_MITOCHONDRIAL".equals(nameOrNumber) || "2".equals(nameOrNumber) ||
                "Vertebrate Mitochondrial Code".equals(nameOrNumber)) {
            assertInstanceOf(VertebrateMitochondrialCode.class, geneticCode);
        }
    }

    @Test
    public void testCreateTranslatorWithNull() {
        Translator translator = TranslatorFactory.createTranslator((GeneticCodeTable)null);

        assertNotNull(translator);
        assertInstanceOf(StandardTranslator.class, translator);

        StandardTranslator stdTranslator = (StandardTranslator) translator;
        GeneticCode geneticCode = stdTranslator.getGeneticCode();

        // Should default to Invertebrate Mitochondrial
        assertInstanceOf(InvertebrateMitochondrialCode.class, geneticCode);
    }
}