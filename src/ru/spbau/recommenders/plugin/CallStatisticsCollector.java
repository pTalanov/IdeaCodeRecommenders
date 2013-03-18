package ru.spbau.recommenders.plugin;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final DeclaredVariables declaredVariables = collect(method);
        final MethodSequenceData methodSequenceData = new MethodSequenceData();
        PsiCodeBlock body = method.getBody();
        assert body != null;
        body.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                methodSequenceData.processMethodCall(expression, declaredVariables);
            }
        });

        for (String variableName : declaredVariables.getDeclaredVariables()) {
            String typeName = declaredVariables.getType(variableName);
            assert typeName != null;
            for (String methodName : methodSequenceData.getSequence(variableName)) {
                methodCallData.registerCall(typeName, methodName);
            }
        }
    }

    private class MethodSequenceData {
        @NotNull
        private final Map<String, List<String>> varNameToMethodSequence = new HashMap<String, List<String>>();


        private void registerCall(@NotNull String variableName, @NotNull String methodName) {
            List<String> methodSequence = getSequence(variableName);
            methodSequence.add(methodName);
        }

        @NotNull
        private List<String> getSequence(@NotNull String variableName) {
            List<String> methodSequence = varNameToMethodSequence.get(variableName);
            if (methodSequence == null) {
                methodSequence = new ArrayList<String>();
                varNameToMethodSequence.put(variableName, methodSequence);
            }
            return methodSequence;
        }

        private void processMethodCall(@NotNull PsiMethodCallExpression expression,
                                       @NotNull DeclaredVariables declaredVariables) {
            PsiReferenceExpression methodExpression = expression.getMethodExpression();
            PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
            if (qualifierExpression instanceof PsiReferenceExpression) {
                String referencedName = ((PsiReferenceExpression) qualifierExpression).getReferenceName();
                String methodName = methodExpression.getReferenceName();
                if (referencedName != null && methodName != null) {
                    if (declaredVariables.isDeclared(referencedName)) {
                        registerCall(referencedName, methodName);
                    }
                }
            }
        }

    }
}
