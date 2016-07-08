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
    public void processQuery(@NotNull final ReferencesSearch.SearchParameters searchParameters, @NotNull final Processor<PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (!helper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())) return;
            if (EventQueue.isDispatchThread())
                ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {
                    @Override
                    public void run() {
                        processElements(searchParameters, processor);
                    }
                }, "Find Usages", true, searchParameters.getElementToSearch().getProject());
            else
                processElements(searchParameters, processor);
        });
    }

    private void processElements(final ReferencesSearch.SearchParameters searchParameters, final Processor<PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                StepCollector collector = helper.getStepCollector(searchParameters.getElementToSearch());
                collector.collect();
                final List<PsiElement> elements = helper.getPsiElements(collector, searchParameters.getElementToSearch());
                for (PsiElement element : elements)
                    processor.process(element.getReference());
            }
        });
    }
}