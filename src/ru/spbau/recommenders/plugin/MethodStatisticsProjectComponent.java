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
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
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
    public String getMostUsedMethodName(@NotNull String typeName) {
        return methodCallData.getMostCalledMethod(typeName);
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
        //do nothing
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
                for (PsiFile psiFile : getAllPsiFiles()) {
                    psiFile.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitMethod(PsiMethod method) {
                            //TODO: need super call?
                            super.visitMethod(method);
                            if (method.getBody() != null) {
                                processMethod(method);
                            }
                        }
                    });
                }
                methodCallData.printStatistics();
            }
        });
    }

    @Override
    public void projectClosed() {
        //do nothing
    }

    private void processMethod(@NotNull PsiMethod method) {
        final DeclaredVariables declaredVariables = new DeclaredVariables();
        addParameters(method, declaredVariables);
        addLocalVariableDeclarations(method, declaredVariables);
        PsiCodeBlock body = method.getBody();
        assert body != null;
        body.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                //TODO: unneeded super call?
                super.visitMethodCallExpression(expression);
                PsiReferenceExpression methodExpression = expression.getMethodExpression();
                PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
                if (qualifierExpression instanceof PsiReferenceExpression) {
                    String referencedName = ((PsiReferenceExpression) qualifierExpression).getReferenceName();
                    String methodName = methodExpression.getReferenceName();
                    if (referencedName != null && methodName != null) {
                        String type = declaredVariables.getType(referencedName);
                        if (type != null) {
                            methodCallData.registerCall(type, methodName);
                        }
                    }
                }
            }
        });
    }

    private void addLocalVariableDeclarations(@NotNull PsiMethod method,
                                              @NotNull final DeclaredVariables declaredVariables) {
        PsiCodeBlock body = method.getBody();
        assert body != null;
        body.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                PsiElement[] declaredElements = statement.getDeclaredElements();
                for (PsiElement declaredElement : declaredElements) {
                    if (declaredElement instanceof PsiLocalVariable) {
                        String name = ((PsiLocalVariable) declaredElement).getName();
                        String typeName = ((PsiLocalVariable) declaredElement).getType().getCanonicalText();
                        if (name != null) {
                            declaredVariables.registerDeclaration(name, typeName);
                        }
                    }
                }
            }
        });
    }

    private void addParameters(@NotNull PsiMethod method, @NotNull DeclaredVariables declaredVariables) {
        PsiParameterList parameterList = method.getParameterList();
        for (PsiParameter psiParameter : parameterList.getParameters()) {
            String name = psiParameter.getName();
            String typeName = psiParameter.getType().getCanonicalText();
            if (name != null) {
                declaredVariables.registerDeclaration(name, typeName);
            }
        }
    }


    private class DeclaredVariables {
        @NotNull
        private final Map<String, String> nameToType = new HashMap<String, String>();

        void registerDeclaration(@NotNull String name, @NotNull String typeName) {
            nameToType.put(name, typeName);
        }

        @Nullable
        String getType(@NotNull String name) {
            return nameToType.get(name);
        }
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
