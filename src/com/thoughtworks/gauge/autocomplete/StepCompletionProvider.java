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

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.ConceptInfo;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.ConceptArg;
import com.thoughtworks.gauge.language.psi.SpecArg;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtworks.gauge.autocomplete.StepCompletionContributor.getPrefix;

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
            element = element.withInsertHandler(new InsertHandler<LookupElement>() {
                @Override
                public void handleInsert(InsertionContext context, LookupElement item) {
                    PsiElement stepElement = context.getFile().findElementAt(context.getStartOffset()).getParent();
                    TemplateBuilder templateBuilder = getTemplateBuilder(stepElement, prefix);
                    templateBuilder.run(context.getEditor(), false);
                }
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
            String replacementText = i + 1 > filledParams.size() ? stepParam.getText() :filledParams.get(i);
            templateBuilder.replaceElement(stepParam, replacementText);
        }
        return templateBuilder;
    }

    private List<String> getFilledParams(String prefix) {
        Pattern filledParamPattern = Pattern.compile("\"[\\w ]+\"");
        Matcher matcher = filledParamPattern.matcher(prefix);
        List<String> filledParams = new ArrayList<String>();
        while (matcher.find()) {
            filledParams.add(matcher.group());
        }
        return filledParams;
    }

    private class Type {
        private String text;
        private String type;

        public Type(String text, String type) {
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

    private List<Type> getStepsInModule(Module module) {
        List<Type> steps = new ArrayList<Type>();
        try {
            GaugeService gaugeService = Gauge.getGaugeService(module, true);
            if (gaugeService == null)
                return steps;
            GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
            if (gaugeConnection != null) {
                List<StepValue> stepValues = gaugeConnection.fetchAllSteps();
                for (StepValue stepValue : stepValues) {
                    steps.add(new Type(stepValue.getStepAnnotationText(), STEP));
                }
                for (ConceptInfo conceptInfo : gaugeConnection.fetchAllConcepts()) {
                    steps.add(new Type(conceptInfo.getStepValue().getStepAnnotationText(), CONCEPT));
                }
                return steps;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return steps;
    }
}