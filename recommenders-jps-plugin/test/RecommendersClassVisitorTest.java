import org.jetbrains.asm4.ClassReader;
import org.jetbrains.asm4.Opcodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.jps.incremental.recommenders.RecommendersClassVisitor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

/**
 * @author Osipov Stanislav
 */
public class RecommendersClassVisitorTest {

    private static ClassReader reader;
    private static File out;
    private static InputStream inputStream;

    @BeforeClass
    public static void setUp() throws Exception {
        File src = new File("recommenders-jps-plugin/testData/TestClass.java");
        out = new File("recommenders-jps-plugin/testData/TestClass.class");
        String absolutePath = src.getAbsolutePath();
        Runtime.getRuntime().exec("javac " + absolutePath);
        while (!out.exists()) {
        }
        inputStream = new BufferedInputStream(new FileInputStream(out));
        reader = new ClassReader(inputStream);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        inputStream.close();
        while (!out.delete()) {
        }
    }

    @Test
    public void testVisitMethod() throws Exception {
        Map<String, Map<List<String>, Integer>> sequences = new HashMap<String, Map<List<String>, Integer>>();
        reader.accept(new RecommendersClassVisitor("TestClass", sequences), Opcodes.ASM4);
        assertTrue(true);
    }
}
