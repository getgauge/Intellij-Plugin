package com.thoughtworks.gauge.helper;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.util.GaugeUtil;

public class ModuleHelper {
    public boolean isGaugeModule(PsiElement element) {
        Module module = GaugeUtil.moduleForPsiElement(element);
        return module != null && GaugeUtil.isGaugeModule(module);
    }

    public Module getModule(SpecStep step) {
        return GaugeUtil.moduleForPsiElement(step);
    }
}