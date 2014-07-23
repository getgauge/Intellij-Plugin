package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.language.psi.*;
import com.thoughtworks.gauge.reference.StepReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpecStepImpl extends SpecNamedElementImpl implements SpecStep {

    public SpecStepImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof SpecVisitor) ((SpecVisitor) visitor).visitStep(this);
        else super.accept(visitor);
    }

    @Override
    public StepValue getStepValue() {
        return SpecPsiImplUtil.getStepValue(this);
    }

    public String getName() {
        return SpecPsiImplUtil.getStepValue(this).getStepText();
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

    @Override
    @Nullable
    public SpecTable getInlineTable() {
        return findChildByClass(SpecTable.class);
    }

    @Override
    @NotNull
    public List<SpecArg> getArgList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, SpecArg.class);
    }

    @Override
    public List<SpecStaticArg> getStaticArgList() {
        List<SpecArg> argList = getArgList();

        List<SpecStaticArg> specStaticArgs = new ArrayList<SpecStaticArg>();
        for (SpecArg arg : argList) {
            SpecStaticArg staticArg = PsiTreeUtil.getChildOfType(arg, SpecStaticArg.class);
            if (staticArg != null) {
                specStaticArgs.add(staticArg);
            }
        }
        return specStaticArgs;
    }

    @Override
    public PsiReference getReference() {
        return new StepReference(this);
    }
}
