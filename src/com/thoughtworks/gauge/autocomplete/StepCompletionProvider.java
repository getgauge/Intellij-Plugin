package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.GaugeConnection;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.SpecArg;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StepCompletionProvider extends CompletionProvider<CompletionParameters> {

    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        resultSet.stopHere();
        resultSet = resultSet.withPrefixMatcher(new PlainPrefixMatcher(""));
        for (String step : getStepsInProject(parameters.getOriginalFile().getProject())) {
            LookupElementBuilder element = LookupElementBuilder.create(step);
            element = element.withInsertHandler(new InsertHandler<LookupElement>() {
                @Override
                public void handleInsert(InsertionContext context, LookupElement item) {
                    PsiElement stepElement = context.getFile().findElementAt(context.getStartOffset()).getParent();
                    final TemplateBuilder builder = TemplateBuilderFactory.getInstance().createTemplateBuilder(stepElement);
                    List<SpecArg> args = PsiTreeUtil.getChildrenOfTypeAsList(stepElement, SpecArg.class);
                    for (SpecArg arg : args) {
                        builder.replaceRange(getRangeWithinElement(arg), arg.getText());
                    }
                    builder.run(context.getEditor(), false);
                }
            });
            resultSet.addElement(element);
        }
    }

    private TextRange getRangeWithinElement(SpecArg arg) {
        return new TextRange(arg.getStartOffsetInParent(), arg.getStartOffsetInParent() + arg.getTextLength());
    }

    private List<String> getStepsInProject(Project project) {
        List<String> steps = new ArrayList<String>();
        try {
            GaugeService gaugeService = Gauge.getGaugeService(project);
            GaugeConnection gaugeConnection = gaugeService.getGaugeConnection();
            if (gaugeConnection != null) {
                List<StepValue> stepValues = gaugeConnection.fetchAllSteps();
                for (StepValue stepValue : stepValues) {
                    steps.add(stepValue.getStepAnnotationText());
                }
                return steps;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return steps;
    }
}