package org.jetbrains.jps.incremental.recommenders;

import com.intellij.compiler.instrumentation.InstrumentationClassFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.ClassReader;
import org.jetbrains.asm4.ClassWriter;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.incremental.BinaryContent;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.CompiledClass;
import org.jetbrains.jps.incremental.instrumentation.BaseInstrumentingBuilder;

import java.util.HashMap;
import java.util.List;


public class RecommendersBuilder extends BaseInstrumentingBuilder {
    public RecommendersBuilder() {
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Recommenders Analyzer";
    }

    @Override
    protected boolean isEnabled(CompileContext context, ModuleChunk chunk) {
        return true;
    }

    @Override
    protected boolean canInstrument(CompiledClass compiledClass, int classFileVersion) {
        return true;
    }

    @Nullable
    @Override
    protected BinaryContent instrument(CompileContext context, CompiledClass compiled, ClassReader reader, ClassWriter writer, InstrumentationClassFinder finder) {

        HashMap<String, List<List<String>>> sequences = new HashMap<String, List<List<String>>>();
        RecommendersClassVisitor instrumenter = new RecommendersClassVisitor(compiled.getClassName(), sequences);

        try {
            reader.accept(instrumenter, ClassReader.EXPAND_FRAMES);
            System.out.println(compiled.getClassName());
            System.out.println(sequences);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected String getProgressMessage() {
        //TODO
        return "Adding pattern assertions...";
    }
}
