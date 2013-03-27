package ru.spbau.recommenders.plugin;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ru.spbau.recommenders.plugin.PsiUtils.collectMethodCallsBeforeElementForQualifier;

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
        if (!psiElement(PsiIdentifier.class).withText("IntellijIdeaRulezzz")
                .withParent(PsiReferenceExpression.class).accepts(position)) {
            return;
        }
        PsiReferenceExpression parent = (PsiReferenceExpression) position.getParent();
        PsiExpression qualifierExpression = parent.getQualifierExpression();
        if (!(qualifierExpression instanceof PsiReferenceExpression)) {
            return;
        }
        PsiType type = qualifierExpression.getType();
        if (type == null) {
            return;
        }
        String typeName = type.getCanonicalText();
        List<String> methodsCalledBeforePosition
                = collectMethodCallsBeforeElementForQualifier(position, (PsiReferenceExpression) qualifierExpression);
        String mostUsedMethodName =
                MethodStatisticsProjectComponent.getInstance(position.getProject()).getMostUsedMethodName(typeName, methodsCalledBeforePosition);
        if (mostUsedMethodName != null) {
            result.addElement(LookupElementBuilder.create(mostUsedMethodName + "()").withTypeText("most used", true));
        }
    }

}
