/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.findUsages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.Nullable;

public class CustomFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
    @Override
    public boolean canFindUsages(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod)
            return StepUtil.getGaugeStepAnnotationValues((PsiMethod) psiElement).size() > 0;
        return psiElement instanceof SpecStepImpl || psiElement instanceof ConceptStepImpl;
    }

    @Nullable
    @Override
    public FindUsagesHandler createFindUsagesHandler(PsiElement psiElement, boolean b) {
        return new StepFindUsagesHandler(psiElement);
    }
}
