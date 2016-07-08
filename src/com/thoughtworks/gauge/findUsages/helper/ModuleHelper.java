package com.thoughtworks.gauge.findUsages.helper;

import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.util.GaugeUtil;

public class ModuleHelper {
    boolean isGaugeModule(PsiElement element) {
        Module module = GaugeUtil.moduleForPsiElement(element);
        return module != null && GaugeUtil.isGaugeModule(module);
    }
}