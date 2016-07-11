package com.thoughtworks.gauge.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.StepValue;
import org.jetbrains.annotations.NotNull;

public class ParamAnnotator implements Annotator {
    private AnnotationHelper helper;

    public ParamAnnotator(AnnotationHelper helper) {
        this.helper = helper;
    }

    public ParamAnnotator() {
        this.helper = new AnnotationHelper();
    }


    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        if (helper.isGaugeModule(psiElement) && psiElement instanceof PsiMethod)
            helper.getStepValues((PsiMethod) psiElement).stream()
                    .filter(value -> value.getParameters().size() != ((PsiMethod) psiElement).getParameterList().getParametersCount())
                    .forEach(value -> createWarning((PsiMethod) psiElement, holder, value.getStepAnnotationText(), value));
    }

    private void createWarning(@NotNull PsiMethod psiElement, @NotNull AnnotationHolder holder, String alias, StepValue value) {
        int actual = psiElement.getParameterList().getParametersCount();
        int expected = value.getParameters().size();
        holder.createErrorAnnotation(psiElement.getParameterList().getTextRange(), getWarning(alias, actual, expected));
    }

    private String getWarning(String step, int actual, int expected) {
        return String.format("Parameter count mismatch(found [%d] expected [%d]) with step annotation : \"%s\". ", actual, expected, step);
    }
}
