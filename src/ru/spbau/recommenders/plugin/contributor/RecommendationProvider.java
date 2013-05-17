package ru.spbau.recommenders.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.recommenders.plugin.MethodStatisticsProjectComponent;
import ru.spbau.recommenders.plugin.data.Suggestions;
import ru.spbau.recommenders.plugin.utils.PsiUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ru.spbau.recommenders.plugin.utils.PsiUtils.collectMethodCallsBeforeElementForQualifier;
import static ru.spbau.recommenders.plugin.utils.PsiUtils.getTypeNameInByteCodeFormat;

/**
 * @author Osipov Stanislav
 */
public final class RecommendationProvider {

    @Nullable
    public static Recommendation getRecommendation(@NotNull CompletionParameters parameters) {
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
    private static Recommendation calculateRecommendation(@NotNull PsiElement position,
                                                          @NotNull PsiReferenceExpression qualifierExpression,
                                                          @NotNull PsiType type
    ) {
        String typeName = getTypeNameInByteCodeFormat(type);
        List<String> methodsCalledBeforePosition
                = collectMethodCallsBeforeElementForQualifier(position, qualifierExpression);
        Suggestions suggestions = MethodStatisticsProjectComponent.getInstance(position.getProject())
                .getRecommendation(typeName, methodsCalledBeforePosition);
        if (suggestions == null) {
            return null;
        }
        return new SignatureRecommendation(suggestions.getMostUsedSuggestions());
    }

    private final static class SignatureRecommendation implements Recommendation {

        @NotNull
        private Map<String, Integer> suggestions;
        private Map.Entry<String, Integer> maxPriorityElement;

        private SignatureRecommendation(@NotNull Map<String, Integer> suggestions) {
            this.suggestions = suggestions;
            this.maxPriorityElement = Collections.max(suggestions.entrySet(), new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o1.getValue() - o2.getValue();
                }
            });
        }

        @Override
        public double getPriority(@NotNull LookupElement lookupElement) {
            if (!(lookupElement.getPsiElement() instanceof PsiMethod)) {
                return 0.0;
            }
            PsiMethod method = (PsiMethod) lookupElement.getPsiElement();
            if (method == null || !method.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)) {
                return 0.0;
            }
            String signatureString = PsiUtils.getSignatureString(method);
            if (signatureString == null) {
                return 0.0;
            }
            Integer counter = suggestions.get(signatureString);
            if (counter != null && maxPriorityElement.getValue() != 0) {
                return (double) counter / (double) maxPriorityElement.getValue();
            }
            return 0.0;
        }
    }
}
