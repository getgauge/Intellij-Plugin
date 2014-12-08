package com.thoughtworks.gauge.execution;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.language.SpecFile;
import com.thoughtworks.gauge.language.psi.impl.SpecScenarioImpl;

public class ScenarioExecutionProducer extends GaugeExecutionProducer {
    public ScenarioExecutionProducer() {
        super();
    }
    protected ScenarioExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        if (context.getPsiLocation() == null || !(context.getPsiLocation().getContainingFile() instanceof SpecFile) || context.getPsiLocation().getContainingFile().getVirtualFile() == null)
            return false;
        try {
            Project project = context.getPsiLocation().getContainingFile().getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(context.getPsiLocation().getContainingFile().getVirtualFile());
            String name = context.getPsiLocation().getContainingFile().getVirtualFile().getCanonicalPath();
            Integer scenarioIndex = getScenarioIndex(context, context.getPsiLocation().getContainingFile());

            configuration.setName("Scenario: " + scenarioIndex);
            ((GaugeRunConfiguration) configuration).setSpecsToExecute(name + ":" + scenarioIndex);
            ((GaugeRunConfiguration) configuration).setModule(module);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private Integer getScenarioIndex(ConfigurationContext context, PsiFile file) {
        Integer count = -1;
        PsiElement selectedElement = context.getPsiLocation();
        if (selectedElement == null)    return 0;
        String scenarioHeading = (!selectedElement.getClass().equals(SpecScenarioImpl.class)) ? getScenarioHeading(selectedElement) : selectedElement.getText();
        for (PsiElement psiElement : file.getChildren())
            if (psiElement.getClass().equals(SpecScenarioImpl.class)) {
                count++;
                if (psiElement.getNode().getFirstChildNode().getText().equals(scenarioHeading)) return count;
            }
        return 0;
    }

    private String getScenarioHeading(PsiElement selectedElement) {
        if (selectedElement.getClass().equals(SpecScenarioImpl.class))  return selectedElement.getNode().getFirstChildNode().getText();
        if (selectedElement.getParent() == null) return "";
        return getScenarioHeading(selectedElement.getParent());
    }
}
