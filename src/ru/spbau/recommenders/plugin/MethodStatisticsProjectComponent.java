package ru.spbau.recommenders.plugin;

import com.intellij.compiler.server.CustomBuilderMessageHandler;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.jps.incremental.recommenders.RecommendersBuilder;
import ru.spbau.jps.incremental.recommenders.RecommendersMessageData;
import ru.spbau.jps.incremental.recommenders.StringSerializer;
import ru.spbau.recommenders.plugin.data.Suggestions;
import ru.spbau.recommenders.plugin.persistent.PersistentStorage;

import java.io.IOException;
import java.util.List;

/**
 * @author Pavel Talanov
 * @author Goncharova Irina
 */
public final class MethodStatisticsProjectComponent implements ProjectComponent {
    @NotNull
    public static MethodStatisticsProjectComponent getInstance(@NotNull Project project) {
        return project.getComponent(MethodStatisticsProjectComponent.class);
    }

    @Nullable
    public Suggestions getRecommendation(@NotNull String typeName, @NotNull List<String> callSequence) {
        return storage.getSuggestions(typeName, callSequence);
    }

    @NotNull
    private PersistentStorage storage;

    @NotNull
    private StringSerializer<RecommendersMessageData> deserializer
            = new StringSerializer<RecommendersMessageData>();


    @NotNull
    private final Project project;

    public MethodStatisticsProjectComponent(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
        storage = new PersistentStorage(project);
        StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {
                project.getMessageBus().connect().subscribe(CustomBuilderMessageHandler.TOPIC, new CustomBuilderMessageHandler() {
                    @Override
                    public void messageReceived(String builderId, String messageType, String messageText) {
                        if (builderId.equals(RecommendersBuilder.BUILDER_ID) && messageType.equals(RecommendersBuilder.MESSAGE_TYPE)) {
                            try {
                                RecommendersMessageData message = deserializer.deserialize(messageText);
                                storage.proccessDiff(message.getSequencesData());
                            } catch (IOException e) {
                                //TODO
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void disposeComponent() {
        storage.close();
    }

    @NotNull
    public String getComponentName() {
        return "MethodStatisticsProjectComponent";
    }

    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
        //do nothing
    }

}
