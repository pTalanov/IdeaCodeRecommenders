package ru.spbau.recommenders.plugin.utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.PsiSubstitutorImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Pavel Talanov
 * @author Goncharova Irina
 * @author Osipov Stanislav
 */
public final class PsiUtils {
    private PsiUtils() {
    }

    @Nullable
    public static String getReferencedName(@NotNull PsiMethodCallExpression expression) {
        PsiReferenceExpression methodExpression = expression.getMethodExpression();
        PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
        if (qualifierExpression instanceof PsiReferenceExpression) {
            return ((PsiReferenceExpression) qualifierExpression).getReferenceName();
        } else {
            return null;
        }
    }

    @NotNull
    public static List<String> collectMethodCallsBeforeElementForQualifier(@NotNull final PsiElement position,
                                                                           @NotNull final PsiReferenceExpression qualifier) {
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(position, PsiMethod.class);
        //TODO:
        if (containingMethod == null) {
            return Collections.emptyList();
        }
        PsiCodeBlock body = containingMethod.getBody();
        //TODO:
        if (body == null) {
            return Collections.emptyList();
        }
        final List<String> prefixSequence = new ArrayList<String>();
        body.accept(new JavaRecursiveElementVisitor() {

            private boolean stopped = false;

            @Override
            public void visitElement(PsiElement element) {
                if (stopped) {
                    return;
                }

                super.visitElement(element);
                if (element.equals(position)) {
                    stopped = true;
                }
            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                if (stopped) {
                    return;
                }

                super.visitMethodCallExpression(expression);

                String referencedName = getReferencedName(expression);
                if ((referencedName != null) && (referencedName.equals(qualifier.getReferenceName()))) {
                    prefixSequence.add(getMethodSignature(expression));
                }
            }
        });
        return prefixSequence;
    }

    @Nullable
    private static String getMethodSignature(@NotNull PsiMethodCallExpression expression) {
        PsiMethod psiMethod = expression.resolveMethod();
        if (psiMethod == null) {
            return null;
        }
        return getSignatureString(psiMethod);
    }

    @Nullable
    public static String getSignatureString(@NotNull PsiMethod psiMethod) {
        PsiSubstitutor substitutor = createTypeSubstitutor(psiMethod);
        PsiType unsubstitutedReturnType = psiMethod.getReturnTypeNoResolve();
        if (unsubstitutedReturnType == null) {
            return null;
        }
        PsiType returnType = substitutor.substitute(unsubstitutedReturnType);
        PsiType[] parameterTypes = psiMethod.getSignature(substitutor).getParameterTypes();
        return constructSignatureString(psiMethod.getName(), returnType, parameterTypes);
    }

    @NotNull
    private static PsiSubstitutor createTypeSubstitutor(@NotNull PsiMethod psiMethod) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
        Map<PsiTypeParameter, PsiType> map = new HashMap<PsiTypeParameter, PsiType>();
        for (PsiTypeParameter parameter : psiMethod.getTypeParameters()) {
            PsiClass superClass = parameter.getSuperClass();
            if (superClass != null) {
                map.put(parameter, factory.createType(superClass));
            }
        }
        return PsiSubstitutorImpl.createSubstitutor(map);
    }

    @NotNull
    private static String constructSignatureString(@NotNull String methodName,
                                                   @NotNull PsiType returnType,
                                                   @NotNull PsiType[] parameterTypes
    ) {
        StringBuilder result = new StringBuilder();
        result.append(methodName).append("(");
        for (int i = 0; i < parameterTypes.length; ++i) {
            result.append(getTypeNameInByteCodeFormat(parameterTypes[i]));
            if (i < parameterTypes.length - 1) {
                result.append(",");
            }
        }
        result.append(")").append(getTypeNameInByteCodeFormat(returnType));
        return result.toString();
    }

    @NotNull
    public static String getTypeNameInByteCodeFormat(@NotNull PsiType parameterType) {
        String canonicalText = parameterType.getCanonicalText();
        int leftAngleBracketPosition = canonicalText.indexOf("<");
        String result;
        if (leftAngleBracketPosition >= 0) {
            result = canonicalText.substring(0, leftAngleBracketPosition);
        } else {
            result = canonicalText;
        }
        return result.replace(".", "/");
    }
}
