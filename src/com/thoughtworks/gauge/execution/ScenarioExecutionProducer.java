package com.thoughtworks.gauge.execution;

import com.intellij.execution.actions.ConfigurationContext;
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
import com.thoughtworks.gauge.language.psi.impl.SpecTableImpl;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;

public class ScenarioExecutionProducer extends GaugeExecutionProducer {
    public ScenarioExecutionProducer() {
        super();
    }
    protected ScenarioExecutionProducer(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }
    public String SPEC_FILE = "Specification File";
    public String SPEC_DETAIL = SpecTokenTypes.SPEC_DETAIL.toString();
    public String SPEC_STEP = "Specification.STEP";

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
        if (context.getPsiLocation() == null || !(context.getPsiLocation().getContainingFile() instanceof SpecFile) || context.getPsiLocation().getContainingFile().getVirtualFile() == null)
            return false;
        if(context.getPsiLocation().getParent().toString().equals(SPEC_FILE)){
            return false;
        }
        PsiElement table = getTable(context.getPsiLocation());
        if(table!=null){
            if (table.getParent().getParent().getNode().getElementType().toString().equals(SPEC_DETAIL) && table.getParent().getNode().getElementType().toString().equals(SPEC_STEP)){
                return false;
            }
        }
        if(context.getPsiLocation().getParent().getNode().getElementType().toString().equals(SPEC_DETAIL) || isContextStep(context)){
            return false;
        }

        if(isContextTable(context.getPsiLocation())){
            return false;
        }

        try {
            Project project = context.getPsiLocation().getContainingFile().getProject();
            Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(context.getPsiLocation().getContainingFile().getVirtualFile());
            String name = context.getPsiLocation().getContainingFile().getVirtualFile().getCanonicalPath();
            Integer scenarioIndex = getScenarioIndex(context, context.getPsiLocation().getContainingFile());
            String scenarioName = getScenarioName(context);

            configuration.setName(scenarioName);
            ((GaugeRunConfiguration) configuration).setSpecsToExecute(name + ":" + scenarioIndex);
            ((GaugeRunConfiguration) configuration).setModule(module);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private boolean isContextTable(PsiElement selectedElement) {
        PsiElement tableElement = getTable(selectedElement);
        if(tableElement !=null)
            if(tableElement.getParent().getNode().getElementType().toString().equals(SPEC_DETAIL))
                return true;
        return false;
    }

    private PsiElement getTable(PsiElement selectedElement) {
        if(selectedElement.getClass().equals(SpecTableImpl.class)) return selectedElement;
        if(selectedElement.getParent()==null) return null;
        return getTable(selectedElement.getParent());
    }


    private boolean isContextStep(ConfigurationContext context) {
        if(context.getPsiLocation().getParent().getParent().getNode().getElementType().toString().equals(SPEC_DETAIL)){
            if(context.getPsiLocation().getParent().getNode().getElementType().toString().equals(SPEC_STEP)){
                return true;
            }
        }
        return false;
    }

    private String getScenarioName(ConfigurationContext context) {
        PsiElement selectedElement = context.getPsiLocation();
        String scenarioName = null;

        if(selectedElement== null) return null;
        if(selectedElement.getClass().equals(SpecScenarioImpl.class)){
            scenarioName= selectedElement.getText();
        }else {
            String text = getScenarioHeading(selectedElement).trim();
            if(text.equals("*")) {
                scenarioName =  selectedElement.getParent().getParent().getNode().getFirstChildNode().getText();
            } else scenarioName = text;
        }
        if(scenarioName.startsWith("##"))
            scenarioName = scenarioName.replaceFirst("##","");
        scenarioName = scenarioName.trim();
        if(scenarioName.contains("\n"))
            return scenarioName.substring(0,scenarioName.indexOf("\n"));
        else
            return scenarioName;
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
