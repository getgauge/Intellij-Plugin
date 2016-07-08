package com.thoughtworks.gauge.extract;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.ConceptTable;
import com.thoughtworks.gauge.language.psi.SpecTable;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import gauge.messages.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractConceptRequest {
    private List<Api.step> steps;
    private final String fileName;
    private final Api.step concept;
    private final boolean refactorOtherUsages;
    private final Api.textInfo textInfo;

    public ExtractConceptRequest(String fileName, String concept, boolean refactorOtherUsages, Api.textInfo textInfo) {
        this.fileName = fileName;
        this.concept = Api.step.newBuilder().setName(concept).build();
        this.refactorOtherUsages = refactorOtherUsages;
        this.textInfo = textInfo;
    }

    public void convertToSteps(List<PsiElement> specSteps, Map<String, String> tableMap) {
        this.steps = new ArrayList<>();
        for (PsiElement specStep : specSteps) {
            String text = specStep.getText().trim().substring(2).trim();
            Api.step stepToAdd = Api.step.newBuilder().setName(text).build();
            if (specStep.getClass().equals(SpecStepImpl.class))
                stepToAdd = formatTable(tableMap, (SpecStepImpl) specStep, text, stepToAdd);
            else
                stepToAdd = formatTable(tableMap, (ConceptStepImpl) specStep, text, stepToAdd);
            steps.add(stepToAdd);
        }
    }

    private Api.step formatTable(Map<String, String> tableMap, SpecStepImpl specStep, String text, Api.step stepToAdd) {
        SpecTable table = specStep.getInlineTable();
        if (table != null) stepToAdd = getStep(tableMap, text, table.getText().trim());
        return stepToAdd;
    }

    private Api.step formatTable(Map<String, String> tableMap, ConceptStepImpl conceptStep, String text, Api.step stepToAdd) {
        ConceptTable table = conceptStep.getTable();
        if (table != null) stepToAdd = getStep(tableMap, text, table.getText().trim());
        return stepToAdd;
    }

    private Api.step getStep(Map<String, String> tableMap, String text, String tableText) {
        Api.step stepToAdd;
        Api.step.Builder builder = Api.step.newBuilder().setName(text.replace(tableText, "").trim()).setTable(tableText);
        String tableAsParameter = getTableName(concept.getName(), tableMap, tableText);
        if (tableAsParameter != null) builder = builder.setParamTableName(tableAsParameter);
        stepToAdd = builder.build();
        return stepToAdd;
    }

    private String getTableName(String concept, Map<String, String> tableMap, String tableText) {
        Pattern pattern = Pattern.compile("<(.*?)>");
        final Matcher matcher = pattern.matcher(concept);
        while (matcher.find()) {
            if (tableMap.get(matcher.group(1)) != null && tableMap.get(matcher.group(1)).equals(tableText))
                return matcher.group(1);
        }
        return null;
    }

    public Api.ExtractConceptResponse makeExtractConceptRequest(PsiElement element) {
        GaugeService gaugeService = Gauge.getGaugeService(ModuleUtil.findModuleForPsiElement(element), true);
        String message = "Cannot connect to gauge service.";
        if (gaugeService != null)
            try {
                return gaugeService.getGaugeConnection().sendGetExtractConceptRequest(steps, concept, refactorOtherUsages, fileName, textInfo);
            } catch (Exception ignored) {
                message = "Something went wrong during extract concept request.";
            }
        return Api.ExtractConceptResponse.newBuilder().setIsSuccess(false).setError(message).build();
    }
}