package com.thoughtworks.gauge.findUsages;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.thoughtworks.gauge.findUsages.helper.ReferenceSearchHelper;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.HookUtil;
import com.thoughtworks.gauge.util.StepUtil;

public class ImplUsageProvider implements ImplicitUsageProvider {
    private static final ReferenceSearchHelper helper = new ReferenceSearchHelper();

    public boolean isImplicitUsage(PsiElement element) {
        Module module = GaugeUtil.moduleForPsiElement(element);
        if (module == null || !GaugeUtil.isGaugeModule(module)) return false;
        if (element instanceof PsiClassImpl) return isClassUsed((PsiClassImpl) element);
        if (element instanceof PsiParameterImpl) return isParameterUsed((PsiParameterImpl) element);
        return isElementUsed(element);
    }

    private boolean isClassUsed(PsiClassImpl element) {
        for (PsiMethod psiMethod : element.getMethods())
            if (StepUtil.getGaugeStepAnnotationValues(psiMethod).size() > 0 || HookUtil.isHook(psiMethod)) return true;
        return false;
    }

    private boolean isParameterUsed(PsiParameterImpl element) {
        return GaugeUtil.isGaugeElement(element.getDeclarationScope());
    }

    private boolean isElementUsed(PsiElement element) {
        if (HookUtil.isHook(element)) return true;
        boolean isGaugeElement = GaugeUtil.isGaugeElement(element);
        if (!isGaugeElement) return false;
        StepCollector collector = new StepCollector(element.getProject());
        collector.collect();
        return helper.getPsiElements(collector, element).size() > 0;
    }

    public boolean isImplicitRead(final PsiElement element) {
        return false;
    }

    public boolean isImplicitWrite(final PsiElement element) {
        return false;
    }
}
