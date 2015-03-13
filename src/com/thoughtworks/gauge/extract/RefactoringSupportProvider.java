package com.thoughtworks.gauge.extract;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.Nullable;

public class RefactoringSupportProvider extends com.intellij.lang.refactoring.RefactoringSupportProvider {
    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceConstantHandler() {
        return new ExtractConceptHandler();
    }
}
