package ru.spbau.recommenders.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class CallSequenceStatistics {

    @NotNull
    private final Map<CallSequence, Suggestions> sequenceToSuggestions = new HashMap<CallSequence, Suggestions>();

    @NotNull
    private Suggestions getOrCreateSuggestions(@NotNull CallSequence sequence) {
        Suggestions suggestions = sequenceToSuggestions.get(sequence);
        if (suggestions == null) {
            suggestions = new Suggestions();
            sequenceToSuggestions.put(sequence, suggestions);
        }
        return suggestions;
    }

    private void registerSequence(@NotNull CallSequence sequence, @NotNull String methodName) {
        getOrCreateSuggestions(sequence).registerUsage(methodName);
    }

    public void registerSequence(@NotNull List<String> callSequence) {
        assert !callSequence.isEmpty();
        String methodToSuggest = callSequence.get(callSequence.size() - 1);
        List<String> prefix = callSequence.subList(0, callSequence.size() - 1);
        registerSequence(new CallSequence(prefix), methodToSuggest);
    }

    @Nullable
    public String getSuggestion(@NotNull List<String> callSequence) {
        Suggestions suggestions = sequenceToSuggestions.get(new CallSequence(callSequence));
        if (suggestions == null) {
            return null;
        }
        return suggestions.getMostUsedSuggestion();
    }

    @Override
    public String toString() {
        return sequenceToSuggestions.toString();
    }
}
