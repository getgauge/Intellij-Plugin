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

import com.intellij.find.findUsages.AbstractFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class StepFindUsagesHandler extends FindUsagesHandler {
    protected StepFindUsagesHandler(PsiElement psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<PsiReference> findReferencesToHighlight(PsiElement psiElement, SearchScope searchScope) {
        return super.findReferencesToHighlight(psiElement, searchScope);
    }

    @Override
    protected boolean isSearchForTextOccurencesAvailable(PsiElement psiElement, boolean b) {
        return super.isSearchForTextOccurencesAvailable(psiElement, b);
    }

    @Nullable
    @Override
    protected Collection<String> getStringsToSearch(PsiElement psiElement) {
        return super.getStringsToSearch(psiElement);
    }

    @Override
    public boolean processUsagesInText(PsiElement psiElement, Processor<UsageInfo> processor, GlobalSearchScope globalSearchScope) {
        return super.processUsagesInText(psiElement, processor, globalSearchScope);
    }

    @Override
    public boolean processElementUsages(PsiElement psiElement, Processor<UsageInfo> processor, FindUsagesOptions findUsagesOptions) {
        return super.processElementUsages(psiElement, processor, findUsagesOptions);
    }

    @NotNull
    @Override
    public FindUsagesOptions getFindUsagesOptions(DataContext dataContext) {
        return super.getFindUsagesOptions(dataContext);
    }

    @NotNull
    @Override
    public FindUsagesOptions getFindUsagesOptions() {
        return super.getFindUsagesOptions();
    }

    @NotNull
    @Override
    public PsiElement[] getSecondaryElements() {
        return super.getSecondaryElements();
    }

    @NotNull
    @Override
    public PsiElement[] getPrimaryElements() {
        return super.getPrimaryElements();
    }

    @NotNull
    @Override
    public AbstractFindUsagesDialog getFindUsagesDialog(boolean b, boolean b1, boolean b2) {
        return super.getFindUsagesDialog(b, b1, b2);
    }
}
