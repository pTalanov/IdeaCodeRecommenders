package ru.spbau.recommenders.plugin;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.psi.*;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ru.spbau.recommenders.plugin.PsiUtils.collectMethodCallsBeforeElementForQualifier;

/**
 * @author Pavel Talanov
 * @author Osipov Stanislav
 */
public final class MostPopularMethodContributor extends CompletionContributor {

    public MostPopularMethodContributor() {
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final CompletionResultSet result) {
        final String mostUsedMethodName = getMostUsedMethodName(parameters);
        if(mostUsedMethodName == null) {
            return;
        }
        result.runRemainingContributors(parameters, new Consumer<CompletionResult>() {
            @Override
            public void consume(CompletionResult completionResult) {
                final LookupElement lookupElement = completionResult.getLookupElement();
                if(!lookupElement.getLookupString().equals(mostUsedMethodName)) {
                    result.addElement(lookupElement);
                    return;
                }
                addRecommendation(lookupElement);
            }

            private void addRecommendation(final LookupElement lookupElement) {
                LookupElementDecorator<LookupElement> recommendation = LookupElementDecorator.withRenderer(lookupElement, new LookupElementRenderer<LookupElementDecorator<LookupElement>>() {
                    @Override
                    public void renderElement(LookupElementDecorator<LookupElement> element, LookupElementPresentation presentation) {
                        presentation.setItemTextForeground(Color.BLUE);
                        presentation.setItemTextBold(true);
                        lookupElement.renderElement(presentation);
                    }
                });
                result.addElement(PrioritizedLookupElement.withPriority(recommendation, 1.0));
            }
        });


    }

    @Nullable
    private String getMostUsedMethodName(CompletionParameters parameters) {
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
        return calculateMostUsedMethodName(position, (PsiReferenceExpression) qualifierExpression, type);
    }

    @Nullable
    private String calculateMostUsedMethodName(PsiElement position, PsiReferenceExpression qualifierExpression, PsiType type) {
        String typeName = type.getCanonicalText();
        List<String> methodsCalledBeforePosition
                = collectMethodCallsBeforeElementForQualifier(position, qualifierExpression);
        return MethodStatisticsProjectComponent.getInstance(position.getProject()).getMostUsedMethodName(typeName, methodsCalledBeforePosition);

    }

}
