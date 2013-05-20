package ru.spbau.recommenders.plugin.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementDecorator;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author Pavel Talanov
 * @author Osipov Stanislav
 */
public final class MostPopularMethodContributor extends CompletionContributor {

    private static final String TAB = "  ";
    private static final double MIN_PRIORITY_COLORED = 0.5;
    private static final double MIN_PRIORITY_DISPLAYED = 0.05;

    public MostPopularMethodContributor() {
    }


    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final CompletionResultSet result) {
        final Recommendation recommendation = RecommendationProvider.getRecommendation(parameters);
        if (recommendation == null) {
            return;
        }
        result.runRemainingContributors(parameters, new Consumer<CompletionResult>() {
            @Override
            public void consume(CompletionResult completionResult) {
                LookupElement lookupElement = completionResult.getLookupElement();
                double priority = recommendation.getPriority(lookupElement);
                LookupElement markedLookupElement = mark(lookupElement, priority);
                if (markedLookupElement != null) {
                    result.addElement(markedLookupElement);
                }
            }
        });


    }

    @Nullable
    private LookupElement mark(@NotNull final LookupElement lookupElement, final double priority) {

        LookupElementDecorator<LookupElement> markedLookupElement = LookupElementDecorator.withRenderer(lookupElement, new LookupElementRenderer<LookupElementDecorator<LookupElement>>() {
            @Override
            public void renderElement(LookupElementDecorator<LookupElement> element, LookupElementPresentation presentation) {
                lookupElement.renderElement(presentation);
                if (priority > MIN_PRIORITY_COLORED) {
                    presentation.setItemTextForeground(Color.BLUE);
                }

                if (priority > MIN_PRIORITY_DISPLAYED) {
                    presentation.setItemTextBold(true);
                    presentation.setTypeText(asString(priority) + TAB + presentation.getTypeText());
                }
            }

            private String asString(double priority) {
                return ((int) (priority * 100)) + "%";
            }
        });
        return PrioritizedLookupElement.withPriority(markedLookupElement, priority);
    }

}
