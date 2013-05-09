package ru.spbau.jps.incremental.recommenders;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class SequencesData implements Serializable {

    @NotNull
    private final Map<String, Map<List<String>, Integer>> typeNameToSequenceToCount;

    public SequencesData(@NotNull Map<String, Map<List<String>, Integer>> typeNameToSequenceToCount) {
        this.typeNameToSequenceToCount = typeNameToSequenceToCount;
    }

    @NotNull
    public Map<String, Map<List<String>, Integer>> toMap() {
        return typeNameToSequenceToCount;
    }
}
