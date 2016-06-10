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
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.core.GaugeVersion;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.psi.impl.SpecScenarioImpl;
import com.thoughtworks.gauge.util.GaugeUtil;

import static com.thoughtworks.gauge.util.GaugeUtil.isSpecFile;

public class ScenarioExecutionProducer extends GaugeExecutionProducer {
    private final int NO_SCENARIOS = -1;
    private final int NON_SCENARIO_CONTEXT = -2;
    private static final String VERSION = "0.4.0";

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
                ((GaugeRunConfiguration) configuration).setSpecsToExecute(name + ":" + scenarioIdentifier);
            }
            ((GaugeRunConfiguration) configuration).setModule(module);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
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
        if (scenarioName.contains("\n")) {
            return scenarioName.substring(0, scenarioName.indexOf("\n"));
        } else {
            return scenarioName;
        }
    }

    private int getScenarioIdentifier(ConfigurationContext context, PsiFile file) {
        int count = NO_SCENARIOS;
        PsiElement selectedElement = context.getPsiLocation();
        if (selectedElement == null) return NON_SCENARIO_CONTEXT;
        String scenarioHeading = (!selectedElement.getClass().equals(SpecScenarioImpl.class)) ? getScenarioHeading(selectedElement) : selectedElement.getText();
        if (scenarioHeading.equals("")) {
            if (getNumberOfScenarios(file) == 0) {
                return NO_SCENARIOS;
            }
            return NON_SCENARIO_CONTEXT;
        }
        for (PsiElement psiElement : file.getChildren()) {
            if (psiElement.getClass().equals(SpecScenarioImpl.class)) {
                count++;
                if (psiElement.getNode().getFirstChildNode().getText().equals(scenarioHeading)) {
                    return GaugeVersion.isGreaterThan(VERSION) ? StringUtil.offsetToLineNumber(psiElement.getContainingFile().getText(), psiElement.getTextOffset()) + 1 : count;
                }
            }
        }
        return count == NO_SCENARIOS ? NO_SCENARIOS : NON_SCENARIO_CONTEXT;
    }

    private int getNumberOfScenarios(PsiFile file) {
        int count = 0;
        for (PsiElement psiElement : file.getChildren()) {
            if (psiElement.getClass().equals(SpecScenarioImpl.class)) {
                count++;
            }
        }
        return count;
    }

    private String getScenarioHeading(PsiElement selectedElement) {
        if (selectedElement.getClass().equals(SpecScenarioImpl.class)) {
            return selectedElement.getNode().getFirstChildNode().getText();
        }
        if (selectedElement.getClass().equals(SpecFile.class)) {
            return "";
        }
        return getScenarioHeading(selectedElement.getParent());
    }
}
