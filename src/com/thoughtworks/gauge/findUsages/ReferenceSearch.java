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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    private PsiElement elementToSearch;

    @Override
    public void processQuery(@NotNull final ReferencesSearch.SearchParameters searchParameters, @NotNull final Processor<PsiReference> processor) {
        elementToSearch = searchParameters.getElementToSearch();
        final Class<? extends PsiElement> elementClass = searchParameters.getElementToSearch().getClass();
        Boolean shouldFindUsages = elementClass.equals(ConceptStepImpl.class) || elementClass.equals(SpecStepImpl.class) ||
                elementClass.equals(PsiAnnotationImpl.class) || elementClass.equals(PsiMethodImpl.class);
        if (searchParameters.getScope().getDisplayName().equals("<unknown scope>") || !shouldFindUsages) return;
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                StepsCollection allSteps = new StepCollector(elementToSearch.getProject()).getAllSteps();
                Collection<ConceptStepImpl> conceptSteps = allSteps.getConceptSteps();
                Collection<SpecStepImpl> specSteps = allSteps.getSpecSteps();
                if (elementClass.equals(ConceptStepImpl.class))
                    handleConceptStep(conceptSteps, specSteps, processor);
                else if (elementClass.equals(PsiMethodImpl.class))
                    handleAnnotation(conceptSteps, specSteps, processor);
                else if (elementClass.equals(SpecStepImpl.class))
                    handleSpecStep(conceptSteps, specSteps, processor);
            }
        });
    }

    private void handleSpecStep(Collection<ConceptStepImpl> conceptSteps, Collection<SpecStepImpl> specSteps, Processor<PsiReference> processor) {
        String text = getStepText((SpecStepImpl) elementToSearch);
        for (ConceptStepImpl conceptStep : conceptSteps)
            process(processor, conceptStep, text.equals(getConceptStepText(conceptStep)));
        text = ((SpecStepImpl) elementToSearch).getName();
        for (final SpecStep specStep : specSteps)
            process(processor, specStep, text != null && specStep.getName() != null && specStep.getName().trim().equals(text.trim()));
    }

    private void handleAnnotation(Collection<ConceptStepImpl> conceptSteps, Collection<SpecStepImpl> specSteps, Processor<PsiReference> processor) {
        List<String> steps = StepUtil.getGaugeStepAnnotationValues((PsiMethod) elementToSearch);
        for (ConceptStepImpl conceptStep : conceptSteps)
            handleAnnotation(processor, conceptStep, getConceptStepText(conceptStep), steps);
        for (final SpecStep specStep : specSteps)
            handleAnnotation(processor, specStep, getStepText(specStep), steps);
    }

    private void handleConceptStep(Collection<ConceptStepImpl> conceptSteps, Collection<SpecStepImpl> specSteps, Processor<PsiReference> processor) {
        String conceptStepText = getConceptStepText((ConceptStepImpl) elementToSearch);
        for (ConceptStepImpl conceptStep : conceptSteps)
            process(processor, conceptStep, conceptStepText.equals(getConceptStepText(conceptStep)));
        for (final SpecStep specStep : specSteps)
            process(processor, specStep, conceptStepText.equals(getStepText(specStep)));
    }

    private void process(Processor<PsiReference> processor, PsiNamedElement conceptStep, boolean equals) {
        if (equals) processor.process(conceptStep.getReference());
    }

    private String getConceptStepText(ConceptStepImpl elementToSearch) {
        String text = elementToSearch.getStepValue().getStepText().trim();
        return !text.equals("") && text.charAt(0) == '*' || text.charAt(0) == '#' ? text.substring(1).trim() : text;
    }

    private String getStepText(SpecStep elementToSearch) {
        return elementToSearch.getStepValue().getStepText().trim();
    }

    private void handleAnnotation(Processor<PsiReference> processor, PsiNamedElement conceptStep, String text, List<String> steps) {
        for (String step : steps)
            process(processor, conceptStep, text.equals(getStepText(step)));
    }

    private String getStepText(String text) {
        return SpecPsiImplUtil.getStepValueFor(elementToSearch, text.charAt(0) == '"' ? text.substring(1, text.length() - 1) : text, false).getStepText();
    }
}