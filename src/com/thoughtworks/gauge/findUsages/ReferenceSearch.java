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

package com.thoughtworks.gauge.findUsages;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    @Override
    public void processQuery(@NotNull final ReferencesSearch.SearchParameters searchParameters, @NotNull final Processor<PsiReference> processor) {
        if (!ProgressManager.getInstance().hasProgressIndicator())
            ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                @Override
                public void run() {
                    processElements(searchParameters, processor);
                }
            }, "Find Usages", true, searchParameters.getElementToSearch().getProject());
        else
            processElements(searchParameters, processor);
    }

    public static List<PsiElement> getPsiElements(StepCollector collector, PsiElement element) {
        List<PsiElement> elements = new ArrayList<PsiElement>();
        if (element.getClass().equals(ConceptStepImpl.class))
            elements = collector.get(getConceptStepText((ConceptStepImpl) element));
        else if (element.getClass().equals(PsiMethodImpl.class))
            for (String alias : StepUtil.getGaugeStepAnnotationValues((PsiMethod) element))
                elements.addAll(collector.get(getStepText(alias, element)));
        else if (element.getClass().equals(SpecStepImpl.class)) {
            elements = collector.get(getStepText((SpecStepImpl) element));
            elements.addAll(collector.get(((SpecStepImpl) element).getName()));
        }
        return elements;
    }

    private boolean shouldFindUsages(@NotNull ReferencesSearch.SearchParameters searchParameters, PsiElement element) {
        return !searchParameters.getScope().getDisplayName().equals("<unknown scope>") && GaugeUtil.isGaugeElement(element);
    }

    private void processElements(final ReferencesSearch.SearchParameters searchParameters, final Processor<PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                if (!shouldFindUsages(searchParameters, searchParameters.getElementToSearch())) return;
                StepCollector collector = new StepCollector(searchParameters.getElementToSearch().getProject());
                collector.collect();
                final List<PsiElement> elements = getPsiElements(collector, searchParameters.getElementToSearch());
                for (PsiElement element : elements)
                    processor.process(element.getReference());
            }
        });
    }

    private static String getConceptStepText(ConceptStepImpl element) {
        String text = element.getStepValue().getStepText().trim();
        return !text.equals("") && text.charAt(0) == '*' || text.charAt(0) == '#' ? text.substring(1).trim() : text;
    }

    private static String getStepText(SpecStep elementToSearch) {
        return elementToSearch.getStepValue().getStepText().trim();
    }

    private static String getStepText(String text, PsiElement element) {
        return SpecPsiImplUtil.getStepValueFor(element, text.charAt(0) == '"' ? text.substring(1, text.length() - 1) : text, false).getStepText();
    }
}