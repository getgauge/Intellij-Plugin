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

package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.ConceptArg;
import com.thoughtworks.gauge.language.psi.SpecArg;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtworks.gauge.autocomplete.StepCompletionContributor.getPrefix;
import static com.thoughtworks.gauge.language.psi.SpecPsiImplUtil.getStepValueFor;
import static com.thoughtworks.gauge.util.StepUtil.getGaugeStepAnnotationValues;

public class StepCompletionProvider extends CompletionProvider<CompletionParameters> {

    public static final String STEP = "step";
    public static final String CONCEPT = "concept";
    private boolean isConcept = false;

    public void setConcept(boolean isConcept) {
        this.isConcept = isConcept;
    }

    @Override
    public void addCompletions(@NotNull final CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        resultSet.stopHere();
        final String prefix = getPrefix(parameters);

        resultSet = resultSet.withPrefixMatcher(new GaugePrefixMatcher(prefix));
        Module moduleForPsiElement = GaugeUtil.moduleForPsiElement(parameters.getPosition());
        if (moduleForPsiElement == null) {
            return;
        }
        for (Type item : getStepsInModule(moduleForPsiElement)) {
            LookupElementBuilder element = LookupElementBuilder.create(item.getText()).withTypeText(item.getType(), true);
            element = element.withInsertHandler((context1, item1) -> {
                if (context1.getCompletionChar() == '\t') {
                    context1.getDocument().insertString(context1.getEditor().getCaretModel().getOffset(), "\n");
                    PsiDocumentManager.getInstance(context1.getProject()).commitDocument(context1.getDocument());
                }
                PsiElement stepElement = context1.getFile().findElementAt(context1.getStartOffset()).getParent();
                TemplateBuilder templateBuilder = getTemplateBuilder(stepElement, prefix);
                templateBuilder.run(context1.getEditor(), false);
            });
            resultSet.addElement(element);
        }
    }

    private TemplateBuilder getTemplateBuilder(PsiElement stepElement, String prefix) {
        final TemplateBuilder templateBuilder = TemplateBuilderFactory.getInstance().createTemplateBuilder(stepElement);

        Class<? extends PsiElement> stepParamsClass = isConcept ? ConceptArg.class : SpecArg.class;
        List<? extends PsiElement> stepParams = PsiTreeUtil.getChildrenOfTypeAsList(stepElement, stepParamsClass);

        List<String> filledParams = getFilledParams(prefix);
        for (int i = 0; i < stepParams.size(); i++) {
            PsiElement stepParam = stepParams.get(i);
            String replacementText = i + 1 > filledParams.size() ? stepParam.getText() : filledParams.get(i);
            templateBuilder.replaceElement(stepParam, replacementText);
        }
        return templateBuilder;
    }

    private List<String> getFilledParams(String prefix) {
        Pattern filledParamPattern = Pattern.compile("\"[\\w ]+\"");
        Matcher matcher = filledParamPattern.matcher(prefix);
        List<String> filledParams = new ArrayList<>();
        while (matcher.find()) {
            filledParams.add(matcher.group());
        }
        return filledParams;
    }

    private class Type {
        private String text;
        private String type;

        Type(String text, String type) {
            this.text = text;
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }
    }

    private Collection<Type> getStepsInModule(Module module) {
        Map<String, Type> steps = getImplementedSteps(module);
        try {
            GaugeService gaugeService = Gauge.getGaugeService(module, true);
            if (gaugeService == null) return steps.values();
            GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
            if (gaugeConnection != null) {
                gaugeConnection.fetchAllSteps().forEach(s -> addStep(steps, s, STEP));
                gaugeConnection.fetchAllConcepts().forEach(concept -> addStep(steps, concept.getStepValue(), CONCEPT));
            }
        } catch (IOException ignored) {
        }
        return steps.values();
    }

    @NotNull
    private Map<String, Type> getImplementedSteps(Module module) {
        Map<String, Type> steps = new HashMap<>();
        Collection<PsiMethod> methods = StepUtil.getStepMethods(module);
        methods.forEach(m -> {
            getGaugeStepAnnotationValues(m).forEach(s -> {
                steps.put(getStepValueFor(module, m, s, false).getStepText(), new Type(s, STEP));
            });
        });
        return steps;
    }

    private void addStep(Map<String, Type> steps, StepValue stepValue, String entity) {
        if (stepValue.getStepAnnotationText().trim().isEmpty() || steps.containsKey(stepValue.getStepText())) return;
        steps.put(stepValue.getStepText(), new Type(stepValue.getStepAnnotationText(), entity));
    }
}