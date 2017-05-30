package com.thoughtworks.gauge.compiler;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.thoughtworks.gauge.inspection.GaugeError;
import com.thoughtworks.gauge.inspection.GaugeInspectionHelper;
import com.thoughtworks.gauge.util.GaugeUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class GaugeCompilerTask implements CompileTask{
    @Override
    public boolean execute(CompileContext context) {
        Module[] modules = ModuleManager.getInstance(context.getProject()).getModules();
        for (Module module : modules) {
            File dir = GaugeUtil.moduleDir(module);
            List<GaugeError> errors = GaugeInspectionHelper.getErrors(dir);
            for (GaugeError error : errors) {
                context.addMessage(CompilerMessageCategory.ERROR, error.getMessage(), Paths.get(error.getFileName()).toUri().toString(), error.getLineNumber(), -1);
            }
        }
        return true;
    }
}
