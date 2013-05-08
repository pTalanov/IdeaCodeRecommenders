import junit.framework.Assert;
import org.jetbrains.asm4.ClassReader;
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

/**
 * @author Osipov Stanislav
 */
public class EngineTest {

    private static File out;
    private static ClassReader reader;
    private static InputStream inputStream;


    @BeforeClass
    public static void setUp() throws Exception {
        out = new File("recommenders-jps-plugin/testData/Engine.class");
        inputStream = new BufferedInputStream(new FileInputStream(out));
        reader = new ClassReader(inputStream);
    }


    @AfterClass
    public static void tearDown() throws Exception {
        inputStream.close();
    }

    @Test
    public void testEngine() throws Exception {
        Map<String, Map<List<String>, Integer>> sequences = new HashMap<String, Map<List<String>, Integer>>();
        reader.accept(new RecommendersClassVisitor(out.getName(), sequences), ClassReader.EXPAND_FRAMES);
        String check = "{org/tmcdb/parser/instructions/CreateTableInstruction={[getTableName()java/lang/String, getColumns()java/util/List]=2}, org/tmcdb/parser/instructions/InsertInstruction$ColumnNameAndData={[getColumnName()java/lang/String, getData()java/lang/Object]=2}, org/tmcdb/engine/Engine={[getHeapFile(java/lang/String)org/tmcdb/heapfile/HeapFile]=2, [getSchema(java/lang/String)org/tmcdb/engine/schema/TableSchema]=6, [getHeapFile(java/lang/String)org/tmcdb/heapfile/HeapFile, constructRowObject(org/tmcdb/parser/instructions/InsertInstruction)org/tmcdb/engine/data/Row]=1}, java/util/Iterator={[hasNext()boolean]=2, [hasNext()boolean, next()java/lang/Object]=2}, org/tmcdb/engine/schema/TableSchema={[getColumn(java/lang/String)org/tmcdb/engine/schema/Column]=2}, org/tmcdb/parser/instructions/SelectInstruction={[getTableName()java/lang/String]=2}, org/tmcdb/heapfile/HeapFile={[insertRecord(org/tmcdb/engine/data/Row)void]=1}, org/tmcdb/parser/instructions/InsertInstruction={[getTableName()java/lang/String, getColumnNamesWithData()java/util/List]=4, [getTableName()java/lang/String]=1}}";
        Assert.assertTrue(check.equals(sequences.toString()));

    }
}
