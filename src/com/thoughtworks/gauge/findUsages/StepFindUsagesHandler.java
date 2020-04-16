/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.findUsages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.findUsages.JavaFindUsagesHandler;
import com.intellij.find.findUsages.JavaFindUsagesHandlerFactory;
import com.intellij.ide.util.SuperMethodWarningUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

public class StepFindUsagesHandler extends FindUsagesHandler {

    protected StepFindUsagesHandler(@NotNull PsiElement psiElement) {
        super(psiElement);
    }

    @Override
    public boolean processElementUsages(final PsiElement psiElement, final Processor<? super UsageInfo> processor, final FindUsagesOptions findUsagesOptions) {
        ApplicationManager.getApplication().invokeLater(() -> runFindUsageReadAction(psiElement, processor, findUsagesOptions));
        return true;
    }

    private void runFindUsageReadAction(final PsiElement psiElement, final Processor<? super UsageInfo> processor, final FindUsagesOptions findUsagesOptions) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (psiElement instanceof PsiMethod) {
                PsiMethod[] psiMethods = SuperMethodWarningUtil.checkSuperMethods((PsiMethod) psiElement, CustomFUH.getActionString());
                if (psiMethods.length < 1) return;
                for (PsiElement method : psiMethods)
                    StepFindUsagesHandler.this.processUsages(method, processor, findUsagesOptions);
            }
            StepFindUsagesHandler.this.processUsages(psiElement, processor, findUsagesOptions);
        });
    }

    public boolean processUsages(final PsiElement psiElement, final Processor<? super UsageInfo> processor, final FindUsagesOptions findUsagesOptions) {
        return super.processElementUsages(psiElement, processor, findUsagesOptions);
    }

    private static class CustomFUH extends JavaFindUsagesHandler {
        public CustomFUH(@NotNull PsiElement psiElement, @NotNull JavaFindUsagesHandlerFactory javaFindUsagesHandlerFactory) {
            super(psiElement, javaFindUsagesHandlerFactory);
        }

        public static String getActionString() {
            return ACTION_STRING;
        }
    }
}
