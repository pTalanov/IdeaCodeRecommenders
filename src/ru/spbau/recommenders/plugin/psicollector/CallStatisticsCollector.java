package ru.spbau.recommenders.plugin.psicollector;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import ru.spbau.recommenders.plugin.storage.MethodStatisticsStorage;

import java.util.List;

import static ru.spbau.recommenders.plugin.psicollector.DeclaredVariables.collect;
import static ru.spbau.recommenders.plugin.storage.StorageUtils.saveCallSequenceToStorage;

/**
 * @author Pavel Talanov
 */
public final class CallStatisticsCollector {

    @NotNull
    private final MethodStatisticsStorage methodStatisticsStorage;

    public CallStatisticsCollector(@NotNull MethodStatisticsStorage methodStatisticsStorage) {
        this.methodStatisticsStorage = methodStatisticsStorage;
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
            saveCallSequenceToStorage(typeName, sequence, methodStatisticsStorage);
        }
    }
}
