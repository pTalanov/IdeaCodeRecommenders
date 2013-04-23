package ru.spbau.recommenders.plugin;

import com.google.common.collect.Sets;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author Pavel Talanov
 */
public final class MethodStatisticsProjectComponent implements ProjectComponent {

    @NotNull
    public static MethodStatisticsProjectComponent getInstance(@NotNull Project project) {
        return project.getComponent(MethodStatisticsProjectComponent.class);
    }

    @Nullable
    public String getMostUsedMethodName(@NotNull String typeName, @NotNull List<String> callSequence) {
        return methodCallData.getMostCalledMethod(typeName, callSequence);
    }

    @NotNull
    private final MethodCallData methodCallData = new MethodCallData();

    @NotNull
    private final Project project;

    public MethodStatisticsProjectComponent(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void initComponent() {
//        JavaBuilder.registerClassPostProcessor(new RecommendersClassPostProcessor());
    }

    @Override
    public void disposeComponent() {
        //do nothing
    }

    @NotNull
    public String getComponentName() {
        return "MethodStatisticsProjectComponent";
    }

    public void projectOpened() {
        StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
            @Override
            public void run() {

//                JavaBuilder.registerClassPostProcessor(new RecommendersClassPostProcessor());
                CallStatisticsCollector callStatisticsCollector = new CallStatisticsCollector(methodCallData);
                Set<PsiFile> allPsiFiles = getAllPsiFiles();
                System.out.println(allPsiFiles);
                for (PsiFile psiFile : allPsiFiles) {
                    callStatisticsCollector.collectStatistics(psiFile);
                }
                methodCallData.printStatistics();
            }
        });
    }

    @Override
    public void projectClosed() {
        //do nothing
    }

    @NotNull
    private Set<PsiFile> getAllPsiFiles() {
        final Set<PsiFile> files = Sets.newLinkedHashSet();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            final ModuleFileIndex index = ModuleRootManager.getInstance(module).getFileIndex();
            index.iterateContent(new ContentIterator() {
                @Override
                public boolean processFile(VirtualFile file) {
                    if (file.isDirectory()) return true;
                    if (!index.isInSourceContent(file) && !index.isInTestSourceContent(file)) return true;

                    final FileType fileType = FileTypeManager.getInstance().getFileTypeByFile(file);
                    if (fileType != JavaFileType.INSTANCE) return true;
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    files.add(psiFile);
                    return true;
                }
            });
        }
        return files;
    }


}
