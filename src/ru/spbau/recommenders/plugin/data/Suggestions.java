package ru.spbau.recommenders.plugin.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Pavel Talanov
 */
public final class Suggestions {

    @NotNull
    private final Map<String, Integer> methodNameToUsageCount = new HashMap<String, Integer>();

    public void registerUsage(@NotNull String methodName, int count) {
        //TODO: manage overflows
        Integer usageCount = methodNameToUsageCount.get(methodName);
        if (usageCount == null) {
            methodNameToUsageCount.put(methodName, count);
        } else {
            methodNameToUsageCount.put(methodName, usageCount + count);
        }
    }

    public void subtract(@NotNull Suggestions other) {
        for (Map.Entry<String, Integer> entry : other.methodNameToUsageCount.entrySet()) {
            registerUsage(entry.getKey(), -entry.getValue());
        }
    }

    public void mergeIn(@NotNull Suggestions other) {
        for (Map.Entry<String, Integer> entry : other.methodNameToUsageCount.entrySet()) {
            registerUsage(entry.getKey(), entry.getValue());
        }
    }

    @Nullable
    public String getMostUsedSuggestion() {
        if (methodNameToUsageCount.isEmpty()) {
            return null;
        }
        Map.Entry<String, Integer> mostCalledMethod = Collections.max(methodNameToUsageCount.entrySet(), new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });
        return mostCalledMethod.getKey();
    }

    @NotNull
    public List<String> getMostUsedSuggestions() {
        List<String> suggestions = new ArrayList<String>();
        if (methodNameToUsageCount.isEmpty()) {
            return suggestions;
        }
        for (Map.Entry<String, Integer> calledMethod : getMostUsedCalledMethods()) {
            suggestions.add(calledMethod.getKey());
        }
        return suggestions;
    }

    @NotNull
    private List<Map.Entry<String, Integer>> getMostUsedCalledMethods() {
        int suggestionSize = methodNameToUsageCount.size() / 5 + 1;
        ArrayList<Map.Entry<String, Integer>> calledMethods = new ArrayList<Map.Entry<String, Integer>>(methodNameToUsageCount.entrySet());
        Collections.sort(calledMethods, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        return calledMethods.subList(0, suggestionSize);
    }


    @Override
    public String toString() {
        return methodNameToUsageCount.toString();
    }

    @NotNull
    public Map<String, Integer> toMap() {
        return methodNameToUsageCount;
    }

    @NotNull
    public static Suggestions fromMap(@NotNull Map<String, Integer> data) {
        Suggestions suggestions = new Suggestions();
        suggestions.methodNameToUsageCount.putAll(data);
        return suggestions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suggestions that = (Suggestions) o;

        return methodNameToUsageCount.equals(that.methodNameToUsageCount);

    }

    @Override
    public int hashCode() {
        return methodNameToUsageCount.hashCode();
    }
}
