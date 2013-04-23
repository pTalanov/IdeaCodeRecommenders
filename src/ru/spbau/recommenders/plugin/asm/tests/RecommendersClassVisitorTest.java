package ru.spbau.recommenders.plugin.asm.tests;

import org.jetbrains.asm4.ClassReader;
import org.jetbrains.asm4.Opcodes;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.recommenders.plugin.asm.RecommendersClassVisitor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * @author Osipov Stanislav
 */
public class RecommendersClassVisitorTest {

    private static ClassReader reader;

    @BeforeClass
    public static void setUp() throws Exception {
        Runtime.getRuntime().exec("javac src/ru/spbau/recommenders/plugin/asm/tests/TestClass.java");
        reader = new ClassReader(new BufferedInputStream(new FileInputStream("src/ru/spbau/recommenders/plugin/asm/tests/TestClass.class")));
    }


    @Test
    public void testVisitMethod() throws Exception {
        Map<String, List<List<String>>> sequences = new HashMap<String, List<List<String>>>();
        reader.accept(new RecommendersClassVisitor("TestClass", sequences), Opcodes.ASM4);
        assertTrue(true);
    }
}
