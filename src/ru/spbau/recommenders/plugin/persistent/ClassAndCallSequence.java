package ru.spbau.recommenders.plugin.persistent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class ClassAndCallSequence {

    @NotNull
    private final String className;
    @NotNull
    private final List<String> callSequence;

    @NotNull
    public String getClassName() {
        return className;
    }

    @NotNull
    public List<String> getCallSequence() {
        return callSequence;
    }

    public ClassAndCallSequence(@NotNull String className, @NotNull List<String> callSequence) {
        this.className = className;
        this.callSequence = callSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassAndCallSequence that = (ClassAndCallSequence) o;

        if (!callSequence.equals(that.callSequence)) return false;
        if (!className.equals(that.className)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + callSequence.hashCode();
        return result;
    }
}
