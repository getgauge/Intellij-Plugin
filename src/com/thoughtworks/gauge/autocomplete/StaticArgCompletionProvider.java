/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.thoughtworks.gauge.language.psi.SpecDetail;
import com.thoughtworks.gauge.language.psi.SpecScenario;
import com.thoughtworks.gauge.language.psi.SpecStaticArg;
import com.thoughtworks.gauge.language.psi.SpecStep;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thoughtworks.gauge.autocomplete.StepCompletionContributor.getPrefix;

public class StaticArgCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        String prefix = getPrefix(parameters);
        resultSet = resultSet.withPrefixMatcher(new PlainPrefixMatcher(prefix));
        PsiFile specFile = parameters.getOriginalFile();
        SpecDetail specDetail = PsiTreeUtil.getChildOfType(specFile, SpecDetail.class);
        List<SpecStep> stepsInFile = new ArrayList<>();
        addContextSteps(specDetail, stepsInFile);
        addStepsInScenarios(specFile, stepsInFile);

        Set<String> staticArgs = getArgsFromSteps(stepsInFile);
        for (String arg : staticArgs) {
            if (arg != null) {
                LookupElementBuilder item = LookupElementBuilder.create(arg);
                resultSet.addElement(item);
            }
        }
    }

    private void addStepsInScenarios(PsiFile specFile, List<SpecStep> stepsInFile) {
        List<SpecScenario> scenarios = PsiTreeUtil.getChildrenOfTypeAsList(specFile, SpecScenario.class);
        for (SpecScenario scenario : scenarios) {
            stepsInFile.addAll(scenario.getStepList());
        }
    }

    private void addContextSteps(SpecDetail specDetail, List<SpecStep> stepsInFile) {
        if (specDetail != null)
            stepsInFile.addAll(specDetail.getContextSteps());
    }

    private Set<String> getArgsFromSteps(List<SpecStep> steps) {
        Set<String> staticArgs = new HashSet<>();
        for (SpecStep step : steps) {
            List<SpecStaticArg> args = step.getStaticArgList();
            args.stream().filter(arg -> arg.getText() != null).forEach((arg) -> staticArgs.add(arg.getText()));
        }
        return staticArgs;
    }
}
