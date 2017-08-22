package com.thoughtworks.gauge.execution;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.exception.GaugeNotFoundException;
import com.thoughtworks.gauge.settings.GaugeSettingsModel;
import com.thoughtworks.gauge.util.GaugeUtil;

import java.util.Map;

public class GaugeCommandLine {
    public static GeneralCommandLine getInstance(Module module, Project project) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        try {
            GaugeSettingsModel settings = GaugeUtil.getGaugeSettings();
            commandLine.setExePath(settings.getGaugePath());
            Map<String, String> environment = commandLine.getEnvironment();
            environment.put(Constants.GAUGE_HOME, settings.getHomePath());
        } catch (GaugeNotFoundException e) {
            commandLine.setExePath(Constants.GAUGE);
        } finally {
            commandLine.setWorkDirectory(project.getBasePath());
            if (module != null)
                commandLine.setWorkDirectory(GaugeUtil.moduleDir(module));
            return commandLine;
        }
    }
}
