// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.gauge.execution.GaugeRunConfiguration.SPEC_FILE_DELIMITER;
import static com.thoughtworks.gauge.util.GaugeUtil.isSpecFile;


public class SpecsExecutionProducer extends RunConfigurationProducer {

    public static final String DEFAULT_CONFIGURATION_NAME = "Specifications";
    public static final String SPECS_DIR = "specs";

    public SpecsExecutionProducer() {
        super(new GaugeRunTaskConfigurationType());
    }

    protected SpecsExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext configurationContext, Ref ref) {
        VirtualFile[] selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(configurationContext.getDataContext());
        Module module = configurationContext.getModule();
        if (selectedFiles == null || module == null)
            return false;
        if (selectedFiles.length == 1) {
            if (!selectedFiles[0].isDirectory()) {
                return false;
            } else if (selectedFiles[0].equals(configurationContext.getProject().getBaseDir())) {
                configuration.setName(DEFAULT_CONFIGURATION_NAME);
                ((GaugeRunConfiguration) configuration).setModule(module);
                ((GaugeRunConfiguration) configuration).setSpecsToExecute(projectSpecsDirectory(configurationContext.getProject()));
                return true;
            }
        }

        List<String> specsToExecute = getSpecs(selectedFiles);
        if (specsToExecute.size() == 0) {
            return false;
        }
        configuration.setName(DEFAULT_CONFIGURATION_NAME);
        ((GaugeRunConfiguration) configuration).setModule(module);
        ((GaugeRunConfiguration) configuration).setSpecsArrayToExecute(specsToExecute);
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration config, ConfigurationContext context) {
        if (!(config.getType() instanceof GaugeRunTaskConfigurationType)) return false;
        if (!(context.getPsiLocation() instanceof PsiDirectory) && !(context.getPsiLocation() instanceof PsiFile))
            return false;
        VirtualFile[] selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(context.getDataContext());
        if (selectedFiles == null) return false;
        String specs = ((GaugeRunConfiguration) config).getSpecsToExecute();
        return StringUtil.join(getSpecs(selectedFiles), SPEC_FILE_DELIMITER).equals(specs);
    }

    @NotNull
    private List<String> getSpecs(VirtualFile[] selectedFiles) {
        List<String> specsToExecute = new ArrayList<>();
        for (VirtualFile selectedFile : selectedFiles) {
            if (isSpecFile(selectedFile)) {
                specsToExecute.add(selectedFile.getPath());
            } else if (selectedFile.isDirectory() && shouldAddDirToExecute(selectedFile)) {
                specsToExecute.add(selectedFile.getPath());
            }
        }
        return specsToExecute;
    }

    private boolean shouldAddDirToExecute(VirtualFile selectedFile) {
        return numberOfSpecFiles(selectedFile) != 0;
    }

    private String projectSpecsDirectory(Project project) {
        return new File(project.getBaseDir().getPath(), SPECS_DIR).getAbsolutePath();
    }

    private int numberOfSpecFiles(VirtualFile directory) {
        int numberOfSpecs = 0;
        for (VirtualFile file : directory.getChildren()) {
            if (isSpecFile(file))
                numberOfSpecs++;
        }
        return numberOfSpecs;
    }
}
