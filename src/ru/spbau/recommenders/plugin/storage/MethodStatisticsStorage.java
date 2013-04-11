package ru.spbau.recommenders.plugin.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.recommenders.plugin.data.Suggestions;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public interface MethodStatisticsStorage {

    void registerCallSequence(@NotNull String typeName, @NotNull List<String> callSequence, @NotNull String methodToSuggest);

    @Nullable
    public Suggestions getSuggestions(@NotNull String typeName, @NotNull List<String> callSequence);
}
