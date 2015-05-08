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

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import static com.thoughtworks.gauge.util.GaugeUtil.isSpecFile;

public class GaugeExecutionProducer extends RunConfigurationProducer {


    public GaugeExecutionProducer() {
        super(new GaugeRunTaskConfigurationType());
    }

    protected GaugeExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        VirtualFile[] selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(context.getDataContext());
        if (selectedFiles == null || selectedFiles.length>1)
            return false;

        if (context.getPsiLocation() == null) {
            return false;
        }

        PsiFile file = context.getPsiLocation().getContainingFile();
        if (!isSpecFile(file)) {
            return false;
        }

        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                return false;
            }

            Project project = file.getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(virtualFile);
            String name = file.getVirtualFile().getCanonicalPath();

            configuration.setName(file.getName());
            ((GaugeRunConfiguration) configuration).setSpecsToExecute(name);
            ((GaugeRunConfiguration) configuration).setModule(module);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;

    }


    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        if (configuration.getType() != getConfigurationType())
            return false;
        final Location location = context.getLocation();
        if (location == null || location.getVirtualFile() == null) {
            return false;
        }

        final String specsToExecute = ((GaugeRunConfiguration) configuration).getSpecsToExecute();
        if(specsToExecute == null) {
            return false;
        }
        return (specsToExecute.contains(location.getVirtualFile().getName()));
    }
}
