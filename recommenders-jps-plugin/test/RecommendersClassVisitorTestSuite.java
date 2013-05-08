import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author Osipov Stanislav
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ForClassTest.class,
        IfOnLocalVariableClassTest.class,
        MethodArgumentClassTest.class,
        OneLocalVariableClassTest.class,
        EngineTest.class
})
public class RecommendersClassVisitorTestSuite {


    public static void compile(File src) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.forName("UTF-8"));
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(src));
        javaCompiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
    }

}
