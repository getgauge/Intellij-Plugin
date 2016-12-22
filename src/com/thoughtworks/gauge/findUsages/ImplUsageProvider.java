package com.thoughtworks.gauge.findUsages;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.thoughtworks.gauge.findUsages.helper.ReferenceSearchHelper;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.HookUtil;
import com.thoughtworks.gauge.util.StepUtil;

public class ImplUsageProvider implements ImplicitUsageProvider {
    private ReferenceSearchHelper searchHelper;
    private ModuleHelper moduleHelper;

    public ImplUsageProvider(ReferenceSearchHelper searchHelper, ModuleHelper moduleHelper) {
        this.searchHelper = searchHelper;
        this.moduleHelper = moduleHelper;
    }

    public ImplUsageProvider() {
    }

    public boolean isImplicitUsage(PsiElement element) {
        if (!moduleHelper.isGaugeModule(element)) return false;
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
        return searchHelper.getPsiElements(collector, element).size() > 0;
    }

    public boolean isImplicitRead(final PsiElement element) {
        return false;
    }

    public boolean isImplicitWrite(final PsiElement element) {
        return false;
    }
}
