package ru.spbau.recommenders.plugin;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ru.spbau.recommenders.plugin.PsiUtils.collectMethodCallsBeforeElementForQualifier;

/**
 * @author Osipov Stanislav
 */
public class RecommendationProvider {

    @Nullable
    public static Recommendation getRecommendation(CompletionParameters parameters) {
        PsiElement position = parameters.getPosition();
        if (!psiElement(PsiIdentifier.class).withText(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED)
                .withParent(PsiReferenceExpression.class).accepts(position)) {
            return null;
        }
        PsiReferenceExpression parent = (PsiReferenceExpression) position.getParent();
        PsiExpression qualifierExpression = parent.getQualifierExpression();
        if (!(qualifierExpression instanceof PsiReferenceExpression)) {
            return null;
        }
        PsiType type = qualifierExpression.getType();
        if (type == null) {
            return null;
        }
        return calculateRecommendation(position, (PsiReferenceExpression) qualifierExpression, type);
    }

    @Nullable
    private static Recommendation calculateRecommendation(PsiElement position, PsiReferenceExpression qualifierExpression, PsiType type) {
        String typeName = type.getCanonicalText();
        List<String> methodsCalledBeforePosition
                = collectMethodCallsBeforeElementForQualifier(position, qualifierExpression);
        final String mostUsedMethodName = MethodStatisticsProjectComponent.getInstance(position.getProject()).getMostUsedMethodName(typeName, methodsCalledBeforePosition);
        return new Recommendation() {
            @Override
            public double getPriority(@NotNull LookupElement lookupElement) {
                if (lookupElement.getLookupString().equals(mostUsedMethodName)) {
                    return 1.0;
                }
                return 0.0;
            }
        };

    }
}
