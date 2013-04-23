package ru.spbau.recommenders.plugin.asm;

import org.jetbrains.asm4.Label;
import org.jetbrains.asm4.MethodVisitor;
import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.commons.AnalyzerAdapter;
import org.jetbrains.asm4.signature.SignatureReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Osipov Stanislav
 */
public class RecommendersMethodVisitor extends AnalyzerAdapter {
    private Map<String, List<List<String>>> sequences;
    private Map<Integer, Integer> localVariablesMap = new HashMap<Integer, Integer>();
    private Map<Integer, List<String>> methodCallSequence = new HashMap<Integer, List<String>>();

    public RecommendersMethodVisitor(String owner, int access, String name, String desc, Map<String, List<List<String>>> sequences) {
        super(Opcodes.ASM4, owner, access, name, desc, new MethodVisitor(Opcodes.ASM4) {
        });
        this.sequences = sequences;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        List<String> signature = parseSignature(desc);
        int varIndex = stack.size() - signature.size() + 1;
        String representation = name + toStringRepresentation(signature);
        saveResult(varIndex, representation);
    }

    private void saveResult(int varIndex, String representation) {
        if (localVariablesMap.containsKey(varIndex)) {
            System.out.println("On local Variable " + localVariablesMap.get(varIndex));
            methodCallSequence.get(localVariablesMap.get(varIndex)).add(representation);
            localVariablesMap.remove(varIndex);
        }
    }

    private List<String> parseSignature(String desc) {
        SignatureReader reader = new SignatureReader(desc);
        List<String> signature = new ArrayList<String>();
        reader.accept(new RecommendersSignatureVisitor(signature));
        return signature;
    }

    private String toStringRepresentation(List<String> signature) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append('(');
        for (String s : signature.subList(0, signature.size() - 1)) {
            signatureBuilder.append(s);
            signatureBuilder.append(',');
        }
        if (signatureBuilder.length() != 1) {
            signatureBuilder.deleteCharAt(signatureBuilder.length() - 1);
        }
        signatureBuilder.append(')');
        signatureBuilder.append(signature.get(signature.size() - 1));
        return signatureBuilder.toString();
    }

    @Override
    public void visitVarInsn(int i, int i2) {
        if (!methodCallSequence.containsKey(i2)) {
            methodCallSequence.put(i2, new ArrayList<String>());
        }
        int prevSize = stack.size();
        super.visitVarInsn(i, i2);
        if (stack.size() > prevSize) {
            localVariablesMap.put(stack.size(), i2);
        }
    }

    @Override
    public void visitLocalVariable(String s, String type, String s3, Label label, Label label2, int i) {
        super.visitLocalVariable(s, type, s3, label, label2, i);
        String javaType = type.substring(1);
        if (!sequences.containsKey(javaType)) {
            sequences.put(javaType, new ArrayList<List<String>>());
        }
        sequences.get(javaType).add(methodCallSequence.get(i));
    }


}
