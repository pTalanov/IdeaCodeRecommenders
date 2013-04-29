import org.jetbrains.asm4.ClassReader;
import org.jetbrains.asm4.Opcodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.spbau.recommenders.plugin.asm.RecommendersClassVisitor;

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
        File src = new File("testData/TestClass.java");
        out = new File("testData/TestClass.class");
        Runtime.getRuntime().exec("javac " + src.getAbsolutePath());
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
        Map<String, List<List<String>>> sequences = new HashMap<String, List<List<String>>>();
        reader.accept(new RecommendersClassVisitor("TestClass", sequences), Opcodes.ASM4);
        assertTrue(true);
    }
}
