package ru.spbau.recommenders.plugin.storage.inmemory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class MethodCallData {

    @NotNull
    private final Map<String, CallSequenceStatistics> typeNameToStatistics = new HashMap<String, CallSequenceStatistics>();

    public void registerCallSequence(@NotNull String typeName, @NotNull List<String> callSequence) {
        getStatistics(typeName).registerSequence(callSequence);
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
    public String getMostCalledMethod(@NotNull String typeName, @NotNull List<String> callSequence) {
        CallSequenceStatistics callSequenceStatistics = typeNameToStatistics.get(typeName);
        if (callSequenceStatistics == null) {
            return null;
        }
        return callSequenceStatistics.getSuggestion(callSequence);
    }

    public void printStatistics() {
        for (String type : typeNameToStatistics.keySet()) {
            System.out.println("For type [" + type + "]:\n");
            System.out.println(typeNameToStatistics.get(type));
            System.out.println("----------------------------\n");
        }
    }
}
