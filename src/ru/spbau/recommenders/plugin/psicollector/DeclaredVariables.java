package ru.spbau.recommenders.plugin.psicollector;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class DeclaredVariables {

    @NotNull
    public static DeclaredVariables collect(@NotNull PsiMethod method) {
        DeclaredVariables declaredVariables = new DeclaredVariables();
        declaredVariables.addParameterDeclarations(method);
        declaredVariables.addLocalVariableDeclarations(method);
        return declaredVariables;
    }

    @NotNull
    private final Map<String, String> nameToType = new HashMap<String, String>();

    private void addLocalVariableDeclarations(@NotNull PsiMethod method) {
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
                            registerDeclaration(name, typeName);
                        }
                    }
                }
            }
        });
    }

    private void addParameterDeclarations(@NotNull PsiMethod method) {
        PsiParameterList parameterList = method.getParameterList();
        for (PsiParameter psiParameter : parameterList.getParameters()) {
            String name = psiParameter.getName();
            String typeName = psiParameter.getType().getCanonicalText();
            if (name != null) {
                registerDeclaration(name, typeName);
            }
        }
    }

    private void registerDeclaration(@NotNull String name, @NotNull String typeName) {
        nameToType.put(name, typeName);
    }

    @Nullable
    public String getType(@NotNull String name) {
        return nameToType.get(name);
    }

    public boolean isDeclared(@NotNull String name) {
        return nameToType.containsKey(name);
    }

    @NotNull
    public Collection<String> getDeclaredVariables() {
        return nameToType.keySet();
    }
}
