package ru.spbau.recommenders.plugin;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class MethodSequenceData {

    @NotNull
    public static MethodSequenceData collectMethodSequenceData(@NotNull PsiMethod method,
                                                               @NotNull final DeclaredVariables declaredVariables) {
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
        return methodSequenceData;
    }

    @NotNull
    private final Map<String, List<String>> varNameToMethodSequence = new HashMap<String, List<String>>();

    private MethodSequenceData() {
    }

    private void registerCall(@NotNull String variableName, @NotNull String methodName) {
        List<String> methodSequence = getSequence(variableName);
        methodSequence.add(methodName);
    }

    @NotNull
    public List<String> getSequence(@NotNull String variableName) {
        List<String> methodSequence = varNameToMethodSequence.get(variableName);
        if (methodSequence == null) {
            methodSequence = new ArrayList<String>();
            varNameToMethodSequence.put(variableName, methodSequence);
        }
        return methodSequence;
    }

    private void processMethodCall(@NotNull PsiMethodCallExpression expression,
                                   @NotNull DeclaredVariables declaredVariables) {
        String referencedName = PsiUtils.getReferencedName(expression);
        String methodName = PsiUtils.getMethodName(expression);
        if (referencedName == null || methodName == null) {
            return;
        }
        if (declaredVariables.isDeclared(referencedName)) {
            registerCall(referencedName, methodName);
        }
    }
}
