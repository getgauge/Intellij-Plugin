package com.thoughtworks.gauge.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParamAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        if (psiElement instanceof PsiMethod) {
            List<String> aliases = StepUtil.getGaugeStepAnnotationValues((PsiMethod) psiElement);
            for (String alias : aliases) {
                StepValue value = SpecPsiImplUtil.getStepValueFor(psiElement, alias, false);
                if (value.getParameters().size() != ((PsiMethod) psiElement).getParameterList().getParametersCount())
                    createWarning((PsiMethod) psiElement, holder, alias, value);
            }
        }
    }

    private void createWarning(@NotNull PsiMethod psiElement, @NotNull AnnotationHolder holder, String alias, StepValue value) {
        int actual = psiElement.getParameterList().getParametersCount();
        int expected = value.getParameters().size();
        holder.createWarningAnnotation(psiElement.getParameterList().getTextRange(), getWarning(alias, actual, expected));
    }

    private String getWarning(String step, int actual, int expected) {
        return String.format("Parameter count mismatch(found [%d] expected [%d]) with step annotation : \"%s\". ",actual, expected, step);
    }
}
