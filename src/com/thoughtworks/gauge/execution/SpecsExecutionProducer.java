package com.thoughtworks.gauge.execution;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.gauge.language.SpecFileType;

import java.util.ArrayList;


public class SpecsExecutionProducer extends RunConfigurationProducer {
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
        if (selectedFiles.length == 1 && !selectedFiles[0].isDirectory())
            return false;
        if (selectedFiles.length != 1 || !selectedFiles[0].isDirectory() || !selectedFiles[0].equals(configurationContext.getProject().getBaseDir())) {
            if (selectedFiles.length == 1 && selectedFiles[0].isDirectory()) {
                if (numberOfSpecFiles(selectedFiles[0]) == 0)
                    return false;
            }
        }

        ArrayList<String> specsToExecute = new ArrayList<String>();
        for (VirtualFile selectedFile : selectedFiles) {
            if (selectedFile.getFileType().getClass().equals(SpecFileType.class) || selectedFile.isDirectory()) {
                specsToExecute.add(selectedFile.getPath());
            }
        }
        ((GaugeRunConfiguration) configuration).setSpecsArrayToExecute(specsToExecute);
        configuration.setName("Specifications");
        return true;
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
