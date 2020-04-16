/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.findUsages;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.thoughtworks.gauge.findUsages.helper.ReferenceSearchHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class ReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    private ReferenceSearchHelper helper;

    public ReferenceSearch(ReferenceSearchHelper helper) {
        super();
        this.helper = helper;
    }

    public ReferenceSearch() {
        this.helper = new ReferenceSearchHelper();
    }

    public ReferenceSearch(boolean requireReadAction) {
        super(requireReadAction);
        this.helper = new ReferenceSearchHelper();
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters searchParameters, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (!helper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())) return;
            if (EventQueue.isDispatchThread())
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> processElements(searchParameters, processor), "Find Usages", true, searchParameters.getElementToSearch().getProject());
            else
                processElements(searchParameters, processor);
        });
    }

    private void processElements(final ReferencesSearch.SearchParameters searchParameters, final Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            StepCollector collector = helper.getStepCollector(searchParameters.getElementToSearch());
            collector.collect();
            final List<PsiElement> elements = helper.getPsiElements(collector, searchParameters.getElementToSearch());
            for (PsiElement element : elements)
                processor.process(element.getReference());
        });
    }
}
