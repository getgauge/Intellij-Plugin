package com.thoughtworks.gauge.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.language.SpecFileType;

import java.io.File;
import java.util.ArrayList;


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
        if (selectedFiles == null)
            return false;
        if (selectedFiles.length == 1) {
            if (!selectedFiles[0].isDirectory()) {
                return false;
            } else if (selectedFiles[0].equals(configurationContext.getProject().getBaseDir())) {
                configuration.setName(DEFAULT_CONFIGURATION_NAME);
                ((GaugeRunConfiguration) configuration).setSpecsToExecute(projectSpecsDirectory(configurationContext.getProject()));
                return true;
            }
        }
        
        ArrayList<String> specsToExecute = new ArrayList<String>();
        for (VirtualFile selectedFile : selectedFiles) {
            if (selectedFile.getFileType().getClass().equals(SpecFileType.class)) {
                specsToExecute.add(selectedFile.getPath());
            } else if (selectedFile.isDirectory() && shouldAddDirToExecute(selectedFile)) {
                specsToExecute.add(selectedFile.getPath());
            }
        }
        if (specsToExecute.size() == 0) {
            return false;
        }
        configuration.setName(DEFAULT_CONFIGURATION_NAME);
        ((GaugeRunConfiguration) configuration).setSpecsArrayToExecute(specsToExecute);
        return true;
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
            if (file.getFileType().getClass().equals(SpecFileType.class))
                numberOfSpecs++;
        }
        return numberOfSpecs;
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext configurationContext) {
        return configuration.getType() == getConfigurationType();
    }
}
