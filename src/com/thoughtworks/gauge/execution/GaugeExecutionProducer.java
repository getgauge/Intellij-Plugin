/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.execution;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.psi.SpecScenario;
import org.apache.commons.lang.StringUtils;

import static com.thoughtworks.gauge.util.GaugeUtil.isSpecFile;

public class GaugeExecutionProducer extends RunConfigurationProducer {

    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.execution.GaugeExecutionProducer");
    public GaugeExecutionProducer() {
        super(new GaugeRunTaskConfigurationType());
    }

    protected GaugeExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        VirtualFile[] selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(context.getDataContext());
        if (selectedFiles == null || selectedFiles.length > 1) return false;
        Module module = context.getModule();
        if (context.getPsiLocation() == null || module == null) return false;
        PsiFile file = context.getPsiLocation().getContainingFile();
        if (!isSpecFile(file) || !isInSpecScope(context.getPsiLocation())) return false;
        try {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) return false;
            String name = virtualFile.getCanonicalPath();
            configuration.setName(file.getName());
            ((GaugeRunConfiguration) configuration).setSpecsToExecute(name);
            ((GaugeRunConfiguration) configuration).setModule(module);
            return true;
        } catch (Exception ex) {
            LOG.debug(ex);
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        if (!(configuration.getType() instanceof GaugeRunTaskConfigurationType)) return false;
        Location location = context.getLocation();
        PsiElement element = CommonDataKeys.PSI_ELEMENT.getData(context.getDataContext());
        if (location == null || location.getVirtualFile() == null || element == null) return false;
        if (!isInSpecScope(context.getPsiLocation())) return false;
        String specsToExecute = ((GaugeRunConfiguration) configuration).getSpecsToExecute();
        return specsToExecute != null && (specsToExecute.equals(location.getVirtualFile().getPath()));
    }

    private Boolean isInSpecScope(PsiElement element) {
        if (element == null) return false;
        if (element instanceof SpecFile) return true;
        if (element instanceof SpecScenario) return false;
        return isInSpecScope(element.getParent());
    }
}
