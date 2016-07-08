package com.thoughtworks.gauge.findUsages;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;
import com.thoughtworks.gauge.findUsages.helper.*;

public class ImplUsageProvider implements ImplicitUsageProvider {
    private static final ReferenceHelper helper = new ReferenceHelper();

    public boolean isImplicitUsage(PsiElement element) {
        Module module = GaugeUtil.moduleForPsiElement(element);
        if (module == null || !GaugeUtil.isGaugeModule(module))
            return false;
        if (element instanceof PsiClassImpl) {
            for (PsiMethod psiMethod : ((PsiClassImpl) element).getMethods())
                if (StepUtil.getGaugeStepAnnotationValues(psiMethod).size() > 0) return true;
            return false;
        }
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
