package com.thoughtworks.gauge.inspection;

import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.GlobalInspectionContext;
import com.intellij.codeInspection.GlobalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptionsProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GlobalInspectionProvider extends GlobalInspectionTool {
    @Override
    public void runInspection(@NotNull AnalysisScope scope, @NotNull InspectionManager manager, @NotNull GlobalInspectionContext globalContext, @NotNull ProblemDescriptionsProcessor processor) {
        GaugeErrors.init();
        Module[] modules = ModuleManager.getInstance(globalContext.getProject()).getModules();
        for (Module module : modules) {
            File dir = GaugeUtil.moduleDir(module);
            GaugeErrors.add(dir.getAbsolutePath(), GaugeInspectionHelper.getErrors(dir));
        }
    }
}
