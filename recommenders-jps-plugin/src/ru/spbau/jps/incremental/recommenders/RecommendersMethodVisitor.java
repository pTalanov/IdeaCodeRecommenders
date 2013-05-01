package ru.spbau.jps.incremental.recommenders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.asm4.MethodVisitor;
import org.jetbrains.asm4.Opcodes;
import org.jetbrains.asm4.commons.AnalyzerAdapter;
import org.jetbrains.asm4.signature.SignatureReader;

import java.util.*;

/**
 * @author Osipov Stanislav
 */
public final class RecommendersMethodVisitor extends AnalyzerAdapter {

    @NotNull
    private Map<String, Map<List<String>, Integer>> sequences;
    @NotNull
    private Map<Integer, Integer> localVariablesMap = new HashMap<Integer, Integer>();
    @NotNull
    private Map<Integer, List<String>> methodCallSequence = new HashMap<Integer, List<String>>();

    public RecommendersMethodVisitor(String owner, int access, String name, String desc, @NotNull Map<String, Map<List<String>, Integer>> sequences) {
        super(Opcodes.ASM4, owner, access, name, desc, new MethodVisitor(Opcodes.ASM4) {
        });
        this.sequences = sequences;
    }

    @Override
    public void visitMethodInsn(int opcode, @NotNull String owner, @NotNull String name, @NotNull String sign) {
        List<String> signature = parseSignature(sign);
        int varIndex = stack.size() - signature.size() + 1;
        String representation = name + toStringRepresentation(signature);
        saveResult(varIndex, representation, owner);
        super.visitMethodInsn(opcode, owner, name, sign);
    }

    private void saveResult(int varIndex, @NotNull String representation, @NotNull String type) {
        if (localVariablesMap.containsKey(varIndex)) {
            Integer localVarNumber = localVariablesMap.get(varIndex);
            if (!methodCallSequence.containsKey(localVarNumber)) {
                methodCallSequence.put(localVarNumber, new ArrayList<String>(Arrays.asList(type)));
            }
            methodCallSequence.get(localVarNumber).add(representation);
            localVariablesMap.remove(varIndex);
        }
    }


    @NotNull
    private List<String> parseSignature(@NotNull String desc) {
        SignatureReader reader = new SignatureReader(desc);
        List<String> signature = new ArrayList<String>();
        reader.accept(new RecommendersSignatureVisitor(signature));
        return signature;
    }

    @NotNull
    private String toStringRepresentation(@NotNull List<String> signature) {
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
            if (methodSequence.size() < 2) {
                continue;
            }
            String varType = methodSequence.get(0);
            List<String> sequence = methodSequence.subList(1, methodSequence.size());
            Map<List<String>, Integer> currentSequences = sequences.get(varType);
            if (currentSequences == null) {
                currentSequences = new HashMap<List<String>, Integer>();
            }
            Integer sequenceCounter = currentSequences.get(sequence);
            if (sequenceCounter == null) {
                sequenceCounter = 0;
            }
            currentSequences.put(sequence, ++sequenceCounter);
            sequences.put(varType, currentSequences);
        }
    }

}
