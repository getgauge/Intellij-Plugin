package com.thoughtworks.gauge.rename;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;

class CompileAction {
    void compile(Project project) {
        CompilerManager.getInstance(project).make((b, i, i1, compileContext) -> {
        });
    }
}