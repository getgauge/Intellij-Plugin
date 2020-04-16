/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

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
import com.thoughtworks.gauge.Constants;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.gauge.util.GaugeUtil.isSpecFile;


public class SpecsExecutionProducer extends RunConfigurationProducer {

    public static final String DEFAULT_CONFIGURATION_NAME = "Specifications";

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
            } else if (selectedFiles[0].getPath().equals(configurationContext.getProject().getBasePath())) {
                configuration.setName(DEFAULT_CONFIGURATION_NAME);
                ((GaugeRunConfiguration) configuration).setModule(module);
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
        return StringUtil.join(getSpecs(selectedFiles), Constants.SPEC_FILE_DELIMITER).equals(specs);
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

    private int numberOfSpecFiles(VirtualFile directory) {
        int numberOfSpecs = 0;
        for (VirtualFile file : directory.getChildren()) {
            if (isSpecFile(file))
                numberOfSpecs++;
        }
        return numberOfSpecs;
    }
}
