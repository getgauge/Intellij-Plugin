package com.thoughtworks.gauge.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

public class StepAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof SpecStep) {
            SpecStep step = (SpecStep) element;
            createWarning(element, holder, step);
        }
        else if (element instanceof ConceptStep) {
            SpecStepImpl step = new SpecStepImpl(element.getNode());
            step.setConcept(true);
            createWarning(element, holder, step);
        }
    }

    private void createWarning(PsiElement element, AnnotationHolder holder, SpecStep step) {
        if (!StepUtil.isImplementedStep(step, element.getProject()))
            holder.createWeakWarningAnnotation(element.getTextRange(), "Undefined Step").registerFix(new CreateStepImplFix(step));
    }
}
