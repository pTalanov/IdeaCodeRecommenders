package ru.spbau.recommenders.plugin;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

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
        PsiCodeBlock body = method.getBody();
        assert body != null;
        body.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                //TODO: unneeded super call?
                super.visitMethodCallExpression(expression);
                PsiReferenceExpression methodExpression = expression.getMethodExpression();
                PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
                if (qualifierExpression instanceof PsiReferenceExpression) {
                    String referencedName = ((PsiReferenceExpression) qualifierExpression).getReferenceName();
                    String methodName = methodExpression.getReferenceName();
                    if (referencedName != null && methodName != null) {
                        String type = declaredVariables.getType(referencedName);
                        if (type != null) {
                            methodCallData.registerCall(type, methodName);
                        }
                    }
                }
            }
        });
    }
}
