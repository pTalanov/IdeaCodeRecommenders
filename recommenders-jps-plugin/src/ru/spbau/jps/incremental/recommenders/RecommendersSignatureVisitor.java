package ru.spbau.jps.incremental.recommenders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.signature.SignatureVisitor;

import java.util.List;

/**
 * @author Osipov Stanislav
 */
public final class RecommendersSignatureVisitor extends SignatureVisitor {

    public RecommendersSignatureVisitor(@NotNull List<String> signature) {
        super(Opcodes.ASM4);
        this.signature = signature;
    }

    @NotNull
    private List<String> signature;

    @Override
    public void visitBaseType(char c) {
        switch (c) {
            case 'Z':
                signature.add("boolean");
                break;
            case 'B':
                signature.add("byte");
                break;
            case 'C':
                signature.add("char");
                break;
            case 'S':
                signature.add("short");
                break;
            case 'I':
                signature.add("int");
                break;
            case 'J':
                signature.add("long");
                break;
            case 'F':
                signature.add("float");
                break;
            case 'D':
                signature.add("double");
                break;
            case 'V':
                signature.add("void");
                break;
        }
        super.visitBaseType(c);
    }

    @Override
    public void visitClassType(@NotNull String s) {
        signature.add(s);
        super.visitClassType(s);
    }
}
