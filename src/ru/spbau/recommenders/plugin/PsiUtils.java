package ru.spbau.recommenders.plugin;

import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Pavel Talanov
 */
public final class PsiUtils {
    private PsiUtils() {
    }


    @Nullable
    public static String getMethodName(@NotNull PsiMethodCallExpression expression) {
        PsiReferenceExpression methodExpression = expression.getMethodExpression();
        return methodExpression.getReferenceName();
    }

    @Nullable
    public static String getReferencedName(@NotNull PsiMethodCallExpression expression) {
        PsiReferenceExpression methodExpression = expression.getMethodExpression();
        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        if (qualifierExpression instanceof PsiReferenceExpression) {
            return ((PsiReferenceExpression) qualifierExpression).getReferenceName();
        } else {
            return null;
        }
    }
}
