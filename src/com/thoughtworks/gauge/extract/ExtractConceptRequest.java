package com.thoughtworks.gauge.extract;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.SpecTable;
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

    public ExtractConceptRequest(List<Api.step> steps, String fileName, String concept, boolean refactorOtherUsages, Api.textInfo textInfo) {
        this.steps = steps;
        this.fileName = fileName;
        this.concept = Api.step.newBuilder().setName(concept).build();
        this.refactorOtherUsages = refactorOtherUsages;
        this.textInfo = textInfo;
    }

    public ExtractConceptRequest(String fileName, String concept, boolean refactorOtherUsages, Api.textInfo textInfo) {
        this.fileName = fileName;
        this.concept = Api.step.newBuilder().setName(concept).build();
        this.refactorOtherUsages = refactorOtherUsages;
        this.textInfo = textInfo;
    }

    public void convertToSteps(List<SpecStepImpl> specSteps, Map<String,String> tableMap){
        this.steps = new ArrayList<Api.step>();
        for (SpecStepImpl specStep : specSteps) {
            String text = specStep.getText().trim().substring(2).trim();
            Api.step stepToAdd = Api.step.newBuilder().setName(text).build();
            if (specStep.getInlineTable() != null) {
                String tableText = specStep.getInlineTable().getText().trim();
                Api.step.Builder builder = Api.step.newBuilder().setName(text.replace(tableText, "").trim()).setTable(tableText);
                String tableAsParameter = getTableName(specStep.getInlineTable(), concept.getName(), tableMap);
                if (tableAsParameter != null) builder = builder.setParamTableName(tableAsParameter);
                stepToAdd = builder.build();
            }
            steps.add(stepToAdd);
        }
    }

    private String getTableName(SpecTable inlineTable, String concept, Map<String,String> tableMap) {
        Pattern pattern = Pattern.compile("<(.*?)>");
        final Matcher matcher = pattern.matcher(concept);
        while (matcher.find()) {
            if (tableMap.get(matcher.group(1)) != null && tableMap.get(matcher.group(1)).equals(inlineTable.getText().trim()))
                return matcher.group(1);
        }
        return null;
    }

    public Api.ExtractConceptResponse makeExtractConceptRequest(PsiElement element) {
        final Module module = ModuleUtil.findModuleForPsiElement(element);
        GaugeService gaugeService = Gauge.getGaugeService(module);
        try {
            return gaugeService.getGaugeConnection().sendGetExtractConceptRequest(steps, concept, refactorOtherUsages, fileName, textInfo);
        } catch (Exception e) {
            return Api.ExtractConceptResponse.newBuilder().setIsSuccess(false).setError("Unable to make request").build();
        }
    }

}
