package ru.spbau.recommenders.plugin.persistent;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.Function;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.io.PersistentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.jps.incremental.recommenders.RecommendersMessageData;
import ru.spbau.jps.incremental.recommenders.SequencesData;
import ru.spbau.recommenders.plugin.data.Suggestions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.spbau.recommenders.plugin.persistent.Externalizers.*;

/**
 * @author Pavel Talanov
 */
public final class PersistentStorage {

    private static final Logger LOG = Logger.getInstance("#ru.spbau.recommenders.plugin.persistent.PersistentStorage");


    private static final String RECOMMENDERS_DIR_NAME = "recommenders";
    private static final String RECOMMENDATIONS_MAIN_FILE_NAME = "recommendations.data";
    private static final String RECOMMENDATIONS_DIFF_FILE_NAME = "recommendations_diff.data";

    @NotNull
    private PersistentHashMap<ClassAndCallSequence, Suggestions> mainStorage;
    @NotNull
    private PersistentHashMap<String, Map<ClassAndCallSequence, Suggestions>> differenceStorage;

    public PersistentStorage(@NotNull Project project) {
        try {
            mainStorage = new PersistentHashMap<ClassAndCallSequence, Suggestions>(
                    getStorageFile(project, RECOMMENDATIONS_MAIN_FILE_NAME),
                    CLASS_AND_CALL_SEQUENCE_KEY_DESCRIPTOR, SUGGESTIONS_EXTERNALIZER);
            differenceStorage = new PersistentHashMap<String, Map<ClassAndCallSequence, Suggestions>>(
                    getStorageFile(project, RECOMMENDATIONS_DIFF_FILE_NAME),
                    STRING_EXTERNALIZER, DIFFERENCES_EXTERNALIZER);

        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @NotNull
    private static File getStorageFile(@NotNull Project project, @NotNull String fileName) {
        String systemDirPath = PathUtil.getCanonicalPath(PathManager.getSystemPath());
        File recommendersDir = new File(systemDirPath, RECOMMENDERS_DIR_NAME);
        //TODO: see CompilerPaths#getPresentableName()
        File projectDir = new File(recommendersDir, project.getName() + "." + project.getLocationHash());
        File result = new File(projectDir, fileName);
        FileUtil.createIfDoesntExist(result);
        return result;
    }

    public void close() {
        try {
            mainStorage.close();
            differenceStorage.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public void processMessage(@NotNull RecommendersMessageData messageData) {
        Map<ClassAndCallSequence, Suggestions> newData
                = processSequencesData(messageData.getSequencesData());
        String sourceId = messageData.getSourceId();
        try {
            Map<ClassAndCallSequence, Suggestions> oldData = differenceStorage.get(sourceId);
            differenceStorage.put(sourceId, newData);
            Map<ClassAndCallSequence, Suggestions> difference =
                    oldData != null ? calculateDifference(newData, oldData) : newData;
            storeDifference(difference);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    private void storeDifference(@NotNull Map<ClassAndCallSequence, Suggestions> difference) throws IOException {
        for (Map.Entry<ClassAndCallSequence, Suggestions> entryToMergeIn : difference.entrySet()) {
            ClassAndCallSequence key = entryToMergeIn.getKey();
            Suggestions valueToStore = mainStorage.get(key);
            if (valueToStore == null) {
                valueToStore = new Suggestions();
            }
            valueToStore.mergeIn(entryToMergeIn.getValue());
            mainStorage.put(key, valueToStore);
        }
    }

    @NotNull
    private Map<ClassAndCallSequence, Suggestions> calculateDifference(@NotNull Map<ClassAndCallSequence, Suggestions> newData,
                                                                       @NotNull Map<ClassAndCallSequence, Suggestions> oldData) {
        Map<ClassAndCallSequence, Suggestions> result = new HashMap<ClassAndCallSequence, Suggestions>(newData);
        for (Map.Entry<ClassAndCallSequence, Suggestions> entryToSubtract : oldData.entrySet()) {
            Suggestions value = result.get(entryToSubtract.getKey());
            if (value == null) {
                value = new Suggestions();
            }
            value.subtract(entryToSubtract.getValue());
        }
        return result;
    }

    @NotNull
    private Map<ClassAndCallSequence, Suggestions> processSequencesData(@NotNull SequencesData sequencesData) {
        Map<ClassAndCallSequence, Suggestions> result = new HashMap<ClassAndCallSequence, Suggestions>();
        for (Map.Entry<String, Map<List<String>, Integer>> stringMapEntry : sequencesData.toMap().entrySet()) {
            String typeName = stringMapEntry.getKey();
            String realTypeName = typeName.replace("/", ".");
            for (Map.Entry<List<String>, Integer> sequenceAndCount : stringMapEntry.getValue().entrySet()) {
                List<String> sequence = sequenceAndCount.getKey();
                List<String> realSequence = ContainerUtil.map(sequence, new Function<String, String>() {
                    @Override
                    public String fun(String s) {
                        return s.substring(0, s.indexOf("("));
                    }
                });
                Integer count = sequenceAndCount.getValue();
                processOneSequence(result, realTypeName, realSequence, count);
            }
        }
        return result;
    }

    private void processOneSequence(@NotNull Map<ClassAndCallSequence, Suggestions> result,
                                    @NotNull String typeName,
                                    @NotNull List<String> sequence,
                                    @NotNull Integer count) {
        for (int i = 0; i < sequence.size(); ++i) {
            ClassAndCallSequence key = new ClassAndCallSequence(typeName, sequence.subList(0, i));
            Suggestions suggestions = result.get(key);
            if (suggestions == null) {
                suggestions = new Suggestions();
            }
            suggestions.registerUsage(sequence.get(i), count);
            result.put(key, suggestions);
        }
    }

    @Nullable
    public Suggestions getSuggestions(@NotNull String typeName, @NotNull List<String> callSequence) {
        try {
            return mainStorage.get(new ClassAndCallSequence(typeName, callSequence));
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }
}
