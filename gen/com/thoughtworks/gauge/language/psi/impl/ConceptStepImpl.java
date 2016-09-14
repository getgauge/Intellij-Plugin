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

// This is a generated file. Not intended for manual editing.
package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.psi.*;
import com.thoughtworks.gauge.reference.ConceptReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConceptStepImpl extends ConceptNamedElementImpl implements ConceptStep {

    private final boolean isConcept;
    private ModuleHelper helper;

    public ConceptStepImpl(ASTNode node) {
        super(node);
        this.isConcept = false;
        this.helper = new ModuleHelper();
    }

    public ConceptStepImpl(ASTNode node, ModuleHelper helper) {
        super(node);
        this.isConcept = false;
        this.helper = helper;
    }

    public ConceptStepImpl(ASTNode node, boolean isConcept) {
        super(node);
        this.isConcept = isConcept;
        this.helper = new ModuleHelper();
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof ConceptVisitor) ((ConceptVisitor) visitor).visitStep(this);
        else super.accept(visitor);
    }

    @Override
    @NotNull
    public List<ConceptArg> getArgList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ConceptArg.class);
    }

    @Override
    @Nullable
    public ConceptTable getTable() {
        return findChildByClass(ConceptTable.class);
    }

    public StepValue getStepValue() {
        return ConceptPsiImplUtil.getStepValue(this);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiReference getReference() {
        return helper.isGaugeModule(this) ? new ConceptReference(this) : null;
    }

    @Override
    public String toString() {
        return this.isConcept ? this.getStepValue().getStepAnnotationText() : super.toString();
    }
}
