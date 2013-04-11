package ru.spbau.recommenders.plugin.storage.inmemory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.recommenders.plugin.data.CallSequence;
import ru.spbau.recommenders.plugin.data.Suggestions;

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

    public void registerSequence(@NotNull List<String> callSequence, @NotNull String methodToSuggest) {
        registerSequence(new CallSequence(callSequence), methodToSuggest);
    }

    @Nullable
    public Suggestions getSuggestion(@NotNull List<String> callSequence) {
        Suggestions suggestions = sequenceToSuggestions.get(new CallSequence(callSequence));
        if (suggestions == null) {
            return null;
        }
        return suggestions;
    }

    @Override
    public String toString() {
        return sequenceToSuggestions.toString();
    }
}
