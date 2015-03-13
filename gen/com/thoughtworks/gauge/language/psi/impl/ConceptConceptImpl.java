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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConceptConceptImpl extends ASTWrapperPsiElement implements ConceptConcept {

  public ConceptConceptImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ConceptVisitor) ((ConceptVisitor)visitor).visitConcept(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ConceptConceptHeading getConceptHeading() {
    return findNotNullChildByClass(ConceptConceptHeading.class);
  }

  @Override
  @NotNull
  public List<ConceptStep> getStepList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ConceptStep.class);
  }

   public StepValue getStepValue() {
       return ConceptPsiImplUtil.getStepValue(this);
   }

}
