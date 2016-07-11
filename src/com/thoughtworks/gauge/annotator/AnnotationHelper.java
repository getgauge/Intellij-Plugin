package com.thoughtworks.gauge.annotator;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.util.StepUtil;

import java.util.List;
import java.util.stream.Collectors;

class AnnotationHelper {
    private static ModuleHelper helper = new ModuleHelper();

    boolean isImplemented(SpecStep step, Module module) {
        return StepUtil.isImplementedStep(step, module);
    }

    boolean isEmpty(SpecStep step) {
        return step.getStepValue().getStepText().trim().isEmpty();
    }

    boolean isGaugeModule(PsiElement element) {
        return helper.isGaugeModule(element);
    }

    Module getModule(SpecStep step) {
        return helper.getModule(step);
    }

    List<StepValue> getStepValues(PsiMethod psiElement) {
        return StepUtil.getGaugeStepAnnotationValues(psiElement).stream().map(s -> SpecPsiImplUtil.getStepValueFor(psiElement, s, false)).collect(Collectors.toList());
    }
}
