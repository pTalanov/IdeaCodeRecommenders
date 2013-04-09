package ru.spbau.recommenders.plugin.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class CallSequence {

    @NotNull
    private final List<String> sequence;

    public CallSequence(@NotNull List<String> sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallSequence that = (CallSequence) o;

        return sequence.equals(that.sequence);
    }

    @Override
    public int hashCode() {
        return sequence.hashCode();
    }

    @Override
    public String toString() {
        return sequence.toString();
    }
}
