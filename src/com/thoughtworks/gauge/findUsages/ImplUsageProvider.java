package com.thoughtworks.gauge.findUsages;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;

public class ImplUsageProvider implements ImplicitUsageProvider {

    public boolean isImplicitUsage(PsiElement element) {
        if (element instanceof PsiClassImpl) {
            for (PsiMethod psiMethod : ((PsiClassImpl) element).getMethods())
                if (StepUtil.getGaugeStepAnnotationValues(psiMethod).size() > 0) return true;
            return false;
        }
        boolean isGaugeElement = GaugeUtil.isGaugeElement(element);
        if (!isGaugeElement) return false;
        StepCollector collector = new StepCollector(element.getProject());
        collector.collect();
        return ReferenceSearch.getPsiElements(collector, element).size() > 0;
    }

    public boolean isImplicitRead(final PsiElement element) {
        return false;
    }

    public boolean isImplicitWrite(final PsiElement element) {
        return false;
    }
}
