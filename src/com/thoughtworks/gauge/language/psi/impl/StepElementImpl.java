package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.language.psi.SpecPsiImplUtil;
import com.thoughtworks.gauge.language.psi.StepElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StepElementImpl extends SpecNamedElementImpl implements StepElement {

    public StepElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getStepName() {
        return SpecPsiImplUtil.getStepName(this);
    }

    public String getName() {
        return SpecPsiImplUtil.getStepName(this);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String s) {
        return null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return SpecPsiImplUtil.getPresentation(this);
    }
}
