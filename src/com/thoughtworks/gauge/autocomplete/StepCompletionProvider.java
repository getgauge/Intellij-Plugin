package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.GaugeConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepCompletionProvider extends CompletionProvider<CompletionParameters> {

    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        resultSet = resultSet.withPrefixMatcher(new PlainPrefixMatcher(""));
        for (String step : getStepsInProject(parameters.getOriginalFile().getProject())) {
            LookupElementBuilder element = LookupElementBuilder.create(step);
            element = element.withInsertHandler(new InsertHandler<LookupElement>() {
                @Override
                public void handleInsert(InsertionContext context, LookupElement item) {
                    PsiElement stepElement = context.getFile().findElementAt(context.getStartOffset());
                    final TemplateBuilder builder = TemplateBuilderFactory.getInstance().createTemplateBuilder(stepElement);
                    Pattern pattern = Pattern.compile("\\{\\}");
                    if (stepElement != null) {
                        Matcher matcher = pattern.matcher(stepElement.getText());
                        while (matcher.find()) {
                            builder.replaceRange(new TextRange(matcher.start(), matcher.end()), "\"arg\"");
                        }
                        builder.run(context.getEditor(), false);
                    }
                }
            });
            resultSet.addElement(element);
        }
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