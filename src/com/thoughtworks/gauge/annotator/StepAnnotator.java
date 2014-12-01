package com.thoughtworks.gauge.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

public class StepAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

        if (element instanceof SpecStep) {
            SpecStep step = (SpecStep) element;
            if (!StepUtil.isImplementedStep(step, element.getProject())) {
                holder.createWeakWarningAnnotation(element.getTextRange(), "Undefined Step").registerFix(new CreateStepImplFix(step));
            }
        }
    }
}
