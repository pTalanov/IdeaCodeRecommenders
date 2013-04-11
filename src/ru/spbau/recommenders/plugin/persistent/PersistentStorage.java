package ru.spbau.recommenders.plugin.persistent;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.io.PersistentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.recommenders.plugin.data.Suggestions;
import ru.spbau.recommenders.plugin.storage.MethodStatisticsStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static ru.spbau.recommenders.plugin.persistent.Externalizers.CLASS_AND_CALL_SEQUENCE_KEY_DESCRIPTOR;
import static ru.spbau.recommenders.plugin.persistent.Externalizers.SUGGESTIONS_EXTERNALIZER;

/**
 * @author Pavel Talanov
 */
public final class PersistentStorage implements MethodStatisticsStorage {

    private static final Logger LOG = Logger.getInstance("#ru.spbau.recommenders.plugin.persistent.PersistentStorage");


    public static final String RECOMMENDERS_DIR_NAME = "recommenders";
    public static final String RECOMMENDATIONS_FILE_NAME = "recommendations.data";
    private PersistentHashMap<ClassAndCallSequence, Suggestions> storage;

    public PersistentStorage(@NotNull Project project) {
        try {
            storage = new PersistentHashMap<ClassAndCallSequence, Suggestions>(
                    getStorageFile(project), CLASS_AND_CALL_SEQUENCE_KEY_DESCRIPTOR, SUGGESTIONS_EXTERNALIZER);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @NotNull
    private static File getStorageFile(@NotNull Project project) {
        String systemDirPath = PathUtil.getCanonicalPath(PathManager.getSystemPath());
        File recommendersDir = new File(systemDirPath, RECOMMENDERS_DIR_NAME);
        //TODO: see CompilerPaths#getPresentableName()
        File projectDir = new File(recommendersDir, project.getName() + "." + project.getLocationHash());
        File result = new File(projectDir, RECOMMENDATIONS_FILE_NAME);
        FileUtil.createIfDoesntExist(result);
        return result;
    }

    public void close() {
        try {
            storage.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void registerCallSequence(@NotNull String typeName, @NotNull List<String> callSequence, @NotNull String methodToSuggest) {
        ClassAndCallSequence key = new ClassAndCallSequence(typeName, callSequence);
        try {
            Suggestions value = storage.get(key);
            if (value == null) {
                value = new Suggestions();
            }
            value.registerUsage(methodToSuggest);
            storage.put(key, value);
        } catch (IOException e) {
            LOG.error(e);
        }

    }

    @Nullable
    @Override
    public Suggestions getSuggestions(@NotNull String typeName, @NotNull List<String> callSequence) {
        try {
            return storage.get(new ClassAndCallSequence(typeName, callSequence));
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "PersistentStorage{" +
                "storage=" + storage +
                '}';
    }
}
