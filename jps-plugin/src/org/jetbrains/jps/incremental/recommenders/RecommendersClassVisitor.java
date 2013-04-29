package org.jetbrains.jps.incremental.recommenders;

import org.jetbrains.asm4.ClassVisitor;
import org.jetbrains.asm4.MethodVisitor;
import org.jetbrains.asm4.Opcodes;

import java.util.List;
import java.util.Map;

/**
 * @author Osipov Stanislav
 */
public class RecommendersClassVisitor extends ClassVisitor {

    private final String className;
    private Map<String, List<List<String>>> sequences;

    public RecommendersClassVisitor(String className, Map<String, List<List<String>>> sequences) {
        super(Opcodes.ASM4);
        this.className = className;
        this.sequences = sequences;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String n, final String desc, final String signature, final String[] exceptions) {
        return new RecommendersMethodVisitor(className, Opcodes.ACC_PUBLIC, n, desc, sequences);
    }

}