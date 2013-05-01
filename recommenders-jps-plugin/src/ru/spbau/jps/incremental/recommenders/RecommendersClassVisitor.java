package ru.spbau.jps.incremental.recommenders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.ClassVisitor;
import org.jetbrains.asm4.MethodVisitor;
import org.jetbrains.asm4.Opcodes;

import java.util.List;
import java.util.Map;

/**
 * @author Osipov Stanislav
 */
public final class RecommendersClassVisitor extends ClassVisitor {


    @Nullable
    private final String className;
    @NotNull
    private Map<String, Map<List<String>, Integer>> sequences;


    public RecommendersClassVisitor(@Nullable String className, @NotNull Map<String, Map<List<String>, Integer>> sequences) {
        super(Opcodes.ASM4);
        this.className = className;
        this.sequences = sequences;
    }

    @NotNull
    @Override
    public MethodVisitor visitMethod(final int access, final String n, final String desc, final String signature, final String[] exceptions) {
        return new RecommendersMethodVisitor(className, Opcodes.ACC_PUBLIC, n, desc, sequences);
    }

}