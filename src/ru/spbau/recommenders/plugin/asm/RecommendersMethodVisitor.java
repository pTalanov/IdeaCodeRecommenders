package ru.spbau.recommenders.plugin.asm;

import org.jetbrains.asm4.Label;
import org.jetbrains.asm4.MethodVisitor;
import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.commons.AnalyzerAdapter;
import org.jetbrains.asm4.signature.SignatureReader;

import java.util.*;

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
        saveResult(varIndex, representation, owner);
    }

    private void saveResult(int varIndex, String representation, String type) {
        if (localVariablesMap.containsKey(varIndex)) {
            Integer localVarNumber = localVariablesMap.get(varIndex);
            if (!methodCallSequence.containsKey(localVarNumber)) {
                methodCallSequence.put(localVarNumber, new ArrayList<String>(Arrays.asList(type)));
            }
            System.out.println("On local Variable " + localVarNumber);
            methodCallSequence.get(localVarNumber).add(representation);
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
        int prevSize = stack.size();
        super.visitVarInsn(i, i2);
        if (stack.size() > prevSize) {
            localVariablesMap.put(stack.size(), i2);
        }
    }


    @Override
    public void visitEnd() {
        super.visitEnd();
        for (List<String> methodSequence : methodCallSequence.values()) {
            String varType = methodSequence.get(0);
            if(!sequences.containsKey(varType)) {
                sequences.put(varType, new ArrayList<List<String>>());
            }
            sequences.get(varType).add(methodSequence.subList(1, methodSequence.size()));
        }
    }

}
