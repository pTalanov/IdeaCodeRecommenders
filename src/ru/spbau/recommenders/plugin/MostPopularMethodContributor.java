package ru.spbau.recommenders.plugin;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author Pavel Talanov
 */
public final class MostPopularMethodContributor extends CompletionContributor {

    public MostPopularMethodContributor() {
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        //SWEET!
        if (psiElement(PsiIdentifier.class).withText("IntellijIdeaRulezzz")
                .withParent(PsiReferenceExpression.class).accepts(position)) {
            PsiReferenceExpression parent = (PsiReferenceExpression) position.getParent();
            PsiExpression qualifierExpression = parent.getQualifierExpression();
            if (qualifierExpression instanceof PsiReferenceExpression) {
                System.out.println(((PsiReferenceExpression) qualifierExpression).getReferenceName());
                PsiType type = qualifierExpression.getType();
                if (type != null) {
                    String typeName = type.getCanonicalText();
                    String mostUsedMethodName =
                            MethodStatisticsProjectComponent.getInstance(position.getProject()).getMostUsedMethodName(typeName);
                    if (mostUsedMethodName != null) {
                        result.addElement(LookupElementBuilder.create(mostUsedMethodName + "()").withTypeText("most used", true));
                    }
                }
            }
        }

    }
}
