package ru.spbau.recommenders.plugin;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ru.spbau.recommenders.plugin.DeclaredVariables.collect;

/**
 * @author Pavel Talanov
 */
public final class CallStatisticsCollector {

    @NotNull
    private final MethodCallData methodCallData;

    public CallStatisticsCollector(@NotNull MethodCallData methodCallData) {
        this.methodCallData = methodCallData;
    }

    public void collectStatistics(@NotNull PsiFile psiFile) {
        psiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                //TODO: need super call?
                super.visitMethod(method);
                if (method.getBody() != null) {
                    processMethod(method);
                }
            }
        });
    }

    private void processMethod(@NotNull PsiMethod method) {
        DeclaredVariables declaredVariables = collect(method);
        MethodSequenceData methodSequenceData = MethodSequenceData.collectMethodSequenceData(method, declaredVariables);
        recordMethodCallData(declaredVariables, methodSequenceData);
    }

    private void recordMethodCallData(@NotNull DeclaredVariables declaredVariables,
                                      @NotNull MethodSequenceData methodSequenceData) {
        for (String variableName : declaredVariables.getDeclaredVariables()) {
            String typeName = declaredVariables.getType(variableName);
            assert typeName != null;
            List<String> sequence = methodSequenceData.getSequence(variableName);
            for (int i = 1; i <= sequence.size(); ++i) {
                methodCallData.registerCallSequence(typeName, sequence.subList(0, i));
            }
        }
    }

}
