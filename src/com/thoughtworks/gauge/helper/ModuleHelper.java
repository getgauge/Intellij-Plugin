package com.thoughtworks.gauge.helper;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.util.GaugeUtil;

public class ModuleHelper {
    public boolean isGaugeModule(PsiElement element) {
        Module module = GaugeUtil.moduleForPsiElement(element);
        return module != null && GaugeUtil.isGaugeModule(module);
    }

    public boolean isGaugeModule(Module module) {
        return GaugeUtil.isGaugeModule(module);
    }

    public Module getModule(PsiElement step) {
        return GaugeUtil.moduleForPsiElement(step);
    }

    public Module getModule(VirtualFile file, Project project) {
        return ModuleUtil.findModuleForFile(file, project);
    }

    public boolean isGaugeModule(VirtualFile file, Project project) {
        return isGaugeModule(ModuleUtil.findModuleForFile(file, project));
    }
}