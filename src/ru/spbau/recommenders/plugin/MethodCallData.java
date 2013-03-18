package ru.spbau.recommenders.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public class MethodCallData {

    @NotNull
    private final Map<String, Map<String, Integer>> typeNameToMethodCallCount = new HashMap<String, Map<String, Integer>>();

    public void registerCall(@NotNull String typeName, @NotNull String methodName) {
        Map<String, Integer> methodCallCount = getMethodCallCount(typeName);
        Integer count = methodCallCount.get(methodName);
        if (count == null) {
            methodCallCount.put(methodName, 1);
        } else {
            methodCallCount.put(methodName, count + 1);
        }
    }

    @NotNull
    private Map<String, Integer> getMethodCallCount(@NotNull String typeName) {
        Map<String, Integer> methodCallCount = typeNameToMethodCallCount.get(typeName);
        if (methodCallCount == null) {
            methodCallCount = new HashMap<String, Integer>();
            typeNameToMethodCallCount.put(typeName, methodCallCount);
        }
        return methodCallCount;
    }

    @Nullable
    public String getMostCalledMethod(@NotNull String typeName) {
        Map<String, Integer> methodCallCount = typeNameToMethodCallCount.get(typeName);
        if (methodCallCount == null) {
            return null;
        }
        assert !methodCallCount.isEmpty();
        Map.Entry<String, Integer> mostCalledMethod = Collections.max(methodCallCount.entrySet(), new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });
        return mostCalledMethod.getKey();
    }

    public void printStatistics() {
        for (String type : typeNameToMethodCallCount.keySet()) {
            System.out.println("For type [" + type + "]:\n");
            for (Map.Entry<String, Integer> methodWithCount : typeNameToMethodCallCount.get(type).entrySet()) {
                System.out.println("\t" + methodWithCount.getKey() + ": " + methodWithCount.getValue());
            }
            System.out.println("----------------------------\n");
        }
    }


}
