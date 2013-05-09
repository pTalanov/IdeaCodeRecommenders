package ru.spbau.jps.incremental.recommenders;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class RecommendersMessageData implements Serializable {

    @NotNull
    private final String sourceId;

    @NotNull
    private final SequencesData sequencesData;

    public RecommendersMessageData(@NotNull String sourceId,
                                   @NotNull Map<String, Map<List<String>, Integer>> sequencesData) {
        this.sourceId = sourceId;
        this.sequencesData = new SequencesData(sequencesData);
    }

    @NotNull
    public String getSourceId() {
        return sourceId;
    }

    @NotNull
    public SequencesData getSequencesData() {
        return sequencesData;
    }
}
