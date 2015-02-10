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

import java.util.List;

import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.thoughtworks.gauge.reference.ConceptReference;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.thoughtworks.gauge.language.token.ConceptTokenTypes.*;
import com.thoughtworks.gauge.language.psi.*;
import com.thoughtworks.gauge.StepValue;

public class ConceptStepImpl extends ConceptNamedElementImpl implements ConceptStep {

  private final boolean isConcept;

  public ConceptStepImpl(ASTNode node) {
    super(node);
    this.isConcept = false;
  }

  public ConceptStepImpl(ASTNode node, boolean isConcept) {
    super(node);
    this.isConcept = isConcept;
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ConceptVisitor) ((ConceptVisitor)visitor).visitStep(this);
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
    return new ConceptReference(this);
  }

  @Override
  public String toString() {
    return this.isConcept ? this.getStepValue().getStepAnnotationText() : super.toString();
  }
}
