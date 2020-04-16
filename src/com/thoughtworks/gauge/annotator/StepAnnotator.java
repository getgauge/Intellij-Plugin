/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.NotNull;

public class StepAnnotator implements com.intellij.lang.annotation.Annotator {

    private AnnotationHelper helper;

    public StepAnnotator(AnnotationHelper helper) {
        this.helper = helper;
    }

    public StepAnnotator() {
        this.helper = new AnnotationHelper();
    }

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (!helper.isGaugeModule(element)) return;
        if (element instanceof SpecStep)
            createWarning(element, holder, (SpecStep) element);
        else if (element instanceof ConceptStep) {
            SpecStepImpl step = new SpecStepImpl(element.getNode());
            step.setConcept(true);
            createWarning(element, holder, step);
        }
    }

    private void createWarning(PsiElement element, AnnotationHolder holder, SpecStep step) {
        if (helper.isEmpty(step))
            holder.createErrorAnnotation(element.getTextRange(), "Step should not be blank");
        else if (!helper.isImplemented(step, helper.getModule(step)))
            holder.createErrorAnnotation(element.getTextRange(), "Undefined Step").registerFix(new CreateStepImplFix(step));
    }
}
