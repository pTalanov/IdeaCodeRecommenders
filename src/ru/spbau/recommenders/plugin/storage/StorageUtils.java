package ru.spbau.recommenders.plugin.storage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class StorageUtils {

    private StorageUtils() {
    }

    public static void saveCallSequenceToStorage(@NotNull String typeName, @NotNull List<String> sequence,
                                                 @NotNull MethodStatisticsStorage storage) {
        for (int i = 0; i < sequence.size(); ++i) {
            storage.registerCallSequence(typeName, sequence.subList(0, i), sequence.get(i));
        }
    }
}
