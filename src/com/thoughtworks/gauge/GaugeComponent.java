package com.thoughtworks.gauge;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.core.Gauge;

import java.util.Arrays;

public class GaugeComponent implements ProjectComponent {

    private Project project;

    public GaugeComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectClosed() {
        Arrays.stream(ModuleManager.getInstance(project).getModules()).forEach(Gauge::disposeComponent);
    }
}
