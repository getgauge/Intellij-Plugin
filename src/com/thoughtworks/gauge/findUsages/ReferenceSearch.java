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
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    @Override
    public void processQuery(@NotNull final ReferencesSearch.SearchParameters searchParameters, @NotNull final Processor<PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
            checkInConceptFiles(searchParameters, processor);
            checkInSpecFiles(searchParameters, processor);
            }
        });
    }

    private void checkInSpecFiles(ReferencesSearch.SearchParameters searchParameters, Processor<PsiReference> processor) {
        Collection<SpecStep> specSteps = getSpecSteps(searchParameters.getElementToSearch());
        for (final SpecStep specStep : specSteps) {
            final PsiElement elementToSearch = searchParameters.getElementToSearch();
            if (elementToSearch.getClass().equals(PsiAnnotationImpl.class)) {
                handleAnnotation(searchParameters, processor, specStep, replaceParamValues(getStepAnnotationText(specStep)));
                continue;
            }
            final String text = ((PsiNamedElement) elementToSearch).getName();
            if (elementToSearch.getClass().equals(ConceptStepImpl.class) && replaceParamValues(getConceptStepText(((ConceptStepImpl) elementToSearch))).equals(specStep.getStepValue().getStepAnnotationText().trim()))
                processor.process(specStep.getReference());
            if (text != null && specStep.getName() != null && specStep.getName().trim().equals(text.trim()))
                processor.process(specStep.getReference());
        }
    }

    private String getConceptStepText(ConceptStepImpl elementToSearch) {
        String text = elementToSearch.getStepValue().getStepAnnotationText().trim();
        if (!text.equals("") && text.charAt(0) == '*' || text.charAt(0) == '#')
            return text.substring(1).trim();
        return text;
    }

    private void checkInConceptFiles(ReferencesSearch.SearchParameters searchParameters, Processor<PsiReference> processor) {
        Collection<ConceptStepImpl> conceptSteps = getConceptSteps(searchParameters.getElementToSearch());
        if (searchParameters.getElementToSearch().getClass().equals(ConceptStepImpl.class))
            for (ConceptStepImpl conceptStep : conceptSteps)
                if (replaceParamValues(getConceptStepText((ConceptStepImpl) searchParameters.getElementToSearch())).equals(replaceParamValues(getConceptStepText(conceptStep))))
                    processor.process(conceptStep.getReference());
        if (searchParameters.getElementToSearch().getClass().equals(PsiAnnotationImpl.class))
            for (ConceptStepImpl conceptStep : conceptSteps)
                handleAnnotation(searchParameters, processor, conceptStep, getConceptStepText(conceptStep));
        if (searchParameters.getElementToSearch().getClass().equals(SpecStepImpl.class))
            for (ConceptStepImpl conceptStep : conceptSteps)
                if (getStepAnnotationText(((SpecStepImpl) searchParameters.getElementToSearch())).equals(getConceptStepText(conceptStep)))
                    processor.process(conceptStep.getReference());
    }

    private String getStepAnnotationText(SpecStep elementToSearch) {
        return elementToSearch.getStepValue().getStepAnnotationText();
    }

    private void handleAnnotation(ReferencesSearch.SearchParameters searchParameters, Processor<PsiReference> processor, PsiNamedElement conceptStep, String text) {
        Collection<PsiNameValuePair> psiNameValuePairs = PsiTreeUtil.collectElementsOfType(searchParameters.getElementToSearch(), PsiNameValuePair.class);
        String stepText = replaceParamValues(text);
        for (PsiNameValuePair psiNameValuePair : psiNameValuePairs)
            if (stepText.equals(replaceParamValues(removeQuotes(psiNameValuePair.getText()))))
                processor.process(conceptStep.getReference());
    }

    private String replaceParamValues(String conceptStepText) {
        return conceptStepText.replaceAll("<[^>]*>", "<>").trim();
    }

    private String removeQuotes(String text) {
        return text.substring(1, text.length() - 1);
    }

    private Collection<SpecStep> getSpecSteps(PsiElement element) {
        PsiDirectory directory = getPsiDirectory(element, element.getContainingFile().getContainingDirectory());
        return PsiTreeUtil.collectElementsOfType(directory, SpecStep.class);
    }

    private Collection<ConceptStepImpl> getConceptSteps(PsiElement element) {
        PsiDirectory directory = getPsiDirectory(element, element.getContainingFile().getContainingDirectory());
        return PsiTreeUtil.collectElementsOfType(directory, ConceptStepImpl.class);
    }

    private PsiDirectory getPsiDirectory(PsiElement element, PsiDirectory directory) {
        if (directory.getParentDirectory() == null)
            return null;
        if (element.getProject().getName().equals(directory.getParentDirectory().getName()))
            return directory.getParentDirectory();
        return getPsiDirectory(element, directory.getParentDirectory());
    }
}