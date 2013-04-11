package ru.spbau.recommenders.plugin.storage.inmemory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.recommenders.plugin.data.Suggestions;
import ru.spbau.recommenders.plugin.storage.MethodStatisticsStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class MethodCallData implements MethodStatisticsStorage {

    @NotNull
    private final Map<String, CallSequenceStatistics> typeNameToStatistics = new HashMap<String, CallSequenceStatistics>();

    @Override
    public void registerCallSequence(@NotNull String typeName,
                                     @NotNull List<String> callSequence,
                                     @NotNull String methodToSuggest) {
        getStatistics(typeName).registerSequence(callSequence, methodToSuggest);
    }

    @NotNull
    private CallSequenceStatistics getStatistics(@NotNull String typeName) {
        CallSequenceStatistics callSequenceStatistics = typeNameToStatistics.get(typeName);
        if (callSequenceStatistics == null) {
            callSequenceStatistics = new CallSequenceStatistics();
            typeNameToStatistics.put(typeName, callSequenceStatistics);
        }
        return callSequenceStatistics;
    }

    @Nullable
    @Override
    public Suggestions getSuggestions(@NotNull String typeName, @NotNull List<String> callSequence) {
        CallSequenceStatistics callSequenceStatistics = typeNameToStatistics.get(typeName);
        if (callSequenceStatistics == null) {
            return null;
        }
        return callSequenceStatistics.getSuggestion(callSequence);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String type : typeNameToStatistics.keySet()) {
            sb.append("For type [").append(type).append("]:\n");
            sb.append(typeNameToStatistics.get(type)).append("\n\n");
        }
        return sb.toString();
    }
}
