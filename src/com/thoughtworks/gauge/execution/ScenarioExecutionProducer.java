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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.psi.impl.SpecScenarioImpl;
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.util.GaugeUtil.isSpecFile;

public class ScenarioExecutionProducer extends RunConfigurationProducer {
    private static final Logger LOG = Logger.getInstance("#com.thoughtworks.gauge.execution.ScenarioExecutionProducer");
    private final int NO_SCENARIOS = -1;
    private final int NON_SCENARIO_CONTEXT = -2;

    public ScenarioExecutionProducer() {
        super(new GaugeRunTaskConfigurationType());
    }

    protected ScenarioExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        VirtualFile[] selectedFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(context.getDataContext());
        if (selectedFiles == null || selectedFiles.length > 1) {
            return false;
        }
        Module module = GaugeUtil.moduleForPsiElement(context.getPsiLocation());
        if (module == null) {
            return false;
        }
        if (context.getPsiLocation() == null || !(isSpecFile(context.getPsiLocation().getContainingFile())) || context.getPsiLocation().getContainingFile().getVirtualFile() == null) {
            return false;
        }
        try {
            String name = context.getPsiLocation().getContainingFile().getVirtualFile().getCanonicalPath();
            int scenarioIdentifier = getScenarioIdentifier(context, context.getPsiLocation().getContainingFile());
            if (scenarioIdentifier == NO_SCENARIOS || scenarioIdentifier == NON_SCENARIO_CONTEXT) {
                return false;
            } else {
                String scenarioName = getScenarioName(context);
                configuration.setName(scenarioName);
                ((GaugeRunConfiguration) configuration).setSpecsToExecute(name + Constants.SPEC_SCENARIO_DELIMITER + scenarioIdentifier);
            }
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
        if (location == null || location.getVirtualFile() == null || context.getPsiLocation() == null) return false;
        String specsToExecute = ((GaugeRunConfiguration) configuration).getSpecsToExecute();
        int identifier = getScenarioIdentifier(context, context.getPsiLocation().getContainingFile());
        return specsToExecute != null && specsToExecute.equals(location.getVirtualFile().getPath() + Constants.SPEC_SCENARIO_DELIMITER + identifier);
    }

    private String getScenarioName(ConfigurationContext context) {
        PsiElement selectedElement = context.getPsiLocation();
        String scenarioName;

        if (selectedElement == null) return null;
        if (selectedElement.getClass().equals(SpecScenarioImpl.class)) {
            scenarioName = selectedElement.getText();
        } else {
            String text = getScenarioHeading(selectedElement).trim();
            if (text.equals("*")) {
                scenarioName = selectedElement.getParent().getParent().getNode().getFirstChildNode().getText();
            } else scenarioName = text;
        }
        if (scenarioName.startsWith("##"))
            scenarioName = scenarioName.replaceFirst("##", "");
        scenarioName = scenarioName.trim();
        return scenarioName.contains("\n") ? scenarioName.substring(0, scenarioName.indexOf("\n")) : scenarioName;
    }

    private int getScenarioIdentifier(ConfigurationContext context, PsiFile file) {
        int count = NO_SCENARIOS;
        PsiElement selectedElement = context.getPsiLocation();
        if (selectedElement == null) return NON_SCENARIO_CONTEXT;
        String scenarioHeading = (!selectedElement.getClass().equals(SpecScenarioImpl.class)) ? getScenarioHeading(selectedElement) : selectedElement.getText();
        if (scenarioHeading.equals(""))
            return getNumberOfScenarios(file) == 0 ? NO_SCENARIOS : NON_SCENARIO_CONTEXT;
        for (PsiElement psiElement : file.getChildren()) {
            if (psiElement.getClass().equals(SpecScenarioImpl.class)) {
                count++;
                if (psiElement.getNode().getFirstChildNode().getText().equals(scenarioHeading)) {
                    return StringUtil.offsetToLineNumber(psiElement.getContainingFile().getText(), psiElement.getTextOffset()) + 1;
                }
            }
        }
        return count == NO_SCENARIOS ? NO_SCENARIOS : NON_SCENARIO_CONTEXT;
    }

    private int getNumberOfScenarios(PsiFile file) {
        int count = 0;
        for (PsiElement psiElement : file.getChildren())
            if (psiElement.getClass().equals(SpecScenarioImpl.class))
                count++;
        return count;
    }

    private String getScenarioHeading(PsiElement selectedElement) {
        if (selectedElement == null) return "";
        if (selectedElement.getClass().equals(SpecScenarioImpl.class))
            return selectedElement.getNode().getFirstChildNode().getText();
        if (selectedElement.getClass().equals(SpecFile.class))
            return "";
        return getScenarioHeading(selectedElement.getParent());
    }
}
